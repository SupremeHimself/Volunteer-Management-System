package com.fstgc.vms.service;

import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.repository.AnnouncementRepository;
import java.time.LocalDateTime;
import java.util.List;

public class AnnouncementService {
    private final AnnouncementRepository repository;
    private final NotificationService notifier;

    public AnnouncementService(AnnouncementRepository repository, NotificationService notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    public Announcement publish(Announcement a) {
        a.setPublishedDate(LocalDateTime.now());
        Announcement saved = repository.save(a);
        notifier.sendInApp(0, "New announcement: " + a.getTitle());
        return saved;
    }

    public List<Announcement> active() { return repository.findActive(); }
    
    public List<Announcement> listAll() { return repository.findAll(); }
}
