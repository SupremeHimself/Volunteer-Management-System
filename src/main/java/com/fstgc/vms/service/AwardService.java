package com.fstgc.vms.service;

import com.fstgc.vms.model.Award;
import com.fstgc.vms.model.AwardCriteria;
import com.fstgc.vms.model.enums.BadgeTier;
import com.fstgc.vms.repository.AwardRepository;
import java.util.List;

public class AwardService {
    private final AwardRepository repository;

    public AwardService(AwardRepository repository) { this.repository = repository; }

    public Award assignIfEligible(int volunteerId, AwardCriteria criteria) {
        if (repository.checkIfAwarded(volunteerId, criteria.getCriteriaId())) {
            return null; // already awarded
        }
        Award a = new Award();
        a.setVolunteerId(volunteerId);
        a.setBadgeName(criteria.getBadgeName());
        a.setBadgeDescription(criteria.getDescription());
        a.setCriteriaId(criteria.getCriteriaId());
        a.setBadgeTier(criteria.getBadgeTier()!=null ? criteria.getBadgeTier() : BadgeTier.BRONZE);
        return repository.save(a);
    }

    public List<Award> leaderboard() { return repository.findLeaderboard(); }
}
