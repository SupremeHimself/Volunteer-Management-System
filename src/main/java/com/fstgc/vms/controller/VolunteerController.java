package com.fstgc.vms.controller;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.model.enums.VolunteerStatus;
import com.fstgc.vms.service.VolunteerService;

import java.util.List;
import java.util.Optional;

public class VolunteerController {
    private final VolunteerService service;

    public VolunteerController(VolunteerService service) {
        this.service = service;
    }

    /**
     * Register a new volunteer with basic information
     */
    public Volunteer register(String firstName, String lastName, String email, String phone) {
        Volunteer volunteer = new Volunteer();
        volunteer.setFirstName(firstName);
        volunteer.setLastName(lastName);
        volunteer.setEmail(email);
        volunteer.setPhone(phone);
        return service.register(volunteer);
    }

    /**
     * Register a volunteer using a pre-built Volunteer object
     */
    public Volunteer register(Volunteer volunteer) {
        return service.register(volunteer);
    }

    /**
     * Get a volunteer by their ID
     */
    public Optional<Volunteer> get(int id) {
        return service.get(id);
    }

    /**
     * Update an existing volunteer's information
     */
    public Volunteer update(Volunteer volunteer) {
        return service.update(volunteer);
    }

    /**
     * Get a list of all volunteers
     */
    public List<Volunteer> list() {
        return service.list();
    }

    /**
     * Get a list of all volunteers (alias for list())
     */
    public List<Volunteer> listAll() {
        return service.list();
    }

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
    
    public void updateVolunteerTier(int volunteerId) {
        service.updateVolunteerTier(volunteerId);
    }
    
    public double getTotalHoursWorked(int volunteerId) {
        return service.getTotalHoursWorked(volunteerId);
    }
}
