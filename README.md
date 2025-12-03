# Volunteer Management System (Java)

A modern desktop application built with Java Swing that provides comprehensive volunteer management capabilities for the FSTGC.

## Features

### Authentication & Security
- Secure login/signup system with password hashing
- Login with username OR email
- Role-based access control (SUPER_ADMIN, ADMIN, COORDINATOR, VOLUNTEER)
- Account lockout after 5 failed login attempts
- Session management and "Remember Me" functionality
- Password recovery option

**Default Admin Credentials:**
- Username: `admin`
- Email: `admin@vms.com`
- Password: `admin123`
- Role: SUPER_ADMIN

### Core Functionality
- **Dashboard** - Interactive overview with clickable statistics cards that navigate to relevant sections
  - Active Volunteers → Volunteers tab
  - Upcoming Events → Events tab  
  - Total Hours → Timesheets tab
  - Badges Earned → Awards tab
- **Volunteer Management** - Full Create, Read, Update, Delete (CRUD) operations for volunteers
  - Register new volunteers
  - View and search volunteer records
  - Edit volunteer information (Admin/Super Admin only)
  - Delete volunteers with confirmation (Admin/Super Admin only)
  - Audit tracking shows who last modified each record
- **Event Management** - Complete event lifecycle management
  - Create and schedule events with capacity tracking
  - Edit event details (Admin/Super Admin only)
  - Delete events with confirmation (Admin/Super Admin only)
  - Date format: MM-DD-YYYY
  - Audit tracking for all modifications
- **Attendance Tracking** - Record and monitor volunteer attendance
  - Check-in/check-out system
  - Automatic hours calculation (rounded up to nearest hour)
  - Attendance history per volunteer
- **Timesheet Management** - Track and approve volunteer hours
  - Generate timesheets for specific periods
  - Admin/Super Admin can edit timesheet details
  - Approval workflow with status tracking
  - Hours automatically rounded up to nearest whole hour
- **Announcements** - Communication system with priority management
  - Create, edit, and delete announcements (Admin/Super Admin only)
  - Priority levels: LOW, MEDIUM, HIGH, URGENT
  - Audit tracking for modifications
- **Awards & Badges** - Recognition system for volunteer achievements

### Technical Features
- Modern Swing GUI with professional design and hover effects
- Single-file database persistence (`database/vmsdatabase.txt`)
- In-memory repositories with automatic save/load
- MVC architecture (Models, Views, Controllers, Services)
- Cross-platform emoji support with font fallback
- Responsive layout with tabbed interface
- Resizable dialogs with scrolling support
- Comprehensive audit logging (lastModifiedBy, lastModifiedDate)

## Requirements
- Java 17 or higher
- Maven 3.6+

## Build
```bash
mvn -version         # ensure Maven is installed
mvn clean package    # produces target/volunteer-management-system-1.0.0-shaded.jar
```

This produces `target/volunteer-management-system-1.0.0.jar`

## Run
```bash
java -jar target/volunteer-management-system-1.0.0.jar
```

Or simply double-click the JAR file on systems with Java installed.

## Data Storage
All application data is automatically saved to a single database file and persists between sessions:
- **Location**: `database/vmsdatabase.txt` (created in application directory)
- **Format**: Java serialization (single-file database)
- **Contents**: 
  - Volunteers
  - Events
  - Attendance records
  - Timesheets
  - Announcements
  - User accounts (with hashed passwords)
- **Backup**: Simply copy `database/vmsdatabase.txt` to backup all data
- **Restore**: Replace the file to restore a previous backup

## Architecture
```
src/main/java/com/fstgc/vms/
├── Main.java           # Application entry point
├── controller/         # Business logic orchestration (5 controllers)
├── model/              # Domain models (8 models + 11 enums)
├── repository/         # Data access layer (interfaces)
│   └── memory/         # In-memory implementations (5 repositories)
├── service/            # Core business services (6 services)
├── ui/                 # Swing GUI components (LoginDialog, SystemUI)
└── util/               # Utility classes (DataPersistence)
```

## First-Time Setup
1. Launch the application
2. The `database` folder and `vmsdatabase.txt` file are automatically created
3. Default admin account is automatically created on first run
4. Login with admin credentials (username: `admin`, password: `admin123`)
5. Or click "Sign Up" to create a new volunteer account

## Admin Features
Admins (ADMIN and SUPER_ADMIN roles) have additional capabilities:
-  Edit volunteer, event, and announcement information
-  Delete volunteers, events, and announcements
-  Edit timesheet details (hours, dates, status)
-  View audit logs showing who last modified each record
-  Approve/reject timesheets
-  All modifications are tracked with username and timestamp

## Date Formats
- All date inputs use **MM-DD-YYYY** format
- Examples: 12-02-2025, 01-15-2026

## Hours Calculation
- Attendance hours are automatically **rounded up** to the nearest whole hour
- Example: 2.3 hours → 3 hours, 4.7 hours → 5 hours
- Admins can manually adjust hours in timesheet editing

## Notes
- All passwords are hashed using SHA-256
- New signups are automatically assigned the VOLUNTEER role
- Only SUPER_ADMIN can manage other administrators
- The database file is portable - copy it to transfer data between installations
- All edit/delete operations require admin privileges
- Audit trail tracks all modifications with username and timestamp
