package com.fstgc.vms.repository.memory;

import com.fstgc.vms.model.Award;
import com.fstgc.vms.repository.AwardRepository;
import com.fstgc.vms.util.DataPersistence;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryAwardRepository implements AwardRepository {
    private final Map<Integer, Award> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);
    
    public InMemoryAwardRepository() {
        Map<Integer, Award> loaded = DataPersistence.loadAwards();
        store.putAll(loaded);
        if (!loaded.isEmpty()) {
            seq.set(loaded.keySet().stream().max(Integer::compare).orElse(0) + 1);
        }
    }

    @Override
    public Optional<Award> findById(int id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Award> findByVolunteer(int volunteerId) {
        return store.values().stream().filter(a -> a.getVolunteerId() == volunteerId).collect(Collectors.toList());
    }

    @Override
    public List<Award> findByBadgeTier(String tier) {
        return store.values().stream()
                .filter(a -> a.getBadgeTier().name().equalsIgnoreCase(tier))
                .collect(Collectors.toList());
    }

    @Override
    public List<Award> findLeaderboard() {
        // Simple leaderboard by count of awards
        Map<Integer, Long> counts = store.values().stream().collect(Collectors.groupingBy(Award::getVolunteerId, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted((a,b) -> Long.compare(b.getValue(), a.getValue()))
                .flatMap(e -> store.values().stream().filter(aw -> aw.getVolunteerId()==e.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkIfAwarded(int volunteerId, int criteriaId) {
        return store.values().stream().anyMatch(a -> a.getVolunteerId()==volunteerId && a.getCriteriaId()==criteriaId);
    }

    @Override
    public Award save(Award award) {
        int id = seq.getAndIncrement();
        award.setAwardId(id);
        store.put(id, award);
        DataPersistence.saveAwards(store);
        return award;
    }

    @Override
    public Award update(Award award) { 
        store.put(award.getAwardId(), award); 
        DataPersistence.saveAwards(store);
        return award; 
    }
}
