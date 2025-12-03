package com.fstgc.vms.service;

import com.fstgc.vms.model.SystemAdmin;
import com.fstgc.vms.model.enums.AccountStatus;
import com.fstgc.vms.model.enums.Role;
import com.fstgc.vms.repository.AdminRepository;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

public class AuthenticationService {
    private final AdminRepository adminRepository;
    private SystemAdmin currentUser;

    public AuthenticationService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        // Create default admin if none exists
        initializeDefaultAdmin();
    }

    private void initializeDefaultAdmin() {
        Optional<SystemAdmin> existing = adminRepository.findByUsername("admin@vms.com");
        if (existing.isEmpty()) {
            SystemAdmin admin = new SystemAdmin();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@vms.com");
            admin.setPhone("555-0100");
            admin.setPasswordHash(hashPassword("admin123"));
            admin.setRole(Role.SUPER_ADMIN);
            admin.setAccountStatus(AccountStatus.ACTIVE);
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
            adminRepository.updatePassword(admin.getId(), admin.getPasswordHash());
            return false;
        }

        // Successful login
        admin.setFailedLoginAttempts(0);
        admin.setAccountLockedUntil(null);
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.updatePassword(admin.getId(), admin.getPasswordHash());
        
        this.currentUser = admin;
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

    public boolean register(String firstName, String lastName, String email, String phone, String password) {
        // Check if email already exists
        Optional<SystemAdmin> existing = adminRepository.findByUsername(email);
        if (existing.isPresent()) {
            return false;
        }

        // Create new admin account
        SystemAdmin admin = new SystemAdmin();
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPhone(phone);
        admin.setPasswordHash(hashPassword(password));
        admin.setRole(Role.VOLUNTEER); // New signups get VOLUNTEER role
        admin.setAccountStatus(AccountStatus.ACTIVE);
        adminRepository.save(admin);
        return true;
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
}
