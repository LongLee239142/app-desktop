package atlantafx.sampler.admin.page.dialog;

import atlantafx.sampler.admin.entity.Supplier;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditSupplierDialog extends Stage {
    private final Supplier supplier;

    public EditSupplierDialog(Supplier supplier) {
        this.supplier = supplier;

        setTitle("Edit Supplier");
        initModality(Modality.APPLICATION_MODAL);
        setMinWidth(300);

        // Create form layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Create fields
        Label idLabel = new Label("Supplier ID:");
        TextField idField = new TextField(supplier.getSuppliersId());
        idField.setEditable(false); // Not editable
        Label nameLabel = new Label("Supplier Name:");
        TextField nameField = new TextField(supplier.getName());
        nameField.setEditable(false); // Not editable
        Label contactNumberLabel = new Label("Contact Number:");
        TextField contactNumberField = new TextField(supplier.getContactNumber());
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(supplier.getEmail());

        // Validation message labels
        Label validationLabel = new Label();
        validationLabel.setStyle("-fx-text-fill: red;");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String contactNumber = contactNumberField.getText();
            String email = emailField.getText();
            if (isValidPhoneNumber(contactNumber) && isValidEmail(email)) {
                updateSupplier(supplier.getSuppliersId(), contactNumber, email);
                close(); // Close dialog after saving
            } else {
                validationLabel.setText("Số điện thoại không hợp lệ hoặc email không hợp lệ.");
            }
        });

        // Add elements to the grid
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(contactNumberLabel, 0, 2);
        grid.add(contactNumberField, 1, 2);
        grid.add(emailLabel, 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(validationLabel, 0, 4, 2, 1); // Span two columns for validation message
        grid.add(saveButton, 1, 5);

        Scene scene = new Scene(grid);
        setScene(scene);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("0\\d{9}"); // Phone number should start with 0 and be 10 digits long
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"); // Basic email validation regex
    }

    private void updateSupplier(String suppliersId, String contactNumber, String email) {
        Connection connection = JDBCConnect.getJDBCConnection();
        String query = "UPDATE suppliers SET contact_number = ?, email = ? WHERE suppliers_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, contactNumber);
            statement.setString(2, email);
            statement.setString(3, suppliersId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
