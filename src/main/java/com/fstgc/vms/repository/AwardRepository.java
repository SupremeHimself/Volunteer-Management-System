package com.fstgc.vms.repository;

import com.fstgc.vms.model.Award;
import java.util.List;
import java.util.Optional;

public interface AwardRepository {
    Optional<Award> findById(int id);
    List<Award> findByVolunteer(int volunteerId);
    List<Award> findByBadgeTier(String tier);
    List<Award> findLeaderboard();
    boolean checkIfAwarded(int volunteerId, int criteriaId);
    Award save(Award award);
    Award update(Award award);
}
