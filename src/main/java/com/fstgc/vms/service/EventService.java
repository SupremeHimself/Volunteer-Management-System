package com.fstgc.vms.service;

import com.fstgc.vms.model.Event;
import com.fstgc.vms.model.enums.EventStatus;
import com.fstgc.vms.repository.EventRepository;
import java.time.LocalDate;
import java.util.List;

public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) { this.repository = repository; }

    public Event create(Event e) {
        if (e.getCapacity() < 0) throw new IllegalArgumentException("Capacity must be >= 0");
        return repository.save(e);
    }
    
    public List<Event> listAll() { return repository.findAll(); }

    public Event publish(int eventId) {
        Event e = repository.findById(eventId).orElseThrow();
        e.setStatus(EventStatus.PUBLISHED);
        return repository.update(e);
    }

    public boolean cancel(int eventId) {
        return repository.findById(eventId).map(e -> { e.setStatus(EventStatus.CANCELLED); repository.update(e); return true; }).orElse(false);
    }

    public List<Event> getEventsBetween(LocalDate start, LocalDate end) { return repository.findByDateRange(start, end); }
}
