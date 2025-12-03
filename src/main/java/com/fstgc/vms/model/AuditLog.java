package com.fstgc.vms.model;

import java.time.LocalDateTime;

public class AuditLog {
    private int logId;
    private int userId;
    private String userType; // admin/volunteer
    private String actionType; // create/update/delete/login/logout
    private String tableName;
    private int recordId;
    private String oldValueJson;
    private String newValueJson;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp = LocalDateTime.now();

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public String getOldValueJson() { return oldValueJson; }
    public void setOldValueJson(String oldValueJson) { this.oldValueJson = oldValueJson; }
    public String getNewValueJson() { return newValueJson; }
    public void setNewValueJson(String newValueJson) { this.newValueJson = newValueJson; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
