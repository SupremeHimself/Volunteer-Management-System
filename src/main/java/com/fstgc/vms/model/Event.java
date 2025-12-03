package com.fstgc.vms.model;

import com.fstgc.vms.model.enums.EventStatus;
import com.fstgc.vms.model.enums.EventType;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private int eventId;
    private String title;
    private String description;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private int capacity;
    private int currentRegistrations;
    private EventType eventType;
    private EventStatus status = EventStatus.DRAFT;
    private int createdByAdminId;

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getCurrentRegistrations() { return currentRegistrations; }
    public void setCurrentRegistrations(int currentRegistrations) { this.currentRegistrations = currentRegistrations; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
    public int getCreatedByAdminId() { return createdByAdminId; }
    public void setCreatedByAdminId(int createdByAdminId) { this.createdByAdminId = createdByAdminId; }
}
