package com.fstgc.vms.util;

import com.fstgc.vms.model.*;
import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataPersistence {
    private static final String DATA_DIR = "database";
    private static final String DATA_FILE = DATA_DIR + File.separator + "vmsdatabase.txt";
    private static VMSDatabase database = new VMSDatabase();
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();
    
    // Custom adapter for LocalDateTime
    static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public JsonElement serialize(LocalDateTime dateTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(dateTime.format(formatter));
        }
        
        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
    
    // Custom adapter for LocalDate
    static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        @Override
        public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(formatter));
        }
        
        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
    
    // Custom adapter for LocalTime
    static class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        
        @Override
        public JsonElement serialize(LocalTime time, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(time.format(formatter));
        }
        
        @Override
        public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return LocalTime.parse(json.getAsString(), formatter);
        }
    }

    // Container class to hold all data
    static class VMSDatabase {
        Map<Integer, Volunteer> volunteers = new HashMap<>();
        Map<Integer, Event> events = new HashMap<>();
        Map<Integer, Attendance> attendance = new HashMap<>();
        Map<Integer, Announcement> announcements = new HashMap<>();
        Map<Integer, Timesheet> timesheets = new HashMap<>();
        Map<Integer, SystemAdmin> admins = new HashMap<>();
        Map<Integer, Award> awards = new HashMap<>();
    }

    public static void initialize() {
        // Create database directory if it doesn't exist
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Created database directory: " + DATA_DIR);
        }
        loadDatabase();
    }

    private static void loadDatabase() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            database = new VMSDatabase();
            return;
        }

        try {
            String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            database = gson.fromJson(json, VMSDatabase.class);
            if (database == null) {
                database = new VMSDatabase();
            }
            System.out.println("Database loaded successfully from " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error loading database from " + DATA_FILE + ": " + e.getMessage());
            database = new VMSDatabase();
        }
    }

    private static void saveDatabase() {
        try {
            String json = gson.toJson(database);
            Files.write(new File(DATA_FILE).toPath(), json.getBytes(StandardCharsets.UTF_8));
            System.out.println("Database saved successfully to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving database to " + DATA_FILE + ": " + e.getMessage());
        }
    }

    public static void saveVolunteers(Map<Integer, Volunteer> volunteers) {
        database.volunteers = new HashMap<>(volunteers);
        saveDatabase();
    }

    public static Map<Integer, Volunteer> loadVolunteers() {
        return new HashMap<>(database.volunteers);
    }

    public static void saveEvents(Map<Integer, Event> events) {
        database.events = new HashMap<>(events);
        saveDatabase();
    }

    public static Map<Integer, Event> loadEvents() {
        return new HashMap<>(database.events);
    }

    public static void saveAttendance(Map<Integer, Attendance> attendance) {
        database.attendance = new HashMap<>(attendance);
        saveDatabase();
    }

    public static Map<Integer, Attendance> loadAttendance() {
        return new HashMap<>(database.attendance);
    }

    public static void saveAnnouncements(Map<Integer, Announcement> announcements) {
        database.announcements = new HashMap<>(announcements);
        saveDatabase();
    }

    public static Map<Integer, Announcement> loadAnnouncements() {
        return new HashMap<>(database.announcements);
    }

    public static void saveTimesheets(Map<Integer, Timesheet> timesheets) {
        database.timesheets = new HashMap<>(timesheets);
        saveDatabase();
    }

    public static Map<Integer, Timesheet> loadTimesheets() {
        return new HashMap<>(database.timesheets);
    }

    public static void saveAdmins(Map<Integer, SystemAdmin> admins) {
        database.admins = new HashMap<>(admins);
        saveDatabase();
    }

    public static Map<Integer, SystemAdmin> loadAdmins() {
        return new HashMap<>(database.admins);
    }

    public static void saveAwards(Map<Integer, Award> awards) {
        database.awards = new HashMap<>(awards);
        saveDatabase();
    }

    public static Map<Integer, Award> loadAwards() {
        return new HashMap<>(database.awards);
    }
}
