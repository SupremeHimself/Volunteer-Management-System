package com.fstgc.vms.model;

import com.fstgc.vms.model.enums.VolunteerStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Volunteer extends Person {
    private LocalDateTime registrationDate = LocalDateTime.now();
    private VolunteerStatus status = VolunteerStatus.ACTIVE;
    private String profilePhotoUrl;
    private LocalDate dateOfBirth;
    private String address;
    // Number of events this volunteer has attended; new volunteers start at 0
    private int eventsAttended = 0;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
    private int badgesEarned = 0;

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    public VolunteerStatus getStatus() { return status; }
    public void setStatus(VolunteerStatus status) { this.status = status; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getEventsAttended() { return eventsAttended; }
    public void setEventsAttended(int eventsAttended) { this.eventsAttended = eventsAttended; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
    public int getBadgesEarned() { return badgesEarned; }
    public void setBadgesEarned(int badgesEarned) { this.badgesEarned = badgesEarned; }
    
    // Placeholder for total hours calculation - in production this would aggregate from attendance records
    public double getTotalHoursWorked() {
        // This is a placeholder. In a real system, this would query attendance records
        // For now, return 0 hours for all volunteers initially
        return 0.0;
    }
}
