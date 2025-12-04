package com.fstgc.vms.controller;

import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.service.TimesheetService;
import java.time.LocalDate;

public class TimesheetController {
    private final TimesheetService service;

    public TimesheetController(TimesheetService service) { this.service = service; }

    public Timesheet generate(int volunteerId, LocalDate start, LocalDate end) { return service.generate(volunteerId, start, end); }
    public Timesheet submit(int volunteerId, LocalDate start, LocalDate end, com.fstgc.vms.model.enums.TimesheetStatus status) { 
        return service.submit(volunteerId, start, end, status); 
    }
    public Timesheet approve(int timesheetId, int adminId) { return service.approve(timesheetId, adminId); }
    public Timesheet reject(int timesheetId, int adminId, String reason) { return service.reject(timesheetId, adminId, reason); }
    public Timesheet submitForEvent(int volunteerId, int eventId, String eventName) { return service.submitForEvent(volunteerId, eventId, eventName); }
    public void update(Timesheet timesheet) { service.update(timesheet); }
    public boolean delete(int timesheetId) { return service.delete(timesheetId); }
    public java.util.List<Timesheet> listAll() { return service.listAll(); }
}
