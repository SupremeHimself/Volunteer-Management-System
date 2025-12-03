package com.fstgc.vms.service;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.repository.AttendanceRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class AttendanceService {
    private final AttendanceRepository repository;

    public AttendanceService(AttendanceRepository repository) { this.repository = repository; }

    public Attendance checkIn(int volunteerId, int eventId, LocalDateTime time) {
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
    
    public List<Attendance> listAll() { return repository.findAll(); }
}
