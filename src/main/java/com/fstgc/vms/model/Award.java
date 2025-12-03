package com.fstgc.vms.model;

import com.fstgc.vms.model.enums.BadgeTier;
import java.time.LocalDateTime;

public class Award {
    private int awardId;
    private int volunteerId;
    private String badgeName;
    private String badgeDescription;
    private int criteriaId;
    private LocalDateTime dateEarned = LocalDateTime.now();
    private BadgeTier badgeTier = BadgeTier.BRONZE;
    private String badgeIconUrl;
    private boolean featured = false;

    public int getAwardId() { return awardId; }
    public void setAwardId(int awardId) { this.awardId = awardId; }
    public int getVolunteerId() { return volunteerId; }
    public void setVolunteerId(int volunteerId) { this.volunteerId = volunteerId; }
    public String getBadgeName() { return badgeName; }
    public void setBadgeName(String badgeName) { this.badgeName = badgeName; }
    public String getBadgeDescription() { return badgeDescription; }
    public void setBadgeDescription(String badgeDescription) { this.badgeDescription = badgeDescription; }
    public int getCriteriaId() { return criteriaId; }
    public void setCriteriaId(int criteriaId) { this.criteriaId = criteriaId; }
    public LocalDateTime getDateEarned() { return dateEarned; }
    public void setDateEarned(LocalDateTime dateEarned) { this.dateEarned = dateEarned; }
    public BadgeTier getBadgeTier() { return badgeTier; }
    public void setBadgeTier(BadgeTier badgeTier) { this.badgeTier = badgeTier; }
    public String getBadgeIconUrl() { return badgeIconUrl; }
    public void setBadgeIconUrl(String badgeIconUrl) { this.badgeIconUrl = badgeIconUrl; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
}
