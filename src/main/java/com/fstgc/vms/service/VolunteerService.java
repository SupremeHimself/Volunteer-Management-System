package com.fstgc.vms.service;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.model.enums.BadgeTier;
import com.fstgc.vms.model.enums.VolunteerStatus;
import com.fstgc.vms.repository.AttendanceRepository;
import com.fstgc.vms.repository.VolunteerRepository;
import java.util.List;
import java.util.Optional;

public class VolunteerService {
    private final VolunteerRepository repository;
    private AttendanceRepository attendanceRepository;

    public VolunteerService(VolunteerRepository repository) {
        this.repository = repository;
    }
    
    // Optional setter for attendance repository to enable tier calculation
    public void setAttendanceRepository(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Volunteer register(Volunteer v) {
        if (!isValidEmail(v.getEmail())) throw new IllegalArgumentException("Invalid email");
        repository.findByEmail(v.getEmail()).ifPresent(x -> { throw new IllegalArgumentException("Email already exists"); });
        if (v.getPhone()!=null && !isValidPhone(v.getPhone())) throw new IllegalArgumentException("Invalid phone");
        v.setStatus(VolunteerStatus.ACTIVE);
        return repository.save(v);
    }
    
    public Volunteer register(Volunteer v, String modifiedBy) {
        if (!isValidEmail(v.getEmail())) throw new IllegalArgumentException("Invalid email");
        repository.findByEmail(v.getEmail()).ifPresent(x -> { throw new IllegalArgumentException("Email already exists"); });
        if (v.getPhone()!=null && !isValidPhone(v.getPhone())) throw new IllegalArgumentException("Invalid phone");
        v.setStatus(VolunteerStatus.ACTIVE);
        v.setLastModifiedBy(modifiedBy);
        v.setLastModifiedDate(java.time.LocalDateTime.now());
        return repository.save(v);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\+?[0-9\\s-()]{10,}$");
    }

    public Volunteer update(Volunteer v) {
        // Check if email is being changed to an existing email (excluding current volunteer)
        if (v.getEmail() != null && !isValidEmail(v.getEmail())) {
            throw new IllegalArgumentException("Invalid email");
        }
        repository.findByEmail(v.getEmail()).ifPresent(existing -> {
            if (existing.getId() != v.getId()) {
                throw new IllegalArgumentException("Email already exists");
            }
        });
        if (v.getPhone() != null && !isValidPhone(v.getPhone())) {
            throw new IllegalArgumentException("Invalid phone");
        }
        return repository.update(v);
    }
    
    public Volunteer update(Volunteer v, String modifiedBy) {
        // Check if email is being changed to an existing email (excluding current volunteer)
        if (v.getEmail() != null && !isValidEmail(v.getEmail())) {
            throw new IllegalArgumentException("Invalid email");
        }
        repository.findByEmail(v.getEmail()).ifPresent(existing -> {
            if (existing.getId() != v.getId()) {
                throw new IllegalArgumentException("Email already exists");
            }
        });
        if (v.getPhone() != null && !isValidPhone(v.getPhone())) {
            throw new IllegalArgumentException("Invalid phone");
        }
        v.setLastModifiedBy(modifiedBy);
        v.setLastModifiedDate(java.time.LocalDateTime.now());
        return repository.update(v);
    }
    
    public boolean deactivate(int id) { return repository.findById(id).map(v -> { v.setStatus(VolunteerStatus.INACTIVE); repository.update(v); return true; }).orElse(false); }
    
    public boolean deactivate(int id, String modifiedBy) { 
        return repository.findById(id).map(v -> { 
            v.setStatus(VolunteerStatus.INACTIVE); 
            v.setLastModifiedBy(modifiedBy);
            v.setLastModifiedDate(java.time.LocalDateTime.now());
            repository.update(v); 
            return true; 
        }).orElse(false); 
    }
    public Optional<Volunteer> get(int id) { return repository.findById(id); }
    
    public Optional<Volunteer> getByEmail(String email) { return repository.findByEmail(email); }

    public List<Volunteer> list() { return repository.findAll(); }
    public void delete(int id) { repository.delete(id); }
    
    /**
     * Calculate and update volunteer's achievement tier based on total hours worked
     * Tiers: Bronze (10+ hours), Silver (50+ hours), Gold (100+ hours), Platinum (200+ hours)
     */
    public void updateVolunteerTier(int volunteerId) {
        if (attendanceRepository == null) {
            return; // Cannot calculate tier without attendance data
        }
        
        Optional<Volunteer> volunteerOpt = repository.findById(volunteerId);
        if (volunteerOpt.isEmpty()) {
            return;
        }
        
        Volunteer volunteer = volunteerOpt.get();
        
        // Calculate total hours from attendance records
        double totalHours = attendanceRepository.findByVolunteer(volunteerId).stream()
            .filter(a -> a.getCheckInTime() != null && a.getCheckOutTime() != null)
            .mapToDouble(a -> a.getHoursWorked())
            .sum();
        
        // Determine tier based on hours
        BadgeTier newTier = null;
        if (totalHours >= 200) {
            newTier = BadgeTier.PLATINUM;
        } else if (totalHours >= 100) {
            newTier = BadgeTier.GOLD;
        } else if (totalHours >= 50) {
            newTier = BadgeTier.SILVER;
        } else if (totalHours >= 10) {
            newTier = BadgeTier.BRONZE;
        }
        
        // Update volunteer's tier if changed
        if (newTier != volunteer.getCurrentTier()) {
            volunteer.setCurrentTier(newTier);
            repository.update(volunteer);
        }
    }
    
    /**
     * Get total hours worked by volunteer from attendance records
     */
    public double getTotalHoursWorked(int volunteerId) {
        if (attendanceRepository == null) {
            return 0.0;
        }
        
        return attendanceRepository.findByVolunteer(volunteerId).stream()
            .filter(a -> a.getCheckInTime() != null && a.getCheckOutTime() != null)
            .mapToDouble(a -> a.getHoursWorked())
            .sum();
    }
}
