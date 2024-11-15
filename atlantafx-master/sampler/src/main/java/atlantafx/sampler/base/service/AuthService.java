package atlantafx.sampler.base.service;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.util.PasswordUtils; // Import PasswordUtils
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private static final String QUERY_SELECT_ADMIN_PASSWORD = "SELECT password_hash FROM admin WHERE username = ?";
    private static final String QUERY_SELECT_STAFF_PASSWORD = "SELECT password_hash FROM staff WHERE staff_id = ?";
    private static final String QUERY_SELECT_CASHIER_PASSWORD = "SELECT password_hash FROM cashier WHERE username = ?";

    public boolean authenticate(String username, String password, String role) {
        try (Connection conn = JDBCConnect.getJDBCConnection()) {
            if (conn == null) {
                showAlert("Error", "Database connection failed", AlertType.ERROR);
                return false;
            }

            String storedPasswordHash = null;

            // Check based on role
            if ("Admin".equals(role)) {
                storedPasswordHash = getPasswordHash(conn, QUERY_SELECT_ADMIN_PASSWORD, username);
            } else if ("Staff".equals(role)) {
                storedPasswordHash = getPasswordHash(conn, QUERY_SELECT_STAFF_PASSWORD, username);
            } else if ("Cashier".equals(role)) {
                storedPasswordHash = getPasswordHash(conn, QUERY_SELECT_CASHIER_PASSWORD, username);
            } else {
                showAlert("Error", "Invalid role", AlertType.ERROR);
                return false;
            }

            // Authenticate password
            if (storedPasswordHash != null) {
                if (isPasswordValid(password, storedPasswordHash)) {


                    return true;
                } else {
                    showAlert("Error", "Invalid password", AlertType.ERROR);
                }
            } else {
                showAlert("Error", "Username not found", AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Error", "An error occurred while processing your request: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }

        return false;
    }

    private String getPasswordHash(Connection conn, String query, String username) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("password_hash") : null;
        }
    }

    private boolean isPasswordValid(String inputPassword, String storedPasswordHash) {
        // Use PasswordUtils to check the password
        return PasswordUtils.checkPassword(inputPassword, storedPasswordHash);
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
