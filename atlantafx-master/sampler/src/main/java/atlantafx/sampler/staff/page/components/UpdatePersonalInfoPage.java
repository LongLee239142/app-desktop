package atlantafx.sampler.staff.page.components;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.service.UserSession;
import atlantafx.sampler.staff.page.OutlinePage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdatePersonalInfoPage extends OutlinePage {
    public static final String NAME = "Update Personal Info";

    private TextField phoneField;
    private TextField emailField;

    @Override
    public String getName() {
        return NAME;
    }

    public UpdatePersonalInfoPage() {
        super();
        createForm();
    }

    private void createForm() {
        // Create fields for phone and email
        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        emailField = new TextField();
        emailField.setPromptText("Email");

        Button updateButton = new Button("Update Info");
        updateButton.setOnAction(e -> handleUpdateInfo());

        // Create a grid to layout the fields
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));

        formGrid.add(phoneField, 0, 0);
        formGrid.add(emailField, 0, 1);
        formGrid.add(updateButton, 0, 2);

        // Set the layout for the page
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(formGrid);

        // Add the layout to the scene graph
        getChildren().add(layout);

        // Load current personal info when the page is opened
        loadCurrentInfo();
    }

    private void loadCurrentInfo() {
        // Get the staff ID from session
        String staffId = UserSession.getInstance().getStaffId();

        String query = "SELECT contact_number, email FROM staff WHERE staff_id = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, staffId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Populate fields with current information
                phoneField.setText(rs.getString("contact_number"));
                emailField.setText(rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load personal information.");
        }
    }

    private void handleUpdateInfo() {
        String phone = phoneField.getText();
        String email = emailField.getText();

        // Validate phone and email formats
        if (!validatePhone(phone)) {
            showAlert("Error", "Invalid phone number format. Please enter a valid phone number.");
            return;
        }

        if (!validateEmail(email)) {
            showAlert("Error", "Invalid email format. Please enter a valid email address.");
            return;
        }

        // Update the information in the database
        String staffId = UserSession.getInstance().getStaffId(); // Get staff ID from session
        updatePersonalInfo(staffId, phone, email);
    }

    private void updatePersonalInfo(String staffId, String phone, String email) {
        String query = "UPDATE staff SET contact_number = ?, email = ? WHERE staff_id = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phone);
            stmt.setString(2, email);
            stmt.setString(3, staffId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert("Success", "Personal information updated successfully!");
            } else {
                showAlert("Error", "Failed to update personal information.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating information.");
        }
    }

    private boolean validatePhone(String phone) {
        // Define a regex for phone number validation (example: 10 digits)
        String phoneRegex = "^[0-9]{10}$"; // Adjust regex based on your phone number format
        return phone.matches(phoneRegex);
    }

    private boolean validateEmail(String email) {
        // Define a regex for email validation
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
