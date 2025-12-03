package com.fstgc.vms.service;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.model.Event;
import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.model.enums.TimesheetStatus;
import com.fstgc.vms.repository.AttendanceRepository;
import com.fstgc.vms.repository.EventRepository;
import com.fstgc.vms.repository.TimesheetRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class AttendanceService {
    private final AttendanceRepository repository;
    private final EventRepository eventRepository;
    private final TimesheetRepository timesheetRepository;

    public AttendanceService(AttendanceRepository repository, EventRepository eventRepository, TimesheetRepository timesheetRepository) { 
        this.repository = repository;
        this.eventRepository = eventRepository;
        this.timesheetRepository = timesheetRepository;
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
        Attendance savedAttendance = repository.save(a);
        
        // Automatically create a timesheet for this attendance
        createTimesheetForAttendance(volunteerId, event);
        
        return savedAttendance;
    }
    
    private void createTimesheetForAttendance(int volunteerId, Event event) {
        // Create a timesheet with the event details
        Timesheet timesheet = new Timesheet();
        timesheet.setVolunteerId(volunteerId);
        timesheet.setEventId(event.getEventId());
        timesheet.setEventName(event.getTitle());
        // Set period to the event date
        timesheet.setPeriodStartDate(event.getEventDate());
        timesheet.setPeriodEndDate(event.getEventDate());
        timesheet.setTotalHours(0.0); // Will be updated when hours are logged
        timesheet.setApprovalStatus(TimesheetStatus.PENDING);
        timesheet.setCreatedDate(LocalDateTime.now());
        
        timesheetRepository.save(timesheet);
    }

    public Attendance checkOut(int attendanceId, LocalDateTime time) {
        Attendance a = repository.findById(attendanceId).orElseThrow();
        if (a.getCheckInTime()!=null && time.isBefore(a.getCheckInTime())) throw new IllegalArgumentException("Checkout before checkin");
        a.setCheckOutTime(time);
        double hours = Duration.between(a.getCheckInTime(), time).toMinutes()/60.0;
        // Round up to the nearest hour
        a.setHoursWorked(Math.max(0, Math.ceil(hours)));
        
        // Update the corresponding timesheet with the hours worked
        updateTimesheetHours(a.getVolunteerId(), a.getEventId(), a.getHoursWorked());
        
        return repository.update(a);
    }
    
    private void updateTimesheetHours(int volunteerId, int eventId, double hours) {
        // Find the timesheet for this volunteer and event
        List<Timesheet> timesheets = timesheetRepository.findByVolunteer(volunteerId);
        for (Timesheet timesheet : timesheets) {
            if (timesheet.getEventId() != null && timesheet.getEventId() == eventId) {
                // Update the total hours
                timesheet.setTotalHours(Math.round((timesheet.getTotalHours() + hours) * 100.0) / 100.0);
                timesheet.setLastModifiedDate(LocalDateTime.now());
                timesheetRepository.update(timesheet);
                break;
            }
        }
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
    
    public Attendance byId(int attendanceId) {
        return repository.findById(attendanceId)
            .orElseThrow(() -> new IllegalArgumentException("Attendance not found"));
    }
    
    public Attendance update(Attendance attendance) {
        // Get the old attendance to calculate the hours difference
        Attendance oldAttendance = repository.findById(attendance.getAttendanceId()).orElse(null);
        Attendance updated = repository.update(attendance);
        
        // Update timesheet hours if hours were changed
        if (oldAttendance != null && oldAttendance.getHoursWorked() != attendance.getHoursWorked()) {
            double hoursDifference = attendance.getHoursWorked() - oldAttendance.getHoursWorked();
            updateTimesheetHoursByDifference(attendance.getVolunteerId(), attendance.getEventId(), hoursDifference);
        }
        
        return updated;
    }
    
    private void updateTimesheetHoursByDifference(int volunteerId, int eventId, double hoursDifference) {
        // Find the timesheet for this volunteer and event
        List<Timesheet> timesheets = timesheetRepository.findByVolunteer(volunteerId);
        for (Timesheet timesheet : timesheets) {
            if (timesheet.getEventId() != null && timesheet.getEventId() == eventId) {
                // Update the total hours with the difference
                timesheet.setTotalHours(Math.round((timesheet.getTotalHours() + hoursDifference) * 100.0) / 100.0);
                timesheet.setLastModifiedDate(LocalDateTime.now());
                timesheetRepository.update(timesheet);
                break;
            }
        }
    }
    
    public List<Attendance> listAll() { return repository.findAll(); }
}
