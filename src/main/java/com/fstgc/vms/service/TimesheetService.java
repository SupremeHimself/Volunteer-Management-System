package com.fstgc.vms.service;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.model.enums.TimesheetStatus;
import com.fstgc.vms.repository.AttendanceRepository;
import com.fstgc.vms.repository.TimesheetRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TimesheetService {
    private final TimesheetRepository timesheets;
    private final AttendanceRepository attendance;

    public TimesheetService(TimesheetRepository timesheets, AttendanceRepository attendance) {
        this.timesheets = timesheets;
        this.attendance = attendance;
    }

    public Timesheet generate(int volunteerId, LocalDate start, LocalDate end) {
        Timesheet t = new Timesheet();
        t.setVolunteerId(volunteerId);
        t.setPeriodStartDate(start);
        t.setPeriodEndDate(end);
        double total = attendance.findByVolunteer(volunteerId).stream()
                .filter(a -> a.getCheckInTime()!=null && !a.getCheckInTime().toLocalDate().isBefore(start) && a.getCheckOutTime()!=null && !a.getCheckOutTime().toLocalDate().isAfter(end))
                .mapToDouble(Attendance::getHoursWorked).sum();
        t.setTotalHours(Math.round(total*100.0)/100.0);
        return timesheets.save(t);
    }
    
    public Timesheet submit(int volunteerId, LocalDate start, LocalDate end, TimesheetStatus status) {
        Timesheet t = new Timesheet();
        t.setVolunteerId(volunteerId);
        t.setPeriodStartDate(start);
        t.setPeriodEndDate(end);
        double total = attendance.findByVolunteer(volunteerId).stream()
                .filter(a -> a.getCheckInTime()!=null && !a.getCheckInTime().toLocalDate().isBefore(start) && a.getCheckOutTime()!=null && !a.getCheckOutTime().toLocalDate().isAfter(end))
                .mapToDouble(Attendance::getHoursWorked).sum();
        t.setTotalHours(Math.round(total*100.0)/100.0);
        t.setApprovalStatus(status);
        return timesheets.save(t);
    }

    public Timesheet approve(int timesheetId, int adminId) {
        Timesheet t = timesheets.findById(timesheetId).orElseThrow();
        t.setApprovalStatus(TimesheetStatus.APPROVED);
        t.setApprovedByAdminId(adminId);
        t.setApprovedHours(t.getTotalHours());
        t.setApprovalDate(LocalDateTime.now());
        return timesheets.update(t);
    }

    public Timesheet reject(int timesheetId, int adminId, String reason) {
        Timesheet t = timesheets.findById(timesheetId).orElseThrow();
        t.setApprovalStatus(TimesheetStatus.REJECTED);
        t.setApprovedByAdminId(adminId);
        t.setRejectionReason(reason);
        t.setApprovalDate(LocalDateTime.now());
        return timesheets.update(t);
    }
}
