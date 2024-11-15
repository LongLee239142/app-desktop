package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ChangeStoreInfoPage extends OutlinePage {
    public static final String NAME = "Thau đổi thông tin quán";
    private Label messageLabel;
    private TextField storeNameField;
    private TextField storeEmailField;
    private TextField storeAddressField;
    private TextField taxCodeField;
    private TextField phoneField;

    @Override
    public String getName() {
        return NAME;
    }

    public ChangeStoreInfoPage() {
        super();
        initializeUI();
        loadStoreInfo(); // Load store information on first open
    }

    private void initializeUI() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // Title Label
        Label titleLabel = new Label(NAME);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        layout.setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.TOP_LEFT);

        // Message label for feedback
        messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        // Input fields
        storeNameField = new TextField();
        storeNameField.setPromptText("Store Name");

        storeEmailField = new TextField();
        storeEmailField.setPromptText("Store Email");

        storeAddressField = new TextField();
        storeAddressField.setPromptText("Store Address");

        taxCodeField = new TextField();
        taxCodeField.setPromptText("Tax Code");

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        // Update button
        Button updateButton = new Button("Update Store Information");
        updateButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-size: 16px;");

        updateButton.setOnAction(event -> handleUpdate());

        // Layout setup
        GridPane formLayout = new GridPane();
        formLayout.setVgap(10);
        formLayout.setHgap(10);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.add(storeNameField, 0, 0);
        formLayout.add(storeEmailField, 0, 1);
        formLayout.add(storeAddressField, 0, 2);
        formLayout.add(taxCodeField, 0, 3);
        formLayout.add(phoneField, 0, 4);
        formLayout.add(updateButton, 0, 5);
        formLayout.add(messageLabel, 0, 6);

        layout.setCenter(formLayout);
        getChildren().add(layout);
    }

    private void loadStoreInfo() {
        String query = "SELECT store_name, store_email, store_address, tax_code, phone FROM store_info WHERE id = 1";

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                storeNameField.setText(rs.getString("store_name"));
                storeEmailField.setText(rs.getString("store_email"));
                storeAddressField.setText(rs.getString("store_address"));
                taxCodeField.setText(rs.getString("tax_code"));
                phoneField.setText(rs.getString("phone"));
            }
            messageLabel.setText(""); // Clear any previous message

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load store information.");
        }
    }

    private void handleUpdate() {
        String storeName = storeNameField.getText();
        String storeEmail = storeEmailField.getText();
        String storeAddress = storeAddressField.getText();
        String taxCode = taxCodeField.getText();
        String phone = phoneField.getText();

        if (storeName.isEmpty() || storeEmail.isEmpty() || storeAddress.isEmpty() || taxCode.isEmpty() || phone.isEmpty()) {
            showMessage("Please fill in all fields.");
        } else if (!validateEmail(storeEmail)) {
            showMessage("Invalid email format.");
        } else if (!validatePhone(phone)) {
            showMessage("Invalid phone number format.");
        } else if (updateStoreInfo(storeName, storeEmail, storeAddress, taxCode, phone)) {
            showMessage("Store information updated successfully.");
            loadStoreInfo(); // Reload store info to reset form fields after update
        } else {
            showMessage("Failed to update store information.");
        }
    }

    private boolean updateStoreInfo(String storeName, String storeEmail, String storeAddress, String taxCode, String phone) {
        String updateQuery = "UPDATE store_info SET store_name = ?, store_email = ?, store_address = ?, tax_code = ?, phone = ? WHERE id = 1";

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, storeName);
            stmt.setString(2, storeEmail);
            stmt.setString(3, storeAddress);
            stmt.setString(4, taxCode);
            stmt.setString(5, phone);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update store information.");
            return false;
        }
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    private boolean validatePhone(String phone) {
        String phoneRegex = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
        return phone.matches(phoneRegex);
    }
}
