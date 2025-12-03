package com.fstgc.vms.model;

import com.fstgc.vms.model.enums.Priority;
import com.fstgc.vms.model.enums.TargetAudience;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;
    private int announcementId;
    private String title;
    private String message;
    private LocalDateTime publishedDate = LocalDateTime.now();
    private LocalDateTime expiryDate; // nullable
    private Priority priority = Priority.MEDIUM;
    private TargetAudience targetAudience = TargetAudience.ALL;
    private Integer eventId; // nullable
    private int createdByAdminId;
    private boolean deleted = false;

    public int getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(int announcementId) { this.announcementId = announcementId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDateTime publishedDate) { this.publishedDate = publishedDate; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; }
    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }
    public int getCreatedByAdminId() { return createdByAdminId; }
    public void setCreatedByAdminId(int createdByAdminId) { this.createdByAdminId = createdByAdminId; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
