package com.fstgc.vms.service;

import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.repository.AnnouncementRepository;
import java.time.LocalDateTime;
import java.util.List;

public class AnnouncementService {
    private final AnnouncementRepository repository;

    public AnnouncementService(AnnouncementRepository repository) {
        this.repository = repository;
    }

    public Announcement publish(Announcement a) {
        a.setPublishedDate(LocalDateTime.now());
        return repository.save(a);
    }
    
    public Announcement get(int announcementId) {
        return repository.findById(announcementId).orElse(null);
    }
    
    public void update(Announcement announcement) {
        repository.update(announcement);
    }
    
    public boolean delete(int announcementId) {
        return repository.delete(announcementId);
    }

    public List<Announcement> active() { return repository.findActive(); }
    
    public List<Announcement> listAll() { return repository.findAll(); }
}
