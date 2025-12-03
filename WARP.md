# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Common commands

This is a single-module Maven project targeting Java 17.

### Build & package
- Verify Maven installation and version:
  - `mvn -v`
- Compile and create the shaded runnable JAR (recommended default):
  - `mvn clean package`
  - Output: `target/volunteer-management-system-1.0.0-shaded.jar`

### Run the application
- Run the Swing GUI (default mode):
  - `java -jar target/volunteer-management-system-1.0.0-shaded.jar`
- Run the console (text-based) UI instead of the GUI:
  - `java -jar target/volunteer-management-system-1.0.0-shaded.jar --console`

### Compilation and tests
- Compile only (faster feedback while developing):
  - `mvn compile`
- Run all tests (once tests exist under `src/test/java`):
  - `mvn test`
- Run a single test class:
  - `mvn -Dtest=SomeTestClass test`
- Run a single test method:
  - `mvn -Dtest=SomeTestClass#someTestMethod test`

### Linting / static analysis
- No explicit Maven plugins for linting/static analysis (e.g., Checkstyle, SpotBugs) are currently configured in `pom.xml`.
- Rely on your IDE's inspections, or add Maven plugins if you need automated style/analysis checks.

## High-level architecture

This codebase implements a layered, MVC-inspired architecture for the FSTGC Volunteer Management System.

### Entry point and runtime modes
- **Main entry point**: `com.fstgc.vms.Main`.
  - Initializes persistent storage via `DataPersistence.initialize()`.
  - Constructs an `AuthenticationService` backed by an in-memory admin repository.
  - Chooses the UI mode:
    - No arguments → launches the Swing GUI (`SystemUI`).
    - `--console` argument → runs the text-based console UI (`SystemTXT`).

### Domain model (`com.fstgc.vms.model`)
- Contains the core entities for the system, including (non-exhaustive):
  - Volunteers, Events, Attendance records, Timesheets, Announcements, Awards, System Admins, Audit Logs, Award Criteria, plus supporting enums.
- These classes encapsulate the state and basic behavior needed by services and UIs.

### Repository layer (`com.fstgc.vms.repository` and `com.fstgc.vms.repository.memory`)
- `com.fstgc.vms.repository.*` defines repository interfaces (e.g., `VolunteerRepository`) exposing CRUD and query operations.
- `com.fstgc.vms.repository.memory.*` provides in-memory implementations used at runtime.
  - The UI and services work against the interfaces, so repositories can later be swapped for database-backed implementations without changing higher layers.

### Service layer (`com.fstgc.vms.service`)
- Encapsulates business rules and cross-entity logic; controllers and UIs should talk to services, not directly to repositories.
- Examples of responsibilities (as documented and implemented in the services):
  - Validating volunteer emails and enforcing uniqueness.
  - Validating phone numbers when present.
  - Enforcing event capacity constraints.
  - Detecting overlapping attendance records.
  - Calculating hours worked and aggregating per-volunteer statistics.
- `AuthenticationService` coordinates login/logout and current admin state for both UIs.
- `LoggingService` and `NotificationService` centralize cross-cutting concerns like audit logging and outbound notifications.

### Controller layer (`com.fstgc.vms.controller`)
- Thin orchestration layer between the UI and services.
- Controllers accept simple parameters from the UI, build or look up domain objects, and delegate to services.
- Example: `VolunteerController` wraps `VolunteerService` and exposes methods like `register(...)` and `listAll()` that the GUI/console can call.

### UI layer (`com.fstgc.vms.ui`)
- Two primary front-ends share the same underlying controllers/services:
  - **Swing GUI (`SystemUI`)**
    - A modernized multi-tabbed dashboard (Volunteers, Events, Attendance, Timesheets, Awards, Announcements, etc.).
    - Instantiates in-memory repositories and services, then wires up controllers for each feature area.
    - Uses modal dialogs for CRUD operations (e.g., add volunteer, create event, submit timesheet, publish announcement).
    - Relies on `AuthenticationService` for the currently logged-in admin and logout behavior.
  - **Console UI (`SystemTXT`)**
    - Simple text-menu driver for the same core operations, useful for quick testing or non-GUI environments.
- `LoginDialog` is the Swing login gateway; upon successful authentication it launches `SystemUI`.

### Utility classes (`com.fstgc.vms.util`)
- **`DataPersistence`**
  - Provides a simple, file-based persistence mechanism using Java object serialization.
  - Data directory: `${user.home}/.vms-data`.
  - Maintains separate `.dat` files for major aggregates (volunteers, events, attendance, announcements, timesheets, admins).
  - Exposes generic `saveData/loadData` helpers plus type-specific helpers (e.g., `saveVolunteers`, `loadVolunteers`).
- **`ExportUtil`**
  - Handles exporting timesheet data to CSV as a stand-in for more advanced formats.
  - README notes that this can be swapped for PDF/Excel by integrating a library such as Apache POI or iText.

## How to extend or modify behavior

When adding new features or modifying behavior, follow the existing layering:
- **Model changes** → add/adjust entities/enums under `com.fstgc.vms.model`.
- **Data access changes** → update repository interfaces and their in-memory implementations.
- **Business rules** → implement them in the service layer and keep controllers thin.
- **UI changes** → prefer to call into existing controllers; only add new controllers if a new aggregate or workflow is introduced.

This keeps the console UI, Swing UI, and any future interfaces aligned on the same underlying business logic and persistence model.
