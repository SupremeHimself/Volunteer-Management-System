# Timesheet Event Display Updates

## Summary
Updated the timesheet UI to display event information (Event ID and Event Name) throughout the application.

## Changes Made

### 1. Edit Timesheet Dialog (SystemUI.java)

#### Timesheet Selection List
- **Before**: Showed only `ID | Start-End Date | Hours`
- **After**: Now shows `ID | Start-End Date | Hours | Event: [Event Name]` (when event is available)

Example display:
```
ID: 1 | 2025-11-01 to 2025-11-15 | 8.5 hrs | Event: Community Food Drive
ID: 2 | 2025-11-16 to 2025-11-30 | 12.0 hrs | Event: Youth Mentorship Program
```

#### Edit Form Fields
Added two new read-only fields:
- **Event ID**: Displays the event ID or "N/A" if not linked to an event
- **Event Name**: Displays the event name or "N/A" if not linked to an event

Form layout updated from 5 rows to 7 rows:
1. Timesheet ID (read-only)
2. **Event ID** (read-only) ← NEW
3. **Event Name** (read-only) ← NEW
4. Start Date
5. End Date
6. Total Hours
7. Status

### 2. Timesheet Card Display (SystemUI.java)

Added a "Recent Events" section to each volunteer's timesheet card showing:
- Up to 3 recent events associated with the volunteer's timesheets
- Event name and hours worked for each event
- Format: `• [Event Name] ([Hours] hrs)`

Example display:
```
Recent Events:
• Community Food Drive (8.5 hrs)
• Youth Mentorship Program (12.0 hrs)
• Park Cleanup (4.0 hrs)
```

## Data Model (Already Implemented)

The `Timesheet` class already has these fields populated:
- `Integer eventId` - Stores the event ID
- `String eventName` - Stores the event name

These fields are automatically populated when:
1. A timesheet is auto-created from attendance check-in
2. The `AttendanceService.createTimesheetForAttendance()` method runs

## Benefits

1. **Better Traceability**: Users can now see which specific events contributed to timesheet hours
2. **Improved Transparency**: Volunteers and admins can verify event associations
3. **Enhanced Reporting**: Event information is visible in both list view and detail forms
4. **Automatic Linking**: Events are automatically linked when attendance is recorded

## Testing Recommendations

1. Create new attendance records and verify timesheets show event info
2. Edit existing timesheets and verify event fields display correctly
3. Check volunteer timesheet cards show "Recent Events" section
4. Verify "N/A" displays for old timesheets without event links
5. Test with both volunteer and admin roles

## Technical Notes

- Event information is read-only in the edit dialog (cannot be changed after creation)
- The timesheet selection list gracefully handles timesheets without events
- Card display only shows events when `eventName != null` to avoid clutter
- All changes are backward compatible with existing timesheet data
