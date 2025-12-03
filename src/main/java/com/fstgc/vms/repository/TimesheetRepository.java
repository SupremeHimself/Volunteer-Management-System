package com.fstgc.vms.repository;

import com.fstgc.vms.model.Timesheet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimesheetRepository {
    Optional<Timesheet> findById(int id);
    List<Timesheet> findByVolunteer(int volunteerId);
    List<Timesheet> findByPeriod(LocalDate start, LocalDate end);
    List<Timesheet> findByApprovalStatus(String status);
    List<Timesheet> findPendingApprovals();
    Timesheet save(Timesheet timesheet);
    Timesheet update(Timesheet timesheet);
    boolean delete(int id);
}
