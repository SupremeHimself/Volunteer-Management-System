package com.fstgc.vms.controller;

import com.fstgc.vms.model.Event;
import com.fstgc.vms.model.enums.EventType;
import com.fstgc.vms.service.EventService;
import java.time.LocalDate;

public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    /**
     * Create a new event with the specified details
     */
    public Event create(String title, LocalDate date, int capacity, EventType type, String location) {
        Event event = new Event();
        event.setTitle(title);
        event.setEventDate(date);
        event.setCapacity(capacity);
        event.setEventType(type);
        event.setLocation(location);
        return service.create(event);
    }
    
    /**
     * Get an event by its ID
     */
    public Event get(int eventId) {
        return service.get(eventId);
    }
    
    /**
     * Update an existing event
     */
    public void update(Event event) {
        service.update(event);
    }
    
    /**
     * Delete an event by ID
     */
    public boolean delete(int eventId) {
        return service.delete(eventId);
    }
    
    /**
     * Get a list of all events
     */
    public java.util.List<Event> listAll() {
        return service.listAll();
    }
}
