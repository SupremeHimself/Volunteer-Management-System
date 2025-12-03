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

---

## Class Implementation Breakdown

### 🎯 Model Layer (Domain Objects)

#### Core Models

**`Person.java`** - Abstract base class for users
- Properties: `id`, `firstName`, `lastName`, `email`, `phone`, `address`
- Provides common person attributes shared by all user types
- Implements `Serializable` for persistence

**`Volunteer.java`** - Extends Person
- Additional Properties: `skills`, `availability`, `emergencyContact`, `totalHoursWorked`, `badgesEarned`, `status` (VolunteerStatus), `joinDate`
- Represents volunteer users in the system
- Tracks volunteer-specific data like hours worked and badges
- Used for volunteer registration, profile management, and activity tracking

**`SystemAdmin.java`** - Extends Person
- Additional Properties: `username`, `passwordHash`, `role` (Role enum), `accountStatus`, `createdDate`, `lastLogin`, `failedLoginAttempts`
- Represents system administrators and coordinators
- Handles authentication credentials and security features
- Tracks login attempts for account lockout mechanism
- Manages role-based access control

**`Event.java`** - Event management entity
- Properties: `eventId`, `title`, `description`, `eventDate`, `location`, `eventType`, `targetAudience`, `capacity`, `currentRegistrations`, `status`, `organizerId`, audit fields
- Represents events that volunteers can attend
- Tracks event details, capacity, and registration counts
- Automatically updates capacity when volunteers check in/out
- Supports event lifecycle management (PLANNED, ONGOING, COMPLETED, CANCELLED)

**`Attendance.java`** - Attendance tracking record
- Properties: `attendanceId`, `volunteerId`, `eventId`, `checkInTime`, `checkOutTime`, `hoursWorked`, `status`, `feedback`
- Links volunteers to events they attend
- Automatically calculates hours worked (rounded up to nearest hour)
- Updates event registration counts on check-in
- Tracks attendance status (PRESENT, ABSENT, LATE, EXCUSED)

**`Timesheet.java`** - Hour approval workflow
- Properties: `timesheetId`, `volunteerId`, `periodStartDate`, `periodEndDate`, `totalHours`, `approvedHours`, `approvalStatus`, `approvedByAdminId`, `approvalDate`, `rejectionReason`, audit fields
- Aggregates attendance hours for approval periods
- Supports approval workflow (PENDING, APPROVED, REJECTED)
- Default status is PENDING; volunteers/coordinators can submit but not change status
- Only admins can approve/reject timesheets and modify status
- Tracks who approved/rejected and when

**`Award.java`** - Recognition and badges
- Properties: `awardId`, `volunteerId`, `badgeTier`, `dateAwarded`, `reason`, `awardedByAdminId`
- Represents badges earned by volunteers (BRONZE, SILVER, GOLD, PLATINUM)
- Linked to volunteer accounts for badge counting
- Issued by administrators to recognize achievements

**`Announcement.java`** - Communication system
- Properties: `announcementId`, `title`, `message`, `priority`, `targetAudience`, `publishDate`, `expiryDate`, `isActive`, `createdBy`, audit fields
- Broadcasts messages to users with priority levels (LOW, MEDIUM, HIGH, URGENT)
- Supports targeted messaging by audience type
- Only admins can create/edit/delete announcements
- Tracks creation and modification history

**`AuditLog.java`** - Change tracking
- Properties: `logId`, `entityType`, `entityId`, `action`, `userId`, `username`, `timestamp`, `details`
- Records all modifications to system entities
- Provides audit trail for compliance and accountability

**`AwardCriteria.java`** - Badge earning rules
- Properties: `criteriaId`, `badgeTier`, `criteriaType`, `thresholdValue`, `description`
- Defines requirements for earning badges
- Supports different criteria types (HOURS_WORKED, EVENTS_ATTENDED, etc.)

#### Enums

**`Role.java`** - User authorization levels
- Values: `SUPER_ADMIN`, `ADMIN`, `COORDINATOR`, `VOLUNTEER`
- Determines user permissions throughout the system

**`AccountStatus.java`** - User account states
- Values: `ACTIVE`, `SUSPENDED`, `LOCKED`, `INACTIVE`
- Controls login access and account availability

**`VolunteerStatus.java`** - Volunteer activity states
- Values: `ACTIVE`, `INACTIVE`, `ON_LEAVE`, `TERMINATED`
- Tracks volunteer participation status

