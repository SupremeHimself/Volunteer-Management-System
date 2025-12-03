package com.fstgc.vms.controller;

import com.fstgc.vms.model.Award;
import com.fstgc.vms.model.AwardCriteria;
import com.fstgc.vms.service.AwardService;

public class AwardController {
    private final AwardService service;

    public AwardController(AwardService service) { this.service = service; }

    public Award assign(int volunteerId, AwardCriteria criteria) { return service.assignIfEligible(volunteerId, criteria); }
}
