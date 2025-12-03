package com.fstgc.vms.util;

import com.fstgc.vms.model.*;
import java.io.*;
import java.util.*;

public class DataPersistence {
    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + ".vms-data";
    private static final String VOLUNTEERS_FILE = DATA_DIR + File.separator + "volunteers.dat";
    private static final String EVENTS_FILE = DATA_DIR + File.separator + "events.dat";
    private static final String ATTENDANCE_FILE = DATA_DIR + File.separator + "attendance.dat";
    private static final String ANNOUNCEMENTS_FILE = DATA_DIR + File.separator + "announcements.dat";
    private static final String TIMESHEETS_FILE = DATA_DIR + File.separator + "timesheets.dat";
    private static final String ADMINS_FILE = DATA_DIR + File.separator + "admins.dat";

    public static void initialize() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static <T> void saveData(String filename, Map<Integer, T> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error saving data to " + filename + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<Integer, T> loadData(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Map<Integer, T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + filename + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static void saveVolunteers(Map<Integer, Volunteer> volunteers) {
        saveData(VOLUNTEERS_FILE, volunteers);
    }

    public static Map<Integer, Volunteer> loadVolunteers() {
        return loadData(VOLUNTEERS_FILE);
    }

    public static void saveEvents(Map<Integer, Event> events) {
        saveData(EVENTS_FILE, events);
    }

    public static Map<Integer, Event> loadEvents() {
        return loadData(EVENTS_FILE);
    }

    public static void saveAttendance(Map<Integer, Attendance> attendance) {
        saveData(ATTENDANCE_FILE, attendance);
    }

    public static Map<Integer, Attendance> loadAttendance() {
        return loadData(ATTENDANCE_FILE);
    }

    public static void saveAnnouncements(Map<Integer, Announcement> announcements) {
        saveData(ANNOUNCEMENTS_FILE, announcements);
    }

    public static Map<Integer, Announcement> loadAnnouncements() {
        return loadData(ANNOUNCEMENTS_FILE);
    }

    public static void saveTimesheets(Map<Integer, Timesheet> timesheets) {
        saveData(TIMESHEETS_FILE, timesheets);
    }

    public static Map<Integer, Timesheet> loadTimesheets() {
        return loadData(TIMESHEETS_FILE);
    }

    public static void saveAdmins(Map<Integer, SystemAdmin> admins) {
        saveData(ADMINS_FILE, admins);
    }

    public static Map<Integer, SystemAdmin> loadAdmins() {
        return loadData(ADMINS_FILE);
    }
}
