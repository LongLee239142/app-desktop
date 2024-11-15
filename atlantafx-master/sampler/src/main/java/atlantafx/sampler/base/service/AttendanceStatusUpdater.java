package atlantafx.sampler.base.service;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceStatusUpdater {

    public static void updateAttendanceStatus(String staffId) {
        String attendanceQuery = "SELECT check_in, check_out, staff_id FROM attendance WHERE staff_id = ?";
        String shiftQuery = "SELECT shift_id, assigned_date FROM shift_assignment WHERE staff_id = ?";
        String updateQuery = "UPDATE attendance SET status = ? WHERE staff_id = ? AND check_in = ? AND check_out = ?";

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement attendanceStmt = conn.prepareStatement(attendanceQuery);
             PreparedStatement shiftStmt = conn.prepareStatement(shiftQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Fetch attendance records for the staff
            attendanceStmt.setString(1, staffId);
            ResultSet attendanceRs = attendanceStmt.executeQuery();

            while (attendanceRs.next()) {
                LocalDateTime checkInTime = attendanceRs.getObject("check_in", LocalDateTime.class);
                LocalDateTime checkOutTime = attendanceRs.getObject("check_out", LocalDateTime.class);

                // Get the assigned shift for this staff
                shiftStmt.setString(1, staffId);
                ResultSet shiftRs = shiftStmt.executeQuery();

                if (shiftRs.next()) {
                    int shiftId = shiftRs.getInt("shift_id");
                    LocalDateTime assignedStartTime = getShiftStartTime(shiftId, shiftRs.getDate("assigned_date").toLocalDate());
                    LocalDateTime assignedEndTime = assignedStartTime.plusHours(8); // Assuming 8 hours shift

                    // Determine the status
                    String status = determineStatus(checkInTime, checkOutTime, assignedStartTime, assignedEndTime);

                    // Update attendance status if necessary
                    if (status != null) {
                        updateStmt.setString(1, status);
                        updateStmt.setString(2, staffId);
                        updateStmt.setObject(3, checkInTime);
                        updateStmt.setObject(4, checkOutTime);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // No assigned shift, set to "Explanation Required"
                    updateStmt.setString(1, "Explanation Required");
                    updateStmt.setString(2, staffId);
                    updateStmt.setObject(3, checkInTime);
                    updateStmt.setObject(4, checkOutTime);
                    updateStmt.executeUpdate();
                }
            }
            System.out.println("Attendance statuses updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private static LocalDateTime getShiftStartTime(int shiftId, LocalDate assignedDate) {
        // This method should return the start time of the shift based on the shiftId
        // For this example, let's assume you have a method to fetch shift details from the database.
        // Here, we'll simulate the shift start time.
        switch (shiftId) {
            case 1: // Morning shift
                return assignedDate.atTime(7, 0); // 7 AM
            case 2: // Afternoon shift
                return assignedDate.atTime(15, 0); // 3 PM
            case 3: // Administrative shift
                return assignedDate.atTime(9, 0); // 9 AM
            default:
                throw new IllegalArgumentException("Unknown shift type: " + shiftId);
        }
    }

    private static String determineStatus(LocalDateTime checkInTime, LocalDateTime checkOutTime,
                                          LocalDateTime assignedStartTime, LocalDateTime assignedEndTime) {
        if (checkOutTime == null) {
            // If checkOutTime is null, consider it as an early leave or require an explanation.
            return "No Shift"; // or any status you want to assign for missing check out
        }

        if (checkInTime.isAfter(assignedEndTime.plusHours(1))) {
            return "Explanation Required"; // Late by more than 1 hour
        } else if (checkInTime.isAfter(assignedEndTime)) {
            return "Late"; // Late but within 1 hour
        } else if (checkOutTime.isBefore(assignedStartTime.minusHours(1))) {
            return "Explanation Required"; // Left before shift time
        } else if (checkOutTime.isBefore(assignedStartTime)) {
            return "Early Leave"; // Left early but within 1 hour
        } else if (checkInTime.isBefore(assignedStartTime.minusHours(1))) {
            return "On Time"; // Arrived early before shift start
        } else if (checkInTime.isBefore(assignedStartTime)) {
            return null; // Early but no explanation needed
        }

        return null; // No status change needed
    }



}
