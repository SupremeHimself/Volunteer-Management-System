package com.fstgc.vms.repository;

import com.fstgc.vms.model.Announcement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository {
    Optional<Announcement> findById(int id);
    List<Announcement> findAll();
    List<Announcement> findActive();
    List<Announcement> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<Announcement> findByPriority(String priority);
    List<Announcement> findByTargetAudience(String audience);
    Announcement save(Announcement announcement);
    Announcement update(Announcement announcement);
    boolean softDelete(int id);
    boolean delete(int id);
}
