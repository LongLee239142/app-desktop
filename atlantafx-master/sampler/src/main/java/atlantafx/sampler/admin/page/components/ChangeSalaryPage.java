package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.admin.page.dialog.ChangeSalaryDialog;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.staff.Role;
import atlantafx.sampler.base.util.Lazy;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ChangeSalaryPage extends OutlinePage {
    public static final String NAME = "Change Salary";

    private TableView<Role> tableView;
    private final Lazy<ChangeSalaryDialog> changeSalaryDialog;

    public ChangeSalaryPage() {
        super();
        initializeUI();
        loadData();
        changeSalaryDialog = new Lazy<>(() -> {
            var dialog = new ChangeSalaryDialog();
            dialog.setClearOnClose(true);
            return dialog;
        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initializeUI() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // Title Label
        Label titleLabel = new Label(NAME);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        layout.setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.TOP_LEFT);

        // TableView for displaying role data
        tableView = new TableView<>();
        tableView.setEditable(true);

        TableColumn<Role, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Role, String> roleNameColumn = new TableColumn<>("Role Name");
        roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));

        TableColumn<Role, Double> basicSalaryColumn = new TableColumn<>("Basic Salary");
        basicSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("basicSalary"));

        TableColumn<Role, Double> allowanceColumn = new TableColumn<>("Allowance");
        allowanceColumn.setCellValueFactory(new PropertyValueFactory<>("allowance"));

        // Action Column with "Change Salary" button
        TableColumn<Role, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button changeSalaryButton = new Button("Change Salary");

            {
                changeSalaryButton.setOnAction(event -> {
                    Role role = getTableView().getItems().get(getIndex());
                    openChangeSalaryDialog(role);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : changeSalaryButton);
            }
        });

        tableView.getColumns().addAll(idColumn, roleNameColumn, basicSalaryColumn, allowanceColumn, actionColumn);

        // Layout for the Table
        VBox vbox = new VBox(10, tableView);
        vbox.setAlignment(Pos.CENTER);
        layout.setCenter(vbox);

        // Add layout to the main scene
        this.getChildren().add(layout);
    }

    private void loadData() {
        String query = "SELECT id, role_name, basic_salary, allowance FROM role";
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            tableView.setItems(FXCollections.observableArrayList()); // Clear current data

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String roleName = resultSet.getString("role_name");
                double basicSalary = resultSet.getDouble("basic_salary");
                double allowance = resultSet.getDouble("allowance");

                Role role = new Role(id, roleName, basicSalary, allowance);
                tableView.getItems().add(role); // Add new role to table
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    private void openChangeSalaryDialog(Role role) {
        var dialog = changeSalaryDialog.get(); // Get the dialog instance
        dialog.setRoleDetails(role.getId(), role.getRoleName()); // Set the role details using the correct method
        dialog.show(getScene()); // Show the dialog
    }

    private void refreshTable() {
        loadData(); // Reload the data in the table after salary changes
    }
}
