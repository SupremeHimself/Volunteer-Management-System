package com.fstgc.vms.controller;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.model.enums.VolunteerStatus;
import com.fstgc.vms.service.VolunteerService;

import java.util.List;
import java.util.Optional;

public class VolunteerController {
    private final VolunteerService service;

    public VolunteerController(VolunteerService service) { this.service = service; }

    public Volunteer register(String firstName, String lastName, String email, String phone) {
        Volunteer v = new Volunteer();
        v.setFirstName(firstName);
        v.setLastName(lastName);
        v.setEmail(email);
        v.setPhone(phone);
        return service.register(v);
    }

    // Convenience overload in case a fully-built Volunteer is provided
    public Volunteer register(Volunteer v) {
        return service.register(v);
    }

    public Optional<Volunteer> get(int id) { return service.get(id); }

    public Volunteer update(Volunteer v) { return service.update(v); }

    public List<Volunteer> list() { return service.list(); }

    public List<Volunteer> listAll() { return service.list(); }

    public Volunteer updateVolunteer(int id,
                                     String firstName,
                                     String lastName,
                                     String email,
                                     String phone,
                                     VolunteerStatus status) {
        Volunteer existing = service.get(id)
            .orElseThrow(() -> new IllegalArgumentException("Volunteer not found: " + id));
        existing.setFirstName(firstName);
        existing.setLastName(lastName);
        existing.setEmail(email);
        existing.setPhone(phone);
        if (status != null) {
            existing.setStatus(status);
        }
        return service.update(existing);
    }

    public void delete(int id) { service.delete(id); }

    public Optional<Volunteer> changeStatus(int id, VolunteerStatus status) {
        return service.get(id);
    }
}
