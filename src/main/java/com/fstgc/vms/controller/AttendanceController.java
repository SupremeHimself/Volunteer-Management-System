package com.fstgc.vms.controller;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.service.AttendanceService;

public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) { this.service = service; }

    public Attendance recordAttendance(int volunteerId, int eventId, double hoursWorked) {
        return service.recordAttendance(volunteerId, eventId, hoursWorked);
    }
    
    public Attendance updateStatus(int attendanceId, com.fstgc.vms.model.enums.AttendanceStatus status) {
        return service.updateStatus(attendanceId, status);
    }
    
    public java.util.List<Attendance> byVolunteer(int volunteerId) {
        return service.byVolunteer(volunteerId);
    }
    
    public Attendance byId(int attendanceId) {
        return service.byId(attendanceId);
    }
    
    public Attendance update(Attendance attendance) {
        return service.update(attendance);
    }
    
    public java.util.List<Attendance> listAll() { return service.listAll(); }
    
    public boolean isVolunteerRegisteredForEvent(int volunteerId, int eventId) {
        return service.isVolunteerRegisteredForEvent(volunteerId, eventId);
    }
}
