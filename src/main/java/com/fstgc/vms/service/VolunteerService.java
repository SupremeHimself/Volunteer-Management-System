package com.fstgc.vms.service;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.model.enums.VolunteerStatus;
import com.fstgc.vms.repository.VolunteerRepository;
import java.util.List;
import java.util.Optional;

public class VolunteerService {
    private final VolunteerRepository repository;

    public VolunteerService(VolunteerRepository repository) {
        this.repository = repository;
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

    public Volunteer update(Volunteer v) { return repository.update(v); }
    
    public Volunteer update(Volunteer v, String modifiedBy) {
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
}
