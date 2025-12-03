package com.fstgc.vms.ui;

import com.fstgc.vms.controller.*;
import com.fstgc.vms.model.*;
import com.fstgc.vms.model.enums.*;
import com.fstgc.vms.repository.memory.*;
import com.fstgc.vms.service.*;

import java.time.LocalDate;
import java.util.Scanner;

public class SystemTXT {
    private final Scanner scanner = new Scanner(System.in);

    private final VolunteerController volunteerController;
    private final EventController eventController;
    private final AttendanceController attendanceController;
    private final TimesheetController timesheetController;
    private final AnnouncementController announcementController;

    public SystemTXT() {
        // Wire dependencies
        VolunteerService volunteerService = new VolunteerService(new InMemoryVolunteerRepository());
        EventService eventService = new EventService(new InMemoryEventRepository());
        AttendanceService attendanceService = new AttendanceService(new InMemoryAttendanceRepository());
        TimesheetService timesheetService = new TimesheetService(new InMemoryTimesheetRepository(), new InMemoryAttendanceRepository());
        AnnouncementService announcementService = new AnnouncementService(new InMemoryAnnouncementRepository());

        this.volunteerController = new VolunteerController(volunteerService);
        this.eventController = new EventController(eventService);
        this.attendanceController = new AttendanceController(attendanceService);
        this.timesheetController = new TimesheetController(timesheetService);
        this.announcementController = new AnnouncementController(announcementService);
    }

    public void run() {
        System.out.println("=== Volunteer Management System (Console) ===");
        boolean running = true;
        while (running) {
            System.out.println("\n1) Register Volunteer\n2) Create Event\n3) Check-In\n4) Check-Out\n5) Generate Timesheet\n6) Publish Announcement\n7) Exit");
            System.out.print("Select: ");
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1": registerVolunteer(); break;
                    case "2": createEvent(); break;
                    case "3": checkIn(); break;
                    case "4": checkOut(); break;
                    case "5": generateTimesheet(); break;
                    case "6": publishAnnouncement(); break;
                    case "7": running = false; break;
                    default: System.out.println("Invalid option");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    private void registerVolunteer() {
        System.out.print("First name: "); String fn = scanner.nextLine();
        System.out.print("Last name: "); String ln = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();
        System.out.print("Phone: "); String phone = scanner.nextLine();
        Volunteer v = volunteerController.register(fn, ln, email, phone);
        System.out.println("Registered volunteer #" + v.getId());
    }

    private void createEvent() {
        System.out.print("Title: "); String title = scanner.nextLine();
        System.out.print("Date (YYYY-MM-DD): "); LocalDate date = LocalDate.parse(scanner.nextLine());
        System.out.print("Location: "); String location = scanner.nextLine();
        System.out.print("Capacity: "); int cap = Integer.parseInt(scanner.nextLine());
        Event e = eventController.create(title, date, cap, EventType.WORKSHOP, location);
        System.out.println("Created event #" + e.getEventId());
    }

    private void checkIn() {
        System.out.print("Volunteer ID: "); int vid = Integer.parseInt(scanner.nextLine());
        System.out.print("Event ID: "); int eid = Integer.parseInt(scanner.nextLine());
        Attendance a = attendanceController.checkIn(vid, eid);
        System.out.println("Attendance #" + a.getAttendanceId() + " checked-in.");
    }

    private void checkOut() {
        System.out.print("Attendance ID: "); int aid = Integer.parseInt(scanner.nextLine());
        Attendance a = attendanceController.checkOut(aid);
        System.out.println("Checked-out. Hours worked: " + a.getHoursWorked());
    }

    private void generateTimesheet() {
        System.out.print("Volunteer ID: "); int vid = Integer.parseInt(scanner.nextLine());
        System.out.print("Start date (YYYY-MM-DD): "); LocalDate s = LocalDate.parse(scanner.nextLine());
        System.out.print("End date (YYYY-MM-DD): "); LocalDate e = LocalDate.parse(scanner.nextLine());
        Timesheet t = timesheetController.generate(vid, s, e);
        System.out.println("Timesheet #" + t.getTimesheetId() + " total hours: " + t.getTotalHours());
    }

    private void publishAnnouncement() {
        System.out.print("Title: "); String title = scanner.nextLine();
        System.out.print("Message: "); String message = scanner.nextLine();
        System.out.print("Priority (LOW/MEDIUM/HIGH/URGENT): "); 
        String priorityStr = scanner.nextLine().toUpperCase();
        Priority priority = Priority.valueOf(priorityStr.isEmpty() ? "MEDIUM" : priorityStr);
        Announcement a = announcementController.publish(title, message, priority);
        System.out.println("Announcement #" + a.getAnnouncementId() + " published.");
    }
}
