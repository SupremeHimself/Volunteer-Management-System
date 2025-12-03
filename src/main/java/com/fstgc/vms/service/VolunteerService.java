package com.fstgc.vms.service;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.model.enums.VolunteerStatus;
import com.fstgc.vms.repository.VolunteerRepository;
import java.util.List;
import java.util.Optional;

public class VolunteerService {
    private final VolunteerRepository repository;
    private final ValidationService validation;

    public VolunteerService(VolunteerRepository repository, ValidationService validation) {
        this.repository = repository;
        this.validation = validation;
    }

    public Volunteer register(Volunteer v) {
        if (!validation.isValidEmail(v.getEmail())) throw new IllegalArgumentException("Invalid email");
        repository.findByEmail(v.getEmail()).ifPresent(x -> { throw new IllegalArgumentException("Email already exists"); });
        if (v.getPhone()!=null && !validation.isValidPhone(v.getPhone())) throw new IllegalArgumentException("Invalid phone");
        v.setStatus(VolunteerStatus.ACTIVE);
        return repository.save(v);
    }

    public Volunteer update(Volunteer v) { return repository.update(v); }
    public boolean deactivate(int id) { return repository.findById(id).map(v -> { v.setStatus(VolunteerStatus.INACTIVE); repository.update(v); return true; }).orElse(false); }
    public Optional<Volunteer> get(int id) { return repository.findById(id); }
    public List<Volunteer> list() { return repository.findAll(); }
}
