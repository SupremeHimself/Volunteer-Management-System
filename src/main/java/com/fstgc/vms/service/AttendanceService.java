package com.fstgc.vms.service;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.model.Event;
import com.fstgc.vms.repository.AttendanceRepository;
import com.fstgc.vms.repository.EventRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class AttendanceService {
    private final AttendanceRepository repository;
    private final EventRepository eventRepository;

    public AttendanceService(AttendanceRepository repository, EventRepository eventRepository) { 
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    public Attendance checkIn(int volunteerId, int eventId, LocalDateTime time) {
        // Update event registration count and capacity
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        
        if (event.getCapacity() <= 0) {
            throw new IllegalArgumentException("Event is at full capacity");
        }
        
        event.setCurrentRegistrations(event.getCurrentRegistrations() + 1);
        event.setCapacity(event.getCapacity() - 1);
        eventRepository.update(event);
        
        // Create attendance record
        Attendance a = new Attendance();
        a.setVolunteerId(volunteerId);
        a.setEventId(eventId);
        a.setCheckInTime(time);
        return repository.save(a);
    }

    public Attendance checkOut(int attendanceId, LocalDateTime time) {
        Attendance a = repository.findById(attendanceId).orElseThrow();
        if (a.getCheckInTime()!=null && time.isBefore(a.getCheckInTime())) throw new IllegalArgumentException("Checkout before checkin");
        a.setCheckOutTime(time);
        double hours = Duration.between(a.getCheckInTime(), time).toMinutes()/60.0;
        // Round up to the nearest hour
        a.setHoursWorked(Math.max(0, Math.ceil(hours)));
        return repository.update(a);
    }

    public List<Attendance> byVolunteer(int volunteerId) { return repository.findByVolunteer(volunteerId); }
    
    public Attendance updateStatus(int attendanceId, com.fstgc.vms.model.enums.AttendanceStatus status) {
        Attendance a = repository.findById(attendanceId).orElseThrow();
        a.setStatus(status);
        return repository.update(a);
    }
    
    public boolean deleteAttendance(int attendanceId) {
        // Reverse the event registration count when attendance is deleted
        Attendance a = repository.findById(attendanceId).orElseThrow(() -> new IllegalArgumentException("Attendance not found"));
        
        Event event = eventRepository.findById(a.getEventId()).orElse(null);
        if (event != null) {
            event.setCurrentRegistrations(Math.max(0, event.getCurrentRegistrations() - 1));
            event.setCapacity(event.getCapacity() + 1);
            eventRepository.update(event);
        }
        
        return repository.delete(attendanceId);
    }
    
    public List<Attendance> listAll() { return repository.findAll(); }
}
