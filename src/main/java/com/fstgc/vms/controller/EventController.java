package com.fstgc.vms.controller;

import com.fstgc.vms.model.Event;
import com.fstgc.vms.model.enums.EventType;
import com.fstgc.vms.service.EventService;
import java.time.LocalDate;

public class EventController {
    private final EventService service;

    public EventController(EventService service) { this.service = service; }

    public Event create(String title, LocalDate date, int capacity, EventType type, String location) {
        Event e = new Event();
        e.setTitle(title);
        e.setEventDate(date);
        e.setCapacity(capacity);
        e.setEventType(type);
        e.setLocation(location);
        return service.create(e);
    }
    
    public Event get(int eventId) {
        return service.get(eventId);
    }
    
    public void update(Event event) {
        service.update(event);
    }
    
    public boolean delete(int eventId) {
        return service.delete(eventId);
    }
    
    public java.util.List<Event> listAll() { return service.listAll(); }
}
