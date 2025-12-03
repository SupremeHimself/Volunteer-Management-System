package com.fstgc.vms.model;

import com.fstgc.vms.model.enums.TimesheetStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Timesheet implements Serializable {
    private static final long serialVersionUID = 1L;
    private int timesheetId;
    private int volunteerId;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private double totalHours;
    private Double approvedHours; // nullable
    private TimesheetStatus approvalStatus = TimesheetStatus.PENDING;
    private Integer approvedByAdminId; // nullable
    private LocalDateTime approvalDate; // nullable
    private String rejectionReason; // nullable
    private LocalDateTime createdDate = LocalDateTime.now();
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public int getTimesheetId() { return timesheetId; }
    public void setTimesheetId(int timesheetId) { this.timesheetId = timesheetId; }
    public int getVolunteerId() { return volunteerId; }
    public void setVolunteerId(int volunteerId) { this.volunteerId = volunteerId; }
    public LocalDate getPeriodStartDate() { return periodStartDate; }
    public void setPeriodStartDate(LocalDate periodStartDate) { this.periodStartDate = periodStartDate; }
    public LocalDate getPeriodEndDate() { return periodEndDate; }
    public void setPeriodEndDate(LocalDate periodEndDate) { this.periodEndDate = periodEndDate; }
    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }
    public Double getApprovedHours() { return approvedHours; }
    public void setApprovedHours(Double approvedHours) { this.approvedHours = approvedHours; }
    public TimesheetStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(TimesheetStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public Integer getApprovedByAdminId() { return approvedByAdminId; }
    public void setApprovedByAdminId(Integer approvedByAdminId) { this.approvedByAdminId = approvedByAdminId; }
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
}
