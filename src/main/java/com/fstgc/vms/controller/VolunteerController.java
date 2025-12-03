package com.fstgc.vms.controller;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.service.VolunteerService;
import java.util.List;

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
    
    public Volunteer register(Volunteer v) {
        return service.register(v);
    }
    
    public java.util.Optional<Volunteer> get(int id) {
        return service.get(id);
    }
    
    public Volunteer update(Volunteer v) {
        return service.update(v);
    }
    
    public void delete(int id) {
        service.delete(id);
    }

    public List<Volunteer> list() { return service.list(); }
    
    public List<Volunteer> listAll() { return service.list(); }
}
