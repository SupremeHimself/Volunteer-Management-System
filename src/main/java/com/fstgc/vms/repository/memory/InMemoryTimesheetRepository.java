package com.fstgc.vms.repository.memory;

import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.model.enums.TimesheetStatus;
import com.fstgc.vms.repository.TimesheetRepository;
import com.fstgc.vms.util.DataPersistence;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryTimesheetRepository implements TimesheetRepository {
    private final Map<Integer, Timesheet> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);
    
    public InMemoryTimesheetRepository() {
        Map<Integer, Timesheet> loaded = DataPersistence.loadTimesheets();
        store.putAll(loaded);
        if (!loaded.isEmpty()) {
            seq.set(loaded.keySet().stream().max(Integer::compare).orElse(0) + 1);
        }
    }

    @Override
    public Optional<Timesheet> findById(int id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Timesheet> findByVolunteer(int volunteerId) {
        return store.values().stream().filter(t -> t.getVolunteerId() == volunteerId).collect(Collectors.toList());
    }

    @Override
    public List<Timesheet> findByPeriod(LocalDate start, LocalDate end) {
        return store.values().stream()
                .filter(t -> !t.getPeriodStartDate().isBefore(start) && !t.getPeriodEndDate().isAfter(end))
                .collect(Collectors.toList());
    }

    @Override
    public List<Timesheet> findByApprovalStatus(String status) {
        return store.values().stream()
                .filter(t -> t.getApprovalStatus().name().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Timesheet> findPendingApprovals() {
        return store.values().stream()
                .filter(t -> t.getApprovalStatus() == TimesheetStatus.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public Timesheet save(Timesheet timesheet) {
        int id = seq.getAndIncrement();
        timesheet.setTimesheetId(id);
        store.put(id, timesheet);
        return timesheet;
    }

    @Override
    public Timesheet update(Timesheet timesheet) { store.put(timesheet.getTimesheetId(), timesheet); return timesheet; }

    @Override
    public boolean delete(int id) { return store.remove(id) != null; }
}
