package com.fstgc.vms.controller;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.service.AttendanceService;

public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    /**
     * Record attendance for a volunteer at an event
     */
    public Attendance recordAttendance(int volunteerId, int eventId, double hoursWorked) {
        return service.recordAttendance(volunteerId, eventId, hoursWorked);
    }
    
    /**
     * Update the status of an attendance record
     */
    public Attendance updateStatus(int attendanceId, com.fstgc.vms.model.enums.AttendanceStatus status) {
        return service.updateStatus(attendanceId, status);
    }
    
    /**
     * Get all attendance records for a specific volunteer
     */
    public java.util.List<Attendance> byVolunteer(int volunteerId) {
        return service.byVolunteer(volunteerId);
    }
    
    /**
     * Get an attendance record by its ID
     */
    public Attendance byId(int attendanceId) {
        return service.byId(attendanceId);
    }
    
    /**
     * Update an existing attendance record
     */
    public Attendance update(Attendance attendance) {
        return service.update(attendance);
    }
    
    public java.util.List<Attendance> listAll() { return service.listAll(); }
    
    public boolean isVolunteerRegisteredForEvent(int volunteerId, int eventId) {
        return service.isVolunteerRegisteredForEvent(volunteerId, eventId);
    }
}
