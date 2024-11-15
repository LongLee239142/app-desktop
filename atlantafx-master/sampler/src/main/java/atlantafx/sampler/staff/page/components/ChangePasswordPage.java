package atlantafx.sampler.staff.page.components;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.service.UserSession;
import atlantafx.sampler.staff.page.OutlinePage;
import atlantafx.sampler.base.util.PasswordUtils; // Import PasswordUtils
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ChangePasswordPage extends OutlinePage {
    public static final String NAME = "Change Password Page";

    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;

    @Override
    public String getName() {
        return NAME;
    }

    public ChangePasswordPage() {
        super();
        createForm();
    }

    private void createForm() {
        // Create fields for current password, new password, and confirm password
        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> handleChangePassword());

        // Create a grid to layout the fields
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));

        formGrid.add(currentPasswordField, 0, 0);
        formGrid.add(newPasswordField, 0, 1);
        formGrid.add(confirmPasswordField, 0, 2);
        formGrid.add(changePasswordButton, 0, 3);

        // Set the layout for the page
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(formGrid);

        // Add the layout to the scene graph
        getChildren().add(layout);
    }

    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Check if new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New password and confirmation do not match.");
            return;
        }

        // Validate the new password
        if (!validatePassword(newPassword)) {
            showAlert("Error", "Password must start with an uppercase letter, contain a special character, and include at least one number.");
            return;
        }

        // Get staff ID from session
        String staffId = UserSession.getInstance().getStaffId(); // Retrieve staff ID from session

        // Check current password and update it in the database
        if (updatePassword(staffId, currentPassword, newPassword)) {
            showAlert("Success", "Password changed successfully!");
            clearFields();
        } else {
            showAlert("Error", "Failed to change password. Please check your current password.");
        }
    }

    private boolean updatePassword(String staffId, String currentPassword, String newPassword) {
        String query = "UPDATE staff SET password_hash = ? WHERE staff_id = ? AND password_hash = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Fetch the existing password hash for verification
            String existingPasswordHash = getCurrentPasswordHash(staffId);
            if (existingPasswordHash == null || !PasswordUtils.checkPassword(currentPassword, existingPasswordHash)) {
                return false; // Current password is incorrect
            }

            // Hash the new password
            String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
            stmt.setString(1, hashedNewPassword);
            stmt.setString(2, staffId);
            stmt.setString(3, existingPasswordHash); // Use the existing hashed password for verification

            return stmt.executeUpdate() > 0; // Return true if update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getCurrentPasswordHash(String staffId) {
        String query = "SELECT password_hash FROM staff WHERE staff_id = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, staffId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }

    private boolean validatePassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Z]).+$";
        return password.matches(passwordRegex);
    }

    private void clearFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
