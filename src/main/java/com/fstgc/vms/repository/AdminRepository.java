package com.fstgc.vms.repository;

import com.fstgc.vms.model.SystemAdmin;
import java.util.Optional;

public interface AdminRepository {
    Optional<SystemAdmin> findByUsername(String username);
    Optional<SystemAdmin> findByEmail(String email);
    Optional<SystemAdmin> findById(int id);
    boolean validateCredentials(String username, String passwordHash);
    SystemAdmin save(SystemAdmin admin);
    SystemAdmin updatePassword(int id, String newHash);
    SystemAdmin updatePermissions(int id, String permissionsJson);
}
