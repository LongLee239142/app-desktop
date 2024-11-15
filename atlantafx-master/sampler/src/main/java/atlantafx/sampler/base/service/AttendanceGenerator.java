package atlantafx.sampler.base.service;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttendanceGenerator {

    private static final Random RANDOM = new Random();

    public static void generateAttendanceData(List<ShiftAssignment> assignments) {
        String sql = "INSERT INTO attendance (staff_id, check_in, check_out, status) VALUES (?, ?, ?, NULL)"; // Set status as NULL

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (ShiftAssignment assignment : assignments) {
                LocalDate assignedDate = assignment.getAssignedDate();
                LocalDateTime checkInTime;
                LocalDateTime checkOutTime;

                // Determine check-in and check-out based on shift type
                switch (assignment.getShiftId()) {
                    case 1: // Morning shift
                        checkInTime = determineCheckInTime(assignedDate.atTime(7, 0)); // 7 AM
                        checkOutTime = determineCheckOutTime(checkInTime, 8); // 8-hour shift
                        break;
                    case 2: // Afternoon shift
                        checkInTime = determineCheckInTime(assignedDate.atTime(15, 0)); // 3 PM
                        checkOutTime = determineCheckOutTime(checkInTime, 8); // 8-hour shift
                        break;
                    case 3: // Administrative shift
                        checkInTime = determineCheckInTime(assignedDate.atTime(9, 0)); // 9 AM
                        checkOutTime = determineCheckOutTime(checkInTime, 9); // 9-hour shift
                        break;
                    case 4: // Off
                        checkInTime = null;
                        checkOutTime = null;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown shift type: " + assignment.getShiftId());
                }

                // Insert into the attendance table
                stmt.setString(1, assignment.getStaffId());
                stmt.setObject(2, checkInTime);
                stmt.setObject(3, checkOutTime);
                stmt.addBatch();
            }

            // Execute the batch insert
            stmt.executeBatch();
            System.out.println("Attendance records inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private static LocalDateTime determineCheckInTime(LocalDateTime scheduledTime) {
        int decision = RANDOM.nextInt(100); // Random number between 0 and 99
        LocalDateTime checkInTime;

        if (decision < 5) { // 5% chance of being late by over 1 hour
            checkInTime = scheduledTime.plusHours(1).plusMinutes(RANDOM.nextInt(61)); // Late by over 1 hour
        } else if (decision < 15) { // 10% chance of being late (up to 1 hour)
            checkInTime = scheduledTime.plusMinutes(1 + RANDOM.nextInt(60)); // Late by 1 to 60 minutes
        } else if (decision < 25) { // 10% chance of checking in early (up to 1 hour)
            checkInTime = scheduledTime.minusMinutes(RANDOM.nextInt(61)); // Early by 0 to 60 minutes
        } else { // 75% chance of being on time
            checkInTime = scheduledTime; // On time
        }

        return checkInTime;
    }

    private static LocalDateTime determineCheckOutTime(LocalDateTime checkInTime, int shiftDuration) {
        LocalDateTime checkOutTime = checkInTime.plusHours(shiftDuration);
        int decision = RANDOM.nextInt(100); // Random number between 0 and 99

        if (decision < 5) { // 5% chance of leaving early by over 1 hour
            checkOutTime = checkOutTime.minusHours(1).minusMinutes(RANDOM.nextInt(61)); // Leave early by over 1 hour
        } else if (decision < 15) { // 10% chance of leaving early (up to 1 hour)
            checkOutTime = checkOutTime.minusMinutes(1 + RANDOM.nextInt(60)); // Leave early by 1 to 60 minutes
        }

        return checkOutTime;
    }

    public static void main(String[] args) {
        List<ShiftAssignment> assignments = new ArrayList<>();
        // Add shift assignments to the list (this should come from your actual data)
        assignments.add(new ShiftAssignment("60000001", 1, LocalDate.parse("2024-10-03"))); // Morning
        assignments.add(new ShiftAssignment("60000002", 2, LocalDate.parse("2024-10-03"))); // Afternoon
        assignments.add(new ShiftAssignment("60000003", 3, LocalDate.parse("2024-10-03"))); // Administrative
        assignments.add(new ShiftAssignment("60000004", 4, LocalDate.parse("2024-10-03"))); // Off
        assignments.add(new ShiftAssignment("60000005", 1, LocalDate.parse("2024-10-03"))); // Morning
        assignments.add(new ShiftAssignment("60000006", 2, LocalDate.parse("2024-10-03"))); // Afternoon
        generateAttendanceData(assignments);
    }
}
