package atlantafx.sampler.admin.page.dialog;

import atlantafx.sampler.admin.layout.ModalDialog;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ChangeSalaryDialog extends ModalDialog {
    private final TextField basicSalaryField;
    private final TextField allowanceField;
    private final Button submitButton;
    private String roleName;
    private int roleId;

    public ChangeSalaryDialog() {
        super();

        // Initialize input fields
        basicSalaryField = new TextField();
        basicSalaryField.setPromptText("Enter New Basic Salary");

        allowanceField = new TextField();
        allowanceField.setPromptText("Enter New Allowance");

        submitButton = new Button("Submit");
        submitButton.setOnAction(e -> handleSubmit());

        // Layout for the form
        VBox form = new VBox(10, basicSalaryField, allowanceField, submitButton);
        form.setStyle("-fx-padding: 20px; -fx-background-color: white; -fx-border-radius: 15px; -fx-background-radius: 15px;");
        content.setBody(form);
        content.setPrefSize(400, 250);

        // Add title with role name dynamically
        header.setTitle("Change Salary for Role: " + roleName);

        // Set border style
        content.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 15px;");
    }

    private void handleSubmit() {
        try {
            // Get values from the fields
            double basicSalary = Double.parseDouble(basicSalaryField.getText());
            double allowance = Double.parseDouble(allowanceField.getText());

            // Update the database with new values
            updateSalaryAndAllowance(basicSalary, allowance);
            close(); // Close the dialog after submission
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values for salary and allowance.");
            alert.showAndWait();
        }
    }

    private void updateSalaryAndAllowance(double basicSalary, double allowance) {
        String updateQuery = "UPDATE role SET basic_salary = ?, allowance = ? WHERE id = ?";

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setDouble(1, basicSalary);
            stmt.setDouble(2, allowance);
            stmt.setInt(3, roleId); // Use the stored role ID for updating the specific role

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Salary and allowance updated successfully.");
            } else {
                System.out.println("Failed to update salary and allowance.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    // Set the role details for which the salary is to be changed
    public void setRoleDetails(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
        header.setTitle("Change Salary for Role: " + roleName); // Update the title dynamically
    }
}
