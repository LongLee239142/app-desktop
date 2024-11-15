package atlantafx.sampler.base.service;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryCalculator {

    public void calculateMonthlySalaries(int month, int year) {
        // Use the JDBCConnect class to get the connection
        try (Connection connection = JDBCConnect.getJDBCConnection()) {
            if (connection == null) {
                System.err.println("Failed to establish a connection to the database.");
                return;
            }

            String attendanceQuery = "SELECT staff.staff_id, role.basic_salary, role.allowance, " +
                    "COUNT(CASE WHEN status IN ('On Time', 'Late', 'Early Leave') THEN 1 END) AS valid_days, " +
                    "COUNT(CASE WHEN status IN ('Late', 'Early Leave') THEN 1 END) AS late_days " +
                    "FROM attendance " +
                    "JOIN staff ON attendance.staff_id = staff.staff_id " +
                    "JOIN role ON staff.role_id = role.id " +
                    "WHERE MONTH(check_in) = ? AND YEAR(check_in) = ? " +
                    "GROUP BY staff.staff_id";

            try (PreparedStatement pstmt = connection.prepareStatement(attendanceQuery)) {
                pstmt.setInt(1, month);
                pstmt.setInt(2, year);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String staffId = rs.getString("staff_id");
                    double basicSalary = rs.getDouble("basic_salary");
                    double allowance = rs.getDouble("allowance");
                    int validDays = rs.getInt("valid_days");
                    int lateDays = rs.getInt("late_days");

                    double totalSalary = calculateSalary(basicSalary, allowance, validDays, lateDays);
                    saveSalary(staffId, basicSalary, validDays, allowance, lateDays, totalSalary);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double calculateSalary(double basicSalary, double allowance, int validDays, int lateDays) {
        double basePay = basicSalary * validDays;
        double deductions = (lateDays * 0.05 * (basePay + allowance));

        // Ensure deductions do not exceed allowances
        if (deductions > allowance) {
            deductions = allowance;
        }

        return (basePay + allowance) - deductions;
    }

    private void saveSalary(String staffId, double dailyRate, int daysAttended, double bonus, int deductions, double totalSalary) {
        String insertSalaryQuery = "INSERT INTO salary (staff_id, daily_rate, days_attended, bonus, deductions, total_salary) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = JDBCConnect.getJDBCConnection(); PreparedStatement pstmt = connection.prepareStatement(insertSalaryQuery)) {
            pstmt.setString(1, staffId);
            pstmt.setDouble(2, dailyRate);
            pstmt.setInt(3, daysAttended);
            pstmt.setDouble(4, bonus);
            pstmt.setDouble(5, deductions);
            pstmt.setDouble(6, totalSalary);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
