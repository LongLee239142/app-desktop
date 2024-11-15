package atlantafx.sampler.admin.page.dialog;

import atlantafx.sampler.admin.layout.ModalDialog;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.util.PasswordUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ChangePasswordDialog extends ModalDialog {
    private final TextField passwordField;
    private final TextField confirmPasswordField;
    private final Button submitButton;
    private String staffName;
    private String selectedStaffId;

    public ChangePasswordDialog() {
        super();

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter New Password");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        submitButton = new Button("Submit");
        submitButton.setOnAction(e -> handleSubmit());

        VBox form = new VBox(10, passwordField, confirmPasswordField, submitButton);
        form.setStyle("-fx-padding: 20px; -fx-background-color: white; -fx-border-radius: 15px; -fx-background-radius: 15px;");
        content.setBody(form);
        content.setPrefSize(400, 250);

        // Add title with staff name dynamically
        header.setTitle("Change Password for staff: " + staffName);


        // Set border style
        content.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 15px;");


    }

    private void handleSubmit() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.equals(confirmPassword)) {
            // Hash the password before updating it
            String hashedPassword = PasswordUtils.hashPassword(password);
            updatePassword(hashedPassword);
            close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Passwords do not match!");
            alert.showAndWait();
        }
    }

    private void updatePassword(String hashedPassword) {
        // Implement database logic to update the password for the staff member
        String updateQuery = "UPDATE staff SET password = ? WHERE staff_id = ?";

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, selectedStaffId); // Assume selectedStaffId is stored when opening the dialog
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password updated successfully.");
            } else {
                System.out.println("Failed to update password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Set the staff ID and name for which the password is to be changed
    public void setStaffDetails(String staffId, String staffName) {
        this.selectedStaffId = staffId;
        this.staffName = staffName;
        header.setTitle("Change Password for staff: " + staffName); // Update the title dynamically
    }
}
