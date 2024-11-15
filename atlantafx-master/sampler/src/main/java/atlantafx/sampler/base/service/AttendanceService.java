package atlantafx.sampler.base.service;


import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.staff.entity.AttendanceRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceService {
    private static final String QUERY_EXPLANATIONS_REQUIRED =
            "SELECT * FROM attendance WHERE status = 'Explanation Required' AND MONTH(check_in) = MONTH(CURRENT_DATE())";

    public List<AttendanceRecord> getExplanationsRequired() {
        List<AttendanceRecord> records = new ArrayList<>();

        try (Connection conn = JDBCConnect.getJDBCConnection()) {
            if (conn == null) {
                return records; // return empty list on connection failure
            }

            try (PreparedStatement stmt = conn.prepareStatement(QUERY_EXPLANATIONS_REQUIRED)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    AttendanceRecord record = new AttendanceRecord(
                            rs.getInt("id"),
                            rs.getString("staff_id"),
                            rs.getTimestamp("check_in").toLocalDateTime(),
                            rs.getTimestamp("check_out").toLocalDateTime(),
                            rs.getString("status")
                    );
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }
}
