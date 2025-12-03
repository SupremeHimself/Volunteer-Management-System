package com.fstgc.vms.service;

public class NotificationService {
    public void sendEmail(String to, String subject, String body) {
        // Placeholder: simulate delivery
        System.out.printf("[EMAIL] To: %s | Subject: %s | %s\n", to, subject, body);
    }
    public void sendInApp(int volunteerId, String message) {
        System.out.printf("[IN-APP] Volunteer #%d | %s\n", volunteerId, message);
    }
}
