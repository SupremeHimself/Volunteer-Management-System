package com.fstgc.vms.repository.memory;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.repository.AttendanceRepository;
import com.fstgc.vms.util.DataPersistence;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryAttendanceRepository implements AttendanceRepository {
    private final Map<Integer, Attendance> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);
    
    public InMemoryAttendanceRepository() {
        Map<Integer, Attendance> loaded = DataPersistence.loadAttendance();
        store.putAll(loaded);
        if (!loaded.isEmpty()) {
            seq.set(loaded.keySet().stream().max(Integer::compare).orElse(0) + 1);
        }
    }

    @Override
    public Optional<Attendance> findById(int id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Attendance> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Attendance> findByVolunteer(int volunteerId) {
        return store.values().stream().filter(a -> a.getVolunteerId() == volunteerId).collect(Collectors.toList());
    }

    @Override
    public List<Attendance> findByEvent(int eventId) {
        return store.values().stream().filter(a -> a.getEventId() == eventId).collect(Collectors.toList());
    }

    @Override
    public List<Attendance> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return store.values().stream()
                .filter(a -> !a.getCheckInTime().isBefore(start) && (a.getCheckOutTime()==null || !a.getCheckOutTime().isAfter(end)))
                .collect(Collectors.toList());
    }

    @Override
    public Attendance save(Attendance attendance) {
        int id = seq.getAndIncrement();
        attendance.setAttendanceId(id);
        store.put(id, attendance);
        return attendance;
    }

    @Override
    public Attendance update(Attendance attendance) { store.put(attendance.getAttendanceId(), attendance); return attendance; }

    @Override
    public boolean delete(int id) { return store.remove(id) != null; }
}
