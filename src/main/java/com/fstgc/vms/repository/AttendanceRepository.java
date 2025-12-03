package com.fstgc.vms.repository;

import com.fstgc.vms.model.Attendance;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository {
    Optional<Attendance> findById(int id);
    List<Attendance> findAll();
    List<Attendance> findByVolunteer(int volunteerId);
    List<Attendance> findByEvent(int eventId);
    List<Attendance> findByDateRange(LocalDateTime start, LocalDateTime end);
    Attendance save(Attendance attendance);
    Attendance update(Attendance attendance);
    boolean delete(int id);
}
