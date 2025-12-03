package com.fstgc.vms.controller;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.service.AttendanceService;
import java.time.LocalDateTime;

public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) { this.service = service; }

    public Attendance checkIn(int volunteerId, int eventId) {
        return service.checkIn(volunteerId, eventId, LocalDateTime.now());
    }

    public Attendance checkOut(int attendanceId) {
        return service.checkOut(attendanceId, LocalDateTime.now());
    }
    
    public Attendance updateStatus(int attendanceId, com.fstgc.vms.model.enums.AttendanceStatus status) {
        return service.updateStatus(attendanceId, status);
    }
    
    public java.util.List<Attendance> listAll() { return service.listAll(); }
    
    public boolean isVolunteerRegisteredForEvent(int volunteerId, int eventId) {
        return service.isVolunteerRegisteredForEvent(volunteerId, eventId);
    }
}
