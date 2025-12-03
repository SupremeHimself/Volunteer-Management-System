package com.fstgc.vms.repository.memory;

import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.repository.AnnouncementRepository;
import com.fstgc.vms.util.DataPersistence;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryAnnouncementRepository implements AnnouncementRepository {
    private final Map<Integer, Announcement> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);
    
    public InMemoryAnnouncementRepository() {
        Map<Integer, Announcement> loaded = DataPersistence.loadAnnouncements();
        store.putAll(loaded);
        if (!loaded.isEmpty()) {
            seq.set(loaded.keySet().stream().max(Integer::compare).orElse(0) + 1);
        }
    }

    @Override
    public Optional<Announcement> findById(int id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Announcement> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Announcement> findActive() {
        LocalDateTime now = LocalDateTime.now();
        return store.values().stream()
                .filter(a -> !a.isDeleted() && (a.getExpiryDate()==null || a.getExpiryDate().isAfter(now)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return store.values().stream()
                .filter(a -> !a.getPublishedDate().isBefore(start) && !a.getPublishedDate().isAfter(end))
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> findByPriority(String priority) {
        return store.values().stream()
                .filter(a -> a.getPriority().name().equalsIgnoreCase(priority))
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> findByTargetAudience(String audience) {
        return store.values().stream()
                .filter(a -> a.getTargetAudience().name().equalsIgnoreCase(audience))
                .collect(Collectors.toList());
    }

    @Override
    public Announcement save(Announcement announcement) {
        int id = seq.getAndIncrement();
        announcement.setAnnouncementId(id);
        store.put(id, announcement);
        return announcement;
    }

    @Override
    public Announcement update(Announcement announcement) { store.put(announcement.getAnnouncementId(), announcement); return announcement; }

    @Override
    public boolean softDelete(int id) {
        Announcement a = store.get(id);
        if (a == null) return false;
        a.setDeleted(true);
        store.put(id, a);
        DataPersistence.saveAnnouncements(store);
        return true;
    }
    
    @Override
    public boolean delete(int id) {
        boolean removed = store.remove(id) != null;
        if (removed) {
            DataPersistence.saveAnnouncements(store);
        }
        return removed;
    }
}