**`EventStatus.java`** - Event lifecycle states
- Values: `PLANNED`, `ONGOING`, `COMPLETED`, `CANCELLED`
- Manages event progression stages

**`EventType.java`** - Event categories
- Values: `COMMUNITY_SERVICE`, `FUNDRAISING`, `EDUCATION`, `HEALTH`, `ENVIRONMENT`, `SPORTS`, `ARTS`
- Classifies events by type

**`AttendanceStatus.java`** - Attendance tracking states
- Values: `PRESENT`, `ABSENT`, `LATE`, `EXCUSED`
- Records volunteer attendance outcomes

**`TimesheetStatus.java`** - Approval workflow states
- Values: `PENDING`, `APPROVED`, `REJECTED`
- Manages timesheet approval process

**`BadgeTier.java`** - Award levels
- Values: `BRONZE`, `SILVER`, `GOLD`, `PLATINUM`
- Defines badge hierarchy and recognition levels

**`Priority.java`** - Announcement importance levels
- Values: `LOW`, `MEDIUM`, `HIGH`, `URGENT`
- Determines announcement priority and display styling

**`TargetAudience.java`** - Audience targeting
- Values: `ALL`, `VOLUNTEERS`, `COORDINATORS`, `ADMINS`
- Specifies intended recipients for events/announcements

**`CriteriaType.java`** - Award criteria types
- Values: `HOURS_WORKED`, `EVENTS_ATTENDED`, `CONSECUTIVE_MONTHS`, `SPECIAL_ACHIEVEMENT`
- Defines metrics for badge eligibility

---

### 🗄️ Repository Layer (Data Access)

#### Repository Interfaces

Define standard CRUD operations for each entity:

**`VolunteerRepository.java`** - Volunteer data access
- Methods: `findById()`, `findByEmail()`, `findByStatus()`, `findAll()`, `save()`, `update()`, `delete()`

**`AdminRepository.java`** - Admin user data access
- Methods: `findById()`, `findByUsername()`, `findByEmail()`, `findByRole()`, `findAll()`, `save()`, `update()`, `delete()`

**`EventRepository.java`** - Event data access
- Methods: `findById()`, `findByStatus()`, `findByDateRange()`, `findByType()`, `findAll()`, `save()`, `update()`, `delete()`

**`AttendanceRepository.java`** - Attendance data access
- Methods: `findById()`, `findByVolunteer()`, `findByEvent()`, `findByDateRange()`, `findAll()`, `save()`, `update()`, `delete()`

**`TimesheetRepository.java`** - Timesheet data access
- Methods: `findById()`, `findByVolunteer()`, `findByPeriod()`, `findByApprovalStatus()`, `findPendingApprovals()`, `findAll()`, `save()`, `update()`, `delete()`

**`AnnouncementRepository.java`** - Announcement data access
- Methods: `findById()`, `findByPriority()`, `findByTargetAudience()`, `findActive()`, `findAll()`, `save()`, `update()`, `delete()`

**`AwardRepository.java`** - Award data access
- Methods: `findById()`, `findByVolunteer()`, `findByBadgeTier()`, `findAll()`, `save()`, `update()`, `delete()`

#### In-Memory Implementations

Located in `repository/memory/` package - all extend their respective interfaces:

**`InMemoryVolunteerRepository.java`**
- Uses `ConcurrentHashMap<Integer, Volunteer>` for thread-safe storage
- `AtomicInteger` for auto-incrementing IDs
- Pre-populates with sample data on initialization
- Filters by email, status, and other criteria using Java Streams

**`InMemoryAdminRepository.java`**
- Stores admin accounts with `ConcurrentHashMap<Integer, SystemAdmin>`
- Creates default SUPER_ADMIN account on initialization (username: admin, password: admin123)
- Supports username and email lookups
- Thread-safe operations for concurrent access

**`InMemoryEventRepository.java`**
- Manages events with `ConcurrentHashMap<Integer, Event>`
- Pre-populates sample events on first run
- Filters by date range, status, and type
- Updates currentRegistrations and capacity during attendance operations

**`InMemoryAttendanceRepository.java`**
- Stores attendance records in `ConcurrentHashMap<Integer, Attendance>`
- Filters by volunteer, event, and date range
- Calculates hours worked automatically on check-out
- Rounds hours up to nearest whole number

