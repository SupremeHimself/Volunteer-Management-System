package com.fstgc.vms.repository;

import com.fstgc.vms.model.Event;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Optional<Event> findById(int id);
    List<Event> findAll();
    List<Event> findByDateRange(LocalDate start, LocalDate end);
    List<Event> findByType(String type);
    List<Event> findByStatus(String status);
    List<Event> searchByTitle(String title);
    Event save(Event event);
    Event update(Event event);
    boolean delete(int id);
}
