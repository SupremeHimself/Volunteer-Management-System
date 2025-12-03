package com.fstgc.vms.service;

import com.fstgc.vms.model.AuditLog;
import java.util.ArrayList;
import java.util.List;

public class LoggingService {
    private final List<AuditLog> logs = new ArrayList<>();

    public void log(AuditLog log) { logs.add(log); }
    public List<AuditLog> getLogs() { return logs; }
}
