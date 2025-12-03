package com.fstgc.vms.util;

import com.fstgc.vms.model.Timesheet;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportUtil {
    public static void exportTimesheetsToCsv(List<Timesheet> timesheets, String path) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("timesheet_id,volunteer_id,period_start,period_end,total_hours,approval_status\n");
            for (Timesheet t : timesheets) {
                fw.write(String.format("%d,%d,%s,%s,%.2f,%s\n",
                        t.getTimesheetId(), t.getVolunteerId(), t.getPeriodStartDate(), t.getPeriodEndDate(), t.getTotalHours(), t.getApprovalStatus()));
            }
        }
    }
}