**`InMemoryTimesheetRepository.java`**
- Uses `ConcurrentHashMap<Integer, Timesheet>` for timesheet storage
- Finds pending approvals for admin workflow
- Filters by volunteer, period, and approval status
- Supports timesheet update operations

**`InMemoryAnnouncementRepository.java`**
- Stores announcements with `ConcurrentHashMap<Integer, Announcement>`
- Filters active announcements by expiry date
- Supports priority and audience-based filtering
- Pre-populates sample announcements

**`InMemoryAwardRepository.java`**
- Manages awards with `ConcurrentHashMap<Integer, Award>`
- Filters by volunteer for badge counting
- Filters by badge tier for statistics
- Supports award issuance and revocation

---

### 🔧 Service Layer (Business Logic)

**`AuthenticationService.java`** - User authentication and security
- **Purpose**: Handles login, signup, and session management
- **Key Methods**:
  - `login(username, password)` - Authenticates users, tracks failed attempts, enforces account lockout
  - `signup(admin)` - Registers new users with VOLUNTEER role by default
  - `logout()` - Clears current session
  - `getCurrentUser()` - Returns authenticated user
  - `hashPassword(password)` - SHA-256 password hashing
- **Features**: 
  - Supports login with username OR email
  - Account lockout after 5 failed attempts
  - Password hashing for security
  - Session state management

**`VolunteerService.java`** - Volunteer management operations
- **Purpose**: Business logic for volunteer CRUD operations
- **Key Methods**:
  - `register(volunteer)` - Creates new volunteer with validation
  - `update(volunteer)` - Updates volunteer information
  - `deactivate(volunteerId)` - Sets volunteer status to INACTIVE
  - `findByEmail(email)` - Locates volunteer by email
  - `listAll()` - Returns all volunteers
  - `getVolunteerStats(volunteerId)` - Calculates volunteer statistics
- **Features**: 
  - Input validation using ValidationService
  - Email uniqueness checks
  - Status management

**`EventService.java`** - Event lifecycle management
- **Purpose**: Handles event creation, updates, and queries
- **Key Methods**:
  - `create(event)` - Creates new event with validation
  - `update(event)` - Updates event details
  - `cancel(eventId)` - Cancels event
  - `findUpcoming()` - Returns future events
  - `findByDateRange(start, end)` - Filters events by date
  - `registerVolunteer(eventId, volunteerId)` - Registers volunteer for event
- **Features**:
  - Date validation
  - Capacity tracking
  - Event status management
  - Integration with attendance tracking

**`AttendanceService.java`** - Attendance tracking and hours calculation
- **Purpose**: Manages check-in/check-out and hours calculation
- **Key Methods**:
  - `checkIn(volunteerId, eventId)` - Records volunteer check-in, decrements event capacity
  - `checkOut(attendanceId)` - Records check-out, calculates hours (rounded up)
  - `deleteAttendance(attendanceId)` - Removes attendance, restores event capacity
  - `calculateHours(checkIn, checkOut)` - Computes hours worked
  - `getVolunteerAttendance(volunteerId)` - Retrieves attendance history
- **Features**:
  - Automatic hours calculation with rounding
  - Updates event registration counts
  - Prevents duplicate check-ins
  - Reverses capacity changes on deletion

**`TimesheetService.java`** - Timesheet approval workflow
- **Purpose**: Manages timesheet submission and approval
- **Key Methods**:
  - `submit(volunteerId, start, end, status)` - Creates timesheet, aggregates hours from attendance
  - `approve(timesheetId, adminId)` - Approves timesheet, sets approved hours
  - `reject(timesheetId, adminId, reason)` - Rejects timesheet with reason
  - `update(timesheet)` - Updates timesheet details
  - `listAll()` - Returns all timesheets
- **Features**:
  - Defaults to PENDING status
  - Volunteers/coordinators can submit but not change status
  - Only admins can approve/reject
  - Calculates total hours from attendance records
  - Tracks approval history

**`AnnouncementService.java`** - Announcement management
- **Purpose**: Creates and manages system announcements
- **Key Methods**:
  - `create(announcement)` - Creates new announcement (admin only)
  - `update(announcement)` - Updates announcement content
  - `delete(announcementId)` - Removes announcement
  - `findActive()` - Returns non-expired announcements
  - `findByPriority(priority)` - Filters by priority level
