package com.fstgc.vms.repository.memory;

import com.fstgc.vms.model.SystemAdmin;
import com.fstgc.vms.repository.AdminRepository;
import com.fstgc.vms.util.DataPersistence;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryAdminRepository implements AdminRepository {
    private final Map<Integer, SystemAdmin> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);
    
    public InMemoryAdminRepository() {
        Map<Integer, SystemAdmin> loaded = DataPersistence.loadAdmins();
        store.putAll(loaded);
        if (!loaded.isEmpty()) {
            seq.set(loaded.keySet().stream().max(Integer::compare).orElse(0) + 1);
        }
    }

    @Override
    public Optional<SystemAdmin> findByUsername(String username) {
        // Search by username or email
        return store.values().stream()
                .filter(a -> username.equalsIgnoreCase(a.getUsername()) || 
                            username.equalsIgnoreCase(a.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<SystemAdmin> findByEmail(String email) {
        return store.values().stream()
                .filter(a -> email != null && email.equalsIgnoreCase(a.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<SystemAdmin> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean validateCredentials(String username, String passwordHash) {
        return findByUsername(username)
                .map(admin -> admin.getPasswordHash() != null && admin.getPasswordHash().equals(passwordHash))
                .orElse(false);
    }

    @Override
    public SystemAdmin save(SystemAdmin admin) {
        int id = seq.getAndIncrement();
        admin.setId(id);
        store.put(id, admin);
        DataPersistence.saveAdmins(store);
        return admin;
    }

    @Override
    public SystemAdmin update(SystemAdmin admin) {
        if (admin != null && admin.getId() > 0) {
            store.put(admin.getId(), admin);
            DataPersistence.saveAdmins(store);
        }
        return admin;
    }

    @Override
    public SystemAdmin updatePassword(int id, String newHash) {
        SystemAdmin admin = store.get(id);
        if (admin != null) {
            admin.setPasswordHash(newHash);
            store.put(id, admin);
            DataPersistence.saveAdmins(store);
        }
        return admin;
    }

    @Override
    public SystemAdmin updatePermissions(int id, String permissionsJson) {
        SystemAdmin admin = store.get(id);
        if (admin != null) {
            admin.setPermissionsJson(permissionsJson);
            store.put(id, admin);
            DataPersistence.saveAdmins(store);
        }
        return admin;
    }
}
