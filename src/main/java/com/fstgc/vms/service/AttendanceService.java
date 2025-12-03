package com.fstgc.vms.service;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.model.Event;
import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.model.enums.TimesheetStatus;
import com.fstgc.vms.repository.AttendanceRepository;
import com.fstgc.vms.repository.EventRepository;
import com.fstgc.vms.repository.TimesheetRepository;
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

    public Attendance recordAttendance(int volunteerId, int eventId, double hoursWorked) {
        // Update event registration count and capacity
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        
        if (event.getCapacity() <= 0) {
            throw new IllegalArgumentException("Event is at full capacity");
        }
        
        event.setCurrentRegistrations(event.getCurrentRegistrations() + 1);
        event.setCapacity(event.getCapacity() - 1);
        eventRepository.update(event);
        
        // Create attendance record with hours
        Attendance a = new Attendance();
        a.setVolunteerId(volunteerId);
        a.setEventId(eventId);
        a.setCheckInTime(LocalDateTime.now()); // Set to current time for record keeping
        a.setHoursWorked(hoursWorked);
        Attendance savedAttendance = repository.save(a);
        
        // Automatically create a timesheet for this attendance and update hours
        createTimesheetForAttendance(volunteerId, event, hoursWorked);
        
        return savedAttendance;
    }
    
    private void createTimesheetForAttendance(int volunteerId, Event event, double hours) {
        // Create a timesheet with the event details and hours
        Timesheet timesheet = new Timesheet();
        timesheet.setVolunteerId(volunteerId);
        timesheet.setEventId(event.getEventId());
        timesheet.setEventName(event.getTitle());
        // Set period to the event date
        timesheet.setPeriodStartDate(event.getEventDate());
        timesheet.setPeriodEndDate(event.getEventDate());
        timesheet.setTotalHours(hours); // Set hours directly
        timesheet.setApprovalStatus(TimesheetStatus.PENDING);
        timesheet.setCreatedDate(LocalDateTime.now());
        
        timesheetRepository.save(timesheet);
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
    
    public boolean isVolunteerRegisteredForEvent(int volunteerId, int eventId) {
        // Check if volunteer has an attendance record for this event
        List<Attendance> attendances = repository.findByVolunteer(volunteerId);
        return attendances.stream()
            .anyMatch(a -> a.getEventId() == eventId);
    }
}
