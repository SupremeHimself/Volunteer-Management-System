package com.fstgc.vms.model;

import com.fstgc.vms.model.enums.AttendanceStatus;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Attendance implements Serializable {
    private static final long serialVersionUID = 1L;
    private int attendanceId;
    private int volunteerId;
    private int eventId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime; // nullable
    private AttendanceStatus status;
    private double hoursWorked; // calculated
    private Integer recordedByAdminId; // nullable
    private String notes; // nullable

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public int getVolunteerId() { return volunteerId; }
    public void setVolunteerId(int volunteerId) { this.volunteerId = volunteerId; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }
    public Integer getRecordedByAdminId() { return recordedByAdminId; }
    public void setRecordedByAdminId(Integer recordedByAdminId) { this.recordedByAdminId = recordedByAdminId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