- **Features**:
  - Priority-based display
  - Expiry date handling
  - Target audience filtering
  - Admin-only creation/editing

**`AwardService.java`** - Badge and recognition system
- **Purpose**: Manages volunteer awards and badges
- **Key Methods**:
  - `issueAward(volunteerId, badgeTier, reason, adminId)` - Issues badge to volunteer
  - `getAwardsByVolunteer(volunteerId)` - Returns all awards for a volunteer
  - `revokeAward(awardId)` - Removes award
  - `findByBadgeTier(tier)` - Filters awards by tier
- **Features**:
  - Links awards to volunteer accounts
  - Badge counting for statistics
  - Admin-only issuance
  - Tracks award history

**`ValidationService.java`** - Input validation and business rules
- **Purpose**: Centralizes validation logic
- **Key Methods**:
  - `validateEmail(email)` - Email format validation
  - `validatePhone(phone)` - Phone number validation
  - `validateDateRange(start, end)` - Date range validation
  - `validateCapacity(capacity)` - Capacity validation
- **Features**:
  - Reusable validation rules
  - Consistent error messages
  - Business rule enforcement

**`LoggingService.java`** - Audit trail and logging
- **Purpose**: Records system activity for audit purposes
- **Key Methods**:
  - `logAction(entityType, entityId, action, userId, details)` - Records activity
  - `getLogsByEntity(entityType, entityId)` - Retrieves entity history
  - `getLogsByUser(userId)` - Retrieves user activity
- **Features**:
  - Comprehensive audit trail
  - Tracks who, what, when
  - Compliance support

**`NotificationService.java`** - User notifications (future enhancement)
- **Purpose**: Placeholder for notification features
- **Planned Features**:
  - Email notifications
  - In-app alerts
  - Event reminders

---

### 🎮 Controller Layer (Request Handling)

Controllers act as the bridge between UI and Service layers, delegating business logic to services.

**`VolunteerController.java`**
- **Purpose**: Coordinates volunteer operations
- **Key Methods**: `register()`, `update()`, `deactivate()`, `findById()`, `findByEmail()`, `listAll()`
- **Dependencies**: VolunteerService
- **Role**: Thin delegation layer, no business logic

**`EventController.java`**
- **Purpose**: Coordinates event operations
- **Key Methods**: `create()`, `update()`, `cancel()`, `delete()`, `findById()`, `listAll()`
- **Dependencies**: EventService
- **Role**: Handles event CRUD, delegates to service

**`AttendanceController.java`**
- **Purpose**: Coordinates attendance operations
- **Key Methods**: `checkIn()`, `checkOut()`, `byVolunteer()`, `byEvent()`, `listAll()`, `delete()`
- **Dependencies**: AttendanceService
- **Role**: Manages attendance workflow

**`TimesheetController.java`**
- **Purpose**: Coordinates timesheet operations
- **Key Methods**: `submit()`, `approve()`, `reject()`, `update()`, `generate()`, `listAll()`
- **Dependencies**: TimesheetService
- **Role**: Handles timesheet approval workflow

**`AnnouncementController.java`**
- **Purpose**: Coordinates announcement operations
- **Key Methods**: `create()`, `update()`, `delete()`, `listAll()`, `findActive()`
- **Dependencies**: AnnouncementService
- **Role**: Manages announcements

**`AwardController.java`**
- **Purpose**: Coordinates award operations
- **Key Methods**: `issue()`, `revoke()`, `getAwardsByVolunteer()`, `listAll()`
- **Dependencies**: AwardService
- **Role**: Handles badge issuance

---

### 🖥️ UI Layer (User Interface)

**`LoginDialog.java`** - Authentication interface
- **Purpose**: Provides login and signup forms
- **Features**:
  - Login with username OR email
  - Password field with visibility toggle
  - "Remember Me" checkbox
  - Signup dialog for new users
  - Error handling and validation feedback
  - Forgot password link (placeholder)
- **Flow**:
  1. User enters credentials
  2. Validates input
  3. Calls AuthenticationService.login()
  4. On success, launches SystemUI
  5. On failure, shows error message

**`SystemUI.java`** - Main application window (2,437 lines)
- **Purpose**: Primary GUI with tabbed interface
- **Architecture**: 
  - Tabbed navigation (Dashboard, Volunteers, Events, Attendance, Timesheets, Awards, Announcements)
  - Role-based UI elements (shows/hides buttons based on user role)
  - Modern design with Material Design colors
  - Emoji font support with cross-platform fallback
