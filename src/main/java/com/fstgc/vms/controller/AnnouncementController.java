package com.fstgc.vms.controller;

import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.service.AnnouncementService;

public class AnnouncementController {
    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) {
        this.service = service;
    }

    /**
     * Publish a new announcement with title, message, and priority
     */
    public Announcement publish(String title, String message, com.fstgc.vms.model.enums.Priority priority) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setPriority(priority);
        return service.publish(announcement);
    }
    
    /**
     * Get an announcement by its ID
     */
    public Announcement get(int announcementId) {
        return service.get(announcementId);
    }
    
    /**
     * Update an existing announcement
     */
    public void update(Announcement announcement) {
        service.update(announcement);
    }
    
    /**
     * Delete an announcement by ID
     */
    public boolean delete(int announcementId) {
        return service.delete(announcementId);
    }
    
    public java.util.List<Announcement> listAll() { return service.listAll(); }
}
