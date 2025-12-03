# Volunteer Management System (Java)

A layered (MVC-inspired) Java application that follows the Software Design Specification (SDS) for the FSTGC Volunteer Management System.

## Features implemented
- Models for Volunteers, Events, Attendance, Timesheets, Announcements, Awards, Admins, Audit Logs, and Award Criteria
- Repository interfaces + in-memory implementations
- Services with core business rules (unique emails, capacity checks, attendance overlap detection, hours calculation)
- Controllers orchestrating requests
- Console UI (SystemTXT) demo with a simple menu
- Export timesheets to CSV (placeholder for PDF/Excel)

## Build
```bash
mvn -v               # ensure Maven is installed
mvn clean package    # produces target/volunteer-management-system-1.0.0-shaded.jar
```

## Run
```bash
java -jar target/volunteer-management-system-1.0.0-shaded.jar
```

## Notes
- The project uses Java 17 and only standard library dependencies.
- Replace CSV export with PDF/Excel by integrating a library (e.g., Apache POI / iText) if desired.
