package com.fstgc.vms.model;

import com.fstgc.vms.model.enums.BadgeTier;
import com.fstgc.vms.model.enums.CriteriaType;
import java.time.LocalDateTime;

public class AwardCriteria {
    private int criteriaId;
    private String badgeName;
    private String description;
    private CriteriaType criteriaType;
    private int thresholdValue;
    private BadgeTier badgeTier = BadgeTier.BRONZE;
    private String badgeIconUrl;
    private boolean active = true;
    private LocalDateTime createdDate = LocalDateTime.now();

    public int getCriteriaId() { return criteriaId; }
    public void setCriteriaId(int criteriaId) { this.criteriaId = criteriaId; }
    public String getBadgeName() { return badgeName; }
    public void setBadgeName(String badgeName) { this.badgeName = badgeName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CriteriaType getCriteriaType() { return criteriaType; }
    public void setCriteriaType(CriteriaType criteriaType) { this.criteriaType = criteriaType; }
    public int getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(int thresholdValue) { this.thresholdValue = thresholdValue; }
    public BadgeTier getBadgeTier() { return badgeTier; }
    public void setBadgeTier(BadgeTier badgeTier) { this.badgeTier = badgeTier; }
    public String getBadgeIconUrl() { return badgeIconUrl; }
    public void setBadgeIconUrl(String badgeIconUrl) { this.badgeIconUrl = badgeIconUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}
