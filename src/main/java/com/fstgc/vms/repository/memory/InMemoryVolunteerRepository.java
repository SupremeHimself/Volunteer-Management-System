package com.fstgc.vms.repository.memory;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.repository.VolunteerRepository;
import com.fstgc.vms.util.DataPersistence;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryVolunteerRepository implements VolunteerRepository {
    private final Map<Integer, Volunteer> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);
    
    public InMemoryVolunteerRepository() {
        Map<Integer, Volunteer> loaded = DataPersistence.loadVolunteers();
        store.putAll(loaded);
        if (!loaded.isEmpty()) {
            seq.set(loaded.keySet().stream().max(Integer::compare).orElse(0) + 1);
        }
    }

    @Override
    public Optional<Volunteer> findById(int id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Volunteer> findAll() { return new ArrayList<>(store.values()); }

    @Override
    public Optional<Volunteer> findByEmail(String email) {
        return store.values().stream().filter(v -> email.equalsIgnoreCase(v.getEmail())).findFirst();
    }

    @Override
    public Volunteer save(Volunteer volunteer) {
        int id = seq.getAndIncrement();
        volunteer.setId(id);
        store.put(id, volunteer);
        DataPersistence.saveVolunteers(store);
        return volunteer;
    }

    @Override
    public Volunteer update(Volunteer volunteer) {
        store.put(volunteer.getId(), volunteer);
        DataPersistence.saveVolunteers(store);
        return volunteer;
    }

    @Override
    public boolean delete(int id) {
        boolean removed = store.remove(id) != null;
        if (removed) {
            DataPersistence.saveVolunteers(store);
        }
        return removed;
    }

    @Override
    public List<Volunteer> searchByName(String name) {
        String q = name.toLowerCase();
        return store.values().stream()
                .filter(v -> (v.getFirstName()+" "+v.getLastName()).toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public List<Volunteer> filterByStatus(String status) {
        return store.values().stream()
                .filter(v -> v.getStatus().name().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
}
