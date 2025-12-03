package com.fstgc.vms.repository.memory;

import com.fstgc.vms.model.Event;
import com.fstgc.vms.repository.EventRepository;
import com.fstgc.vms.util.DataPersistence;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryEventRepository implements EventRepository {
    private final Map<Integer, Event> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);
    
    public InMemoryEventRepository() {
        Map<Integer, Event> loaded = DataPersistence.loadEvents();
        store.putAll(loaded);
        if (!loaded.isEmpty()) {
            seq.set(loaded.keySet().stream().max(Integer::compare).orElse(0) + 1);
        }
    }

    @Override
    public Optional<Event> findById(int id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Event> findAll() { return new ArrayList<>(store.values()); }

    @Override
    public List<Event> findByDateRange(LocalDate start, LocalDate end) {
        return store.values().stream()
                .filter(e -> !e.getEventDate().isBefore(start) && !e.getEventDate().isAfter(end))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByType(String type) {
        return store.values().stream()
                .filter(e -> e.getEventType().name().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatus(String status) {
        return store.values().stream()
                .filter(e -> e.getStatus().name().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> searchByTitle(String title) {
        String q = title.toLowerCase();
        return store.values().stream()
                .filter(e -> e.getTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public Event save(Event event) {
        int id = seq.getAndIncrement();
        event.setEventId(id);
        store.put(id, event);
        DataPersistence.saveEvents(store);
        return event;
    }

    @Override
    public Event update(Event event) { store.put(event.getEventId(), event); return event; }

    @Override
    public boolean delete(int id) { return store.remove(id) != null; }
}
