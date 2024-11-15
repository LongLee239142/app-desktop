package atlantafx.sampler.admin.page.dialog;

import atlantafx.sampler.admin.layout.ModalDialog;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public final class AddStaffDialog extends ModalDialog {

    private TextField nameField;
    private TextField contactNumberField;
    private TextField emailField;
    private ComboBox<String> genderComboBox;
    private ComboBox<String> roleComboBox;
    private Label messageLabel;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public AddStaffDialog() {
        super();

        // Configure dialog header, content, and footer
        header.setTitle("Add New Staff");
        content.setBody(createContent());
        content.setFooter(createFooter());


    }

    private VBox createContent() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px;"); // Set background and border

        // Create form fields
        nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        contactNumberField = new TextField();
        contactNumberField.setPromptText("Contact Number (10-15 digits)");
        contactNumberField.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setStyle("-fx-font-size: 16px; -fx-pref-width: 200px;");

        roleComboBox = new ComboBox<>();
        loadRoles(); // Load role names from the database
        roleComboBox.setStyle("-fx-font-size: 16px; -fx-pref-width: 200px;");

        messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        // Arrange form fields in a grid layout
        GridPane formLayout = new GridPane();
        formLayout.setVgap(10);
        formLayout.setHgap(10);
        formLayout.setAlignment(Pos.CENTER); // Center the form fields
        formLayout.addRow(0, new Label("Name:"), nameField);
        formLayout.addRow(1, new Label("Contact Number:"), contactNumberField);
        formLayout.addRow(2, new Label("Email:"), emailField);
        formLayout.addRow(3, new Label("Gender:"), genderComboBox);
        formLayout.addRow(4, new Label("Role:"), roleComboBox);

        layout.getChildren().addAll(formLayout, messageLabel);
        return layout;
    }


    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setPadding(new Insets(10, 0, 0, 0));
        footer.setAlignment(Pos.CENTER); // Center the button

        // Create and style the submit button
        Button submitButton = new Button("Add Staff");
        submitButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        submitButton.setOnAction(event -> handleSubmitAction());

        footer.getChildren().add(submitButton);
        return footer;
    }

    private void loadRoles() {
        // Populate roleComboBox from the database
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT role_name FROM role");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roleComboBox.getItems().add(rs.getString("role_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load roles.");
        }
    }

    private void handleSubmitAction() {
        String name = nameField.getText();
        String contactNumber = contactNumberField.getText();
        String email = emailField.getText();
        String gender = genderComboBox.getValue();
        String role = roleComboBox.getValue();

        // Validation
        if (name.isEmpty() || contactNumber.isEmpty() || email.isEmpty() || gender == null || role == null) {
            messageLabel.setText("Please fill in all fields.");
            return;
        } else if (!PHONE_PATTERN.matcher(contactNumber).matches()) {
            messageLabel.setText("Invalid contact number format.");
            return;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            messageLabel.setText("Invalid email format.");
            return;
        }

        // Insert new staff into the database
        boolean success = insertNewStaff(name, contactNumber, email, gender, role);
        if (success) {
            messageLabel.setText("Staff member added successfully.");
            clearFormFields();
        } else {
            messageLabel.setText("Failed to add staff member.");
        }
    }

    private boolean insertNewStaff(String name, String contactNumber, String email, String gender, String role) {
        String defaultPassword = "0000";
        String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO staff (staff_id, password_hash, name, contact_number, email, gender, role_id, status_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, (SELECT id FROM role WHERE role_name = ?), 1)" // status_id 1 corresponds to 'active'
             )) {

            stmt.setString(1, generateUniqueStaffId(conn));
            stmt.setString(2, hashedPassword);
            stmt.setString(3, name);
            stmt.setString(4, contactNumber);
            stmt.setString(5, email);
            stmt.setString(6, gender);
            stmt.setString(7, role);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private String generateUniqueStaffId(Connection conn) throws SQLException {
        // Generates a unique staff ID based on your logic
        return "6" + String.format("%07d", new java.util.Random().nextInt(10000000));
    }

    private void clearFormFields() {
        nameField.clear();
        contactNumberField.clear();
        emailField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        roleComboBox.getSelectionModel().clearSelection();
    }
}