- **Key Components**:
  - `createDashboardPanel()` - Statistics cards with click navigation, role-based stats display
  - `createVolunteerPanel()` - Volunteer table with CRUD operations
  - `createEventPanel()` - Split into "Upcoming Events" and "Past Events" sections
  - `createAttendancePanel()` - Attendance records table with check-in/out
  - `createTimesheetPanel()` - Timesheet management with approval workflow
  - `createAwardPanel()` - Badge leaderboard and tier statistics
  - `createAnnouncementPanel()` - Priority-coded announcements with color borders
- **Role-Based Access Control**:
  - VOLUNTEER/COORDINATOR: Read-only access, can submit timesheets, auto-populated volunteer ID in attendance
  - ADMIN/SUPER_ADMIN: Full CRUD access, can create events/announcements, approve timesheets, edit/delete records
- **Dialog Methods**:
  - Event dialogs: `showAddEventDialog()`, `showEditEventDialog()`, `deleteEvent()`
  - Attendance dialogs: `showAttendanceDialog()` (auto-populates volunteer ID for non-admins)
  - Timesheet dialogs: `showSubmitTimesheetDialog()` (status hidden for non-admins), `showEditTimesheetDialog()` (status disabled for non-admins), `showCreateTimesheetDialog()`
  - Announcement dialogs: `showAddAnnouncementDialog()`, `showEditAnnouncementDialog()`, `deleteAnnouncement()`
- **Features**:
  - Auto-refresh on data changes
  - Confirmation dialogs for destructive operations
  - Audit trail display (lastModifiedBy, lastModifiedDate)
  - Date format: MM-DD-YYYY throughout
  - Responsive grid layouts

**`SystemTXT.java`** - Console-based text interface (legacy)
- **Purpose**: Alternative text-based UI for console environments
- **Features**: Menu-driven interface, basic CRUD operations
- **Note**: Primarily for testing and non-GUI environments

---

### 🛠️ Utility Layer

**`DataPersistence.java`** - Database persistence manager
- **Purpose**: Handles serialization/deserialization of all data
- **Key Methods**:
  - `loadData()` - Reads `database/vmsdatabase.txt`, deserializes all repositories
  - `saveData(repositories)` - Serializes all repositories to file
  - `createDefaultData()` - Initializes default admin and sample data
- **Data Structure**: Single file containing:
  - Map of all repository data
  - Volunteers, Events, Attendance, Timesheets, Announcements, Awards, Admins
- **Features**:
  - Automatic directory creation (`database/` folder)
  - Atomic saves (writes to temp file, then renames)
  - Error handling with fallback
  - Java serialization format

**`ExportUtil.java`** - Data export functionality (future enhancement)
- **Purpose**: Export data to CSV, PDF, Excel formats
- **Planned Features**:
  - Volunteer reports
  - Attendance summaries
  - Timesheet exports
  - Event rosters

---

### 🚀 Main Application

**`Main.java`** - Application entry point
- **Purpose**: Bootstraps the application
- **Initialization Flow**:
  1. Loads persisted data from `database/vmsdatabase.txt`
  2. Initializes all repositories (InMemory implementations)
  3. Creates services with repository dependencies
  4. Creates controllers with service dependencies
  5. Initializes AuthenticationService
  6. Launches LoginDialog
- **Dependency Injection**: Manual constructor injection pattern
- **Error Handling**: Try-catch for initialization failures
- **Thread Safety**: Launches UI on EDT (Event Dispatch Thread)

---

## Architecture Patterns

### 1. **MVC (Model-View-Controller)**
   - **Models**: Domain objects (Volunteer, Event, etc.)
   - **Views**: UI components (LoginDialog, SystemUI)
   - **Controllers**: Request handlers (VolunteerController, etc.)

### 2. **Repository Pattern**
   - Abstracts data access layer
   - Interfaces define contracts
   - In-memory implementations for current version
   - Easy to swap for database implementations (SQL, NoSQL)

### 3. **Service Layer Pattern**
   - Encapsulates business logic
   - Coordinates multiple repositories
   - Reusable across different UI implementations

### 4. **Dependency Injection**
   - Manual constructor injection
   - Clear dependency hierarchy
   - Testable components

### 5. **Singleton Pattern**
   - AuthenticationService maintains current user session
   - DataPersistence manages single database file

