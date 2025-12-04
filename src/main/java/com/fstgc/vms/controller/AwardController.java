package com.fstgc.vms.controller;

import com.fstgc.vms.model.Award;
import com.fstgc.vms.model.AwardCriteria;
import com.fstgc.vms.service.AwardService;

public class AwardController {
    private final AwardService service;

    public AwardController(AwardService service) {
        this.service = service;
    }

    /**
     * Assign an award to a volunteer if they meet the criteria
     */
    public Award assign(int volunteerId, AwardCriteria criteria) {
        return service.assignIfEligible(volunteerId, criteria);
    }
    
    /**
     * Get all awards earned by a specific volunteer
     */
    public java.util.List<Award> getAwardsByVolunteer(int volunteerId) {
        return service.getAwardsByVolunteer(volunteerId);
    }
}
