package com.fstgc.vms.service;

import com.fstgc.vms.model.SystemAdmin;
import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.model.enums.AccountStatus;
import com.fstgc.vms.model.enums.Role;
import com.fstgc.vms.repository.AdminRepository;
import com.fstgc.vms.repository.memory.InMemoryVolunteerRepository;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

public class AuthenticationService {
    private final AdminRepository adminRepository;
    // Service used to create Volunteer records for new signups so admins can manage them
    private final VolunteerService volunteerService;
    private SystemAdmin currentUser;

    public AuthenticationService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        // Separate volunteer service used only for signups; persists via DataPersistence
        this.volunteerService = new VolunteerService(new InMemoryVolunteerRepository());
        // Create default admin if none exists
        initializeDefaultAdmin();
    }

    private void initializeDefaultAdmin() {
        Optional<SystemAdmin> existing = adminRepository.findByUsername("admin");
        if (existing.isEmpty()) {
            SystemAdmin admin = new SystemAdmin();
            admin.setUsername("admin");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@vms.com");
            admin.setPhone("555-0100");
            admin.setPasswordHash(hashPassword("admin123"));
            admin.setRole(Role.SUPER_ADMIN);
            admin.setAccountStatus(AccountStatus.ACTIVE);
            admin.setSecurityQuestion("What is the name of this system?");
            admin.setSecurityAnswerHash(hashPassword("vms"));
            adminRepository.save(admin);
        }
    }

    public boolean login(String usernameOrEmail, String password) {
        Optional<SystemAdmin> adminOpt = adminRepository.findByUsername(usernameOrEmail);
        
        if (adminOpt.isEmpty()) {
            return false;
        }

        SystemAdmin admin = adminOpt.get();
        
        // Check account status
        if (admin.getAccountStatus() != AccountStatus.ACTIVE) {
            return false;
        }

        // Check if account is locked
        if (admin.getAccountLockedUntil() != null && 
            LocalDateTime.now().isBefore(admin.getAccountLockedUntil())) {
            return false;
        }

        // Validate password
        String hashedPassword = hashPassword(password);
        if (!hashedPassword.equals(admin.getPasswordHash())) {
            // Increment failed attempts
            admin.setFailedLoginAttempts(admin.getFailedLoginAttempts() + 1);
            if (admin.getFailedLoginAttempts() >= 5) {
                admin.setAccountLockedUntil(LocalDateTime.now().plusMinutes(15));
            }
            adminRepository.update(admin);
            return false;
        }

        // Successful login
        admin.setFailedLoginAttempts(0);
        admin.setAccountLockedUntil(null);
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.update(admin);
        
        this.currentUser = admin;
        
        // Auto-create volunteer record if it doesn't exist (except for SUPER_ADMIN)
        ensureVolunteerRecordExists(admin);
        
        return true;
    }

    public void logout() {
        this.currentUser = null;
    }

    public SystemAdmin getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public Role getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public boolean register(String username, String firstName, String lastName, String email, String phone, String password, String securityQuestion, String securityAnswer) {
        // Check if username or email already exists
        Optional<SystemAdmin> existingUsername = adminRepository.findByUsername(username);
        Optional<SystemAdmin> existingEmail = adminRepository.findByEmail(email);
        if (existingUsername.isPresent() || existingEmail.isPresent()) {
            return false;
        }

        // Create new admin account used for authentication
        SystemAdmin admin = new SystemAdmin();
        admin.setUsername(username);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPhone(phone);
        admin.setPasswordHash(hashPassword(password));
        admin.setRole(Role.VOLUNTEER); // New signups get VOLUNTEER role
        admin.setAccountStatus(AccountStatus.ACTIVE);
        admin.setSecurityQuestion(securityQuestion);
        admin.setSecurityAnswerHash(hashPassword(securityAnswer.trim().toLowerCase()));
        adminRepository.save(admin);

        // Also create a Volunteer domain record so admins can manage this person
        try {
            Volunteer volunteer = new Volunteer();
            volunteer.setFirstName(firstName);
            volunteer.setLastName(lastName);
            volunteer.setEmail(email);
            volunteer.setPhone(phone);
            volunteerService.register(volunteer);
        } catch (Exception e) {
            // Do not block account creation if the volunteer record fails; log to stderr instead
            System.err.println("Warning: failed to create Volunteer record for signup: " + e.getMessage());
        }

        return true;
    }

    /**
     * Ensures that a Volunteer record exists for the given SystemAdmin user.
     * Automatically creates a volunteer record if one doesn't exist (except for SUPER_ADMIN role).
     * This allows users to be registered under the volunteer tab upon login.
     */
    private void ensureVolunteerRecordExists(SystemAdmin admin) {
        // Skip volunteer creation for SUPER_ADMIN role
        if (admin.getRole() == Role.SUPER_ADMIN) {
            return;
        }
        
        try {
            // Check if volunteer record already exists by email
            Optional<Volunteer> existingVolunteer = volunteerService.getByEmail(admin.getEmail());
            
            if (existingVolunteer.isEmpty()) {
                // Create new volunteer record
                Volunteer volunteer = new Volunteer();
                volunteer.setFirstName(admin.getFirstName());
                volunteer.setLastName(admin.getLastName());
                volunteer.setEmail(admin.getEmail());
                volunteer.setPhone(admin.getPhone());
                volunteer.setLastModifiedBy("System");
                volunteerService.register(volunteer, "System");
                System.out.println("Auto-created volunteer record for: " + admin.getEmail());
            }
        } catch (Exception e) {
            // Don't block login if volunteer creation fails, just log the error
            System.err.println("Warning: Failed to auto-create volunteer record for " + admin.getEmail() + ": " + e.getMessage());
        }
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Initiates password reset process by finding user and returning their security question.
     * @param usernameOrEmail Username or email of the user
     * @return Security question if user exists, null otherwise
     */
    public String getSecurityQuestion(String usernameOrEmail) {
        Optional<SystemAdmin> adminOpt = adminRepository.findByUsername(usernameOrEmail);
        return adminOpt.map(SystemAdmin::getSecurityQuestion).orElse(null);
    }

    /**
     * Verifies security answer and resets password if answer is correct.
     * @param usernameOrEmail Username or email of the user
     * @param securityAnswer Answer to the security question
     * @param newPassword New password to set
     * @return true if password was reset successfully, false otherwise
     */
    public boolean resetPassword(String usernameOrEmail, String securityAnswer, String newPassword) {
        Optional<SystemAdmin> adminOpt = adminRepository.findByUsername(usernameOrEmail);
        
        if (adminOpt.isEmpty()) {
            return false;
        }
        
        SystemAdmin admin = adminOpt.get();
        
        // Verify security answer
        String hashedAnswer = hashPassword(securityAnswer.trim().toLowerCase());
        if (admin.getSecurityAnswerHash() == null || !hashedAnswer.equals(admin.getSecurityAnswerHash())) {
            return false;
        }
        
        // Reset password and unlock account
        admin.setPasswordHash(hashPassword(newPassword));
        admin.setFailedLoginAttempts(0);
        admin.setAccountLockedUntil(null);
        adminRepository.updatePassword(admin.getId(), admin.getPasswordHash());
        
        return true;
    }

    /**
     * Updates security question and answer for a user.
     * @param userId User ID
     * @param securityQuestion New security question
     * @param securityAnswer New security answer
     * @return true if update was successful
     */
    public boolean updateSecurityQuestion(int userId, String securityQuestion, String securityAnswer) {
        Optional<SystemAdmin> adminOpt = adminRepository.findById(userId);
        
        if (adminOpt.isEmpty()) {
            return false;
        }
        
        SystemAdmin admin = adminOpt.get();
        admin.setSecurityQuestion(securityQuestion);
        admin.setSecurityAnswerHash(hashPassword(securityAnswer.trim().toLowerCase()));
        adminRepository.update(admin);
        
        return true;
    }
}