### 6. **Role-Based Access Control (RBAC)**
   - Permissions checked at UI layer (show/hide elements)
   - Permissions enforced at service layer (business logic)
   - Four roles: SUPER_ADMIN, ADMIN, COORDINATOR, VOLUNTEER

---

## Data Flow Example: Check-In Process

1. **User Action**: User clicks "Check In" in attendance dialog
2. **UI Layer**: `SystemUI.showAttendanceDialog()` captures volunteerId and eventId
3. **Controller**: Calls `AttendanceController.checkIn(volunteerId, eventId)`
4. **Service**: `AttendanceService.checkIn()`:
   - Validates volunteer and event exist
   - Creates new Attendance record with check-in time
   - Updates Event: `currentRegistrations++`, `capacity--`
   - Saves attendance to repository
   - Returns Attendance object
5. **Repository**: `InMemoryAttendanceRepository.save()` stores in ConcurrentHashMap
6. **Persistence**: `DataPersistence.saveData()` serializes all data to disk
7. **UI Update**: Dialog shows success message, refreshes all panels
8. **Dashboard**: Updated statistics reflect new attendance

---

## Role-Based UI Behavior

### Dashboard Statistics
- **Volunteers/Coordinators**: See personal "My Hours" and "My Badges"
- **Admins**: See system-wide "Total Hours (All Users)" and "Total Badges (All Users)"

### Events Panel
- **All Users**: View upcoming and past events (split into sections)
- **Volunteers/Coordinators**: No create/edit/delete buttons
- **Admins**: "Create Event" button visible, Edit/Delete buttons on event cards

### Attendance Panel
- **All Users**: Can record attendance
- **Volunteers/Coordinators**: Volunteer ID auto-populated and read-only
- **Admins**: Can enter any volunteer ID

### Timesheets Panel
- **All Users**: Can view timesheets
- **Volunteers/Coordinators**: Can submit timesheets (default status: PENDING), cannot change status
- **Admins**: Can edit timesheets, approve/reject, change status

### Announcements Panel
- **All Users**: Can read announcements
- **Volunteers/Coordinators**: No create/edit/delete buttons
- **Admins**: "New Announcement" button visible, Edit/Delete available

---

## Technical Implementation Notes

### Thread Safety
- `ConcurrentHashMap` used in all repositories for thread-safe operations
- `AtomicInteger` for ID generation prevents race conditions
- Synchronized file access in DataPersistence

### Data Validation
- Email format validation (regex)
- Phone number validation
- Date range validation (start < end)
- Capacity validation (capacity > 0)
- Role-based permission checks

### Password Security
- SHA-256 hashing algorithm
- Passwords never stored in plain text
- Failed login attempt tracking
- Account lockout mechanism after 5 failures

### Date Handling
- `LocalDate` for dates (events, timesheets)
- `LocalDateTime` for timestamps (check-in, audit logs)
- Consistent MM-DD-YYYY format throughout UI
- `DateTimeFormatter` for parsing and display

### Hours Calculation
- `Duration.between()` for time difference
- Always rounds UP to nearest hour using `Math.ceil()`
- Example: 2.1 hours → 3 hours, 4.9 hours → 5 hours

### Emoji Support
- Font detection for emoji rendering
- Fallback chain: Segoe UI Emoji → Apple Color Emoji → Noto Color Emoji → Default
- Cross-platform compatibility (Windows, macOS, Linux)

### Persistence Strategy
- Single-file database for simplicity
- Java serialization for easy object storage
- Atomic saves (write to temp, then rename)
- Automatic backup on startup (future enhancement)

---

## Extension Points

### Adding New Features
1. **Create Model**: Add new entity in `model/` package
2. **Create Repository Interface**: Define data access methods
3. **Implement Repository**: Create in-memory implementation
4. **Create Service**: Add business logic
5. **Create Controller**: Add thin delegation layer
6. **Update UI**: Add panel/dialog in SystemUI
7. **Update Persistence**: Add to DataPersistence save/load

### Switching to Database
1. Implement repository interfaces with JDBC/JPA
2. Replace InMemory implementations with database versions
3. Update DataPersistence to use database connection
4. No changes needed in Service, Controller, or UI layers

### Adding Authentication Methods
1. Extend AuthenticationService
2. Add OAuth/LDAP/SSO support
3. Update LoginDialog with new UI elements
4. Keep existing password hashing as fallback
