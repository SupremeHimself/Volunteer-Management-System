package com.fstgc.vms.controller;

import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.service.TimesheetService;
import java.time.LocalDate;

public class TimesheetController {
    private final TimesheetService service;

    public TimesheetController(TimesheetService service) {
        this.service = service;
    }

    /**
     * Generate a timesheet for a volunteer for a specific period
     */
    public Timesheet generate(int volunteerId, LocalDate start, LocalDate end) {
        return service.generate(volunteerId, start, end);
    }

    /**
     * Submit a timesheet for a specific period with a status
     */
    public Timesheet submit(int volunteerId, LocalDate start, LocalDate end, 
                          com.fstgc.vms.model.enums.TimesheetStatus status) {
        return service.submit(volunteerId, start, end, status);
    }

    /**
     * Approve a timesheet
     */
    public Timesheet approve(int timesheetId, int adminId) {
        return service.approve(timesheetId, adminId);
    }

    /**
     * Reject a timesheet with a reason
     */
    public Timesheet reject(int timesheetId, int adminId, String reason) {
        return service.reject(timesheetId, adminId, reason);
    }

    /**
     * Submit a timesheet for a specific event
     */
    public Timesheet submitForEvent(int volunteerId, int eventId, String eventName) {
        return service.submitForEvent(volunteerId, eventId, eventName);
    }

    /**
     * Update an existing timesheet
     */
    public void update(Timesheet timesheet) {
        service.update(timesheet);
    }

    /**
     * Delete a timesheet by ID
     */
    public boolean delete(int timesheetId) {
        return service.delete(timesheetId);
    }

    /**
     * Get a list of all timesheets
     */
    public java.util.List<Timesheet> listAll() {
        return service.listAll();
    }
}
