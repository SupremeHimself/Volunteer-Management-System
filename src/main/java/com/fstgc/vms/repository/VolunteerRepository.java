package com.fstgc.vms.repository;

import com.fstgc.vms.model.Volunteer;
import java.util.List;
import java.util.Optional;

public interface VolunteerRepository {
    Optional<Volunteer> findById(int id);
    List<Volunteer> findAll();
    Optional<Volunteer> findByEmail(String email);
    Volunteer save(Volunteer volunteer);
    Volunteer update(Volunteer volunteer);
    boolean delete(int id);
    List<Volunteer> searchByName(String name);
    List<Volunteer> filterByStatus(String status);
}
