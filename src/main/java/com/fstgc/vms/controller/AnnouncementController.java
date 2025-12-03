package com.fstgc.vms.controller;

import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.service.AnnouncementService;

public class AnnouncementController {
    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) { this.service = service; }

    public Announcement publish(String title, String message, com.fstgc.vms.model.enums.Priority priority) {
        Announcement a = new Announcement();
        a.setTitle(title);
        a.setMessage(message);
        a.setPriority(priority);
        return service.publish(a);
    }
    
    public java.util.List<Announcement> listAll() { return service.listAll(); }
}
