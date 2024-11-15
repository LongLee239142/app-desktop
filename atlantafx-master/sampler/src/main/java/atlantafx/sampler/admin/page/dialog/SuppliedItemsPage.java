package atlantafx.sampler.admin.page.dialog;

import atlantafx.sampler.admin.entity.Supply;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

import java.sql.Connection;
import java.sql.PreparedStatement; // Added import
import java.sql.ResultSet;
import java.sql.SQLException;

public class SuppliedItemsPage extends VBox {
    private final String supplierId;
    private TableView<Supply> table;
    private ObservableList<Supply> supplies; // ObservableList to hold supplies
    private TextField searchField; // TextField for searching

    public SuppliedItemsPage(String supplierId) {
        this.supplierId = supplierId;
        createSearchField(); // Create the search field
        createTable();
        createAddButton(); // Create the add button
        loadSuppliedItems();
    }

    private void createSearchField() {
        searchField = new TextField();
        searchField.setPromptText("Search by name or code...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSupplies(newValue);
        });
        getChildren().add(searchField); // Add search field to the layout
    }

    private void createTable() {
        table = new TableView<>();
        supplies = FXCollections.observableArrayList(); // Initialize the supplies list

        // Define columns
        TableColumn<Supply, String> supplyCodeCol = new TableColumn<>("Supply Code");
        supplyCodeCol.setCellValueFactory(new PropertyValueFactory<>("supplyCode"));

        TableColumn<Supply, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Supply, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<Supply, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Supply, Double> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Create action column for Edit and Delete
        TableColumn<Supply, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<Supply, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(e -> editSupply(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> deleteSupply(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        // Add columns to table
        table.getColumns().addAll(supplyCodeCol, nameCol, unitCol, priceCol, quantityCol, actionCol);
        getChildren().add(table);
    }

    private void createAddButton() {
        Button addButton = new Button("Add New Supply");
        addButton.setOnAction(e -> openAddSupplyDialog());
        getChildren().add(addButton); // Add the button to the layout
    }

    private void loadSuppliedItems() {
        String query = "SELECT supply_code, name, unit, price, quantity FROM supplies WHERE suppliers_id = ?";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, supplierId);
            ResultSet resultSet = statement.executeQuery();
            supplies.clear(); // Clear the existing supplies list before loading new data
            while (resultSet.next()) {
                Supply supply = new Supply(
                        resultSet.getString("supply_code"),
                        resultSet.getString("name"),
                        resultSet.getString("unit"),
                        resultSet.getDouble("price"),
                        resultSet.getDouble("quantity"),
                        supplierId // Pass the supplierId here
                );
                supplies.add(supply); // Add to the supplies list
            }
            table.setItems(supplies);
        } catch (SQLException e) {
            showErrorDialog("Error loading supplied items: " + e.getMessage());
        }
    }

    private void openAddSupplyDialog() {
        // Create a dialog for adding a new supply
        Dialog<Supply> dialog = new Dialog<>();
        dialog.setTitle("Add New Supply");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Create fields for entering supply details
        TextField supplyCodeField = new TextField();
        TextField nameField = new TextField();
        TextField unitField = new TextField();
        TextField priceField = new TextField();

        // Set up the dialog layout
        GridPane grid = new GridPane();
        grid.add(new Label("Supply Code:"), 0, 0);
        grid.add(supplyCodeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Unit:"), 0, 2);
        grid.add(unitField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                // Create a new Supply object with quantity set to 0
                Supply newSupply = new Supply(
                        supplyCodeField.getText(),
                        nameField.getText(),
                        unitField.getText(),
                        Double.parseDouble(priceField.getText()),
                        0.0, // Set quantity to 0
                        supplierId // Pass the supplierId here
                );

                // Add the new supply to the database
                addNewSupply(newSupply);
                return newSupply;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void addNewSupply(Supply supply) {
        String query = "INSERT INTO supplies (supply_code, name, unit, price, quantity, suppliers_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, supply.getSupplyCode());
            statement.setString(2, supply.getName());
            statement.setString(3, supply.getUnit());
            statement.setDouble(4, supply.getPrice());
            statement.setDouble(5, supply.getQuantity());
            statement.setString(6, supplierId);
            statement.executeUpdate();

            // Reload the supplied items after adding new supply
            loadSuppliedItems();
        } catch (SQLException e) {
            showErrorDialog("Error adding new supply: " + e.getMessage());
        }
    }

    private void filterSupplies(String searchTerm) {
        ObservableList<Supply> filteredSupplies = FXCollections.observableArrayList();

        for (Supply supply : supplies) {
            if (supply.getSupplyCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    supply.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                filteredSupplies.add(supply);
            }
        }
        table.setItems(filteredSupplies);
    }

    private void editSupply(Supply supply) {
        // Create a dialog for editing supply details
        Dialog<Supply> dialog = new Dialog<>();
        dialog.setTitle("Edit Supply");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Create fields for editing
        TextField nameField = new TextField(supply.getName());
        TextField unitField = new TextField(supply.getUnit());
        TextField priceField = new TextField(String.valueOf(supply.getPrice()));

        // Set up the dialog layout
        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Unit:"), 0, 1);
        grid.add(unitField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                // Update the supply details
                supply.setName(nameField.getText());
                supply.setUnit(unitField.getText());
                supply.setPrice(Double.parseDouble(priceField.getText()));

                // Update the database
                updateSupply(supply);
                return supply;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void updateSupply(Supply supply) {
        String query = "UPDATE supplies SET name = ?, unit = ?, price = ? WHERE supply_code = ? AND suppliers_id = ?";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, supply.getName());
            statement.setString(2, supply.getUnit());
            statement.setDouble(3, supply.getPrice());
            statement.setString(4, supply.getSupplyCode());
            statement.setString(5, supplierId);
            statement.executeUpdate();

            // Reload the supplied items after update
            loadSuppliedItems();
        } catch (SQLException e) {
            showErrorDialog("Error updating supply: " + e.getMessage());
        }
    }


    private void deleteSupply(Supply supply) {
        // Check if the quantity is greater than 0
        if (supply.getQuantity() > 0) {
            // Alert the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Cannot Delete Supply");
            alert.showAndWait();
            return; // Exit the method if the quantity is greater than 0
        }

        // If quantity is 0, ask for confirmation to delete
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this supply?");
        confirmationAlert.setContentText("This action cannot be undone.");

        // Add confirmation buttons
        ButtonType confirmButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel");
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == confirmButton) {
                // Proceed with deletion
                String query = "DELETE FROM supplies WHERE supply_code = ? AND suppliers_id = ?";
                try (Connection connection = JDBCConnect.getJDBCConnection();
                     PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, supply.getSupplyCode());
                    statement.setString(2, supplierId);
                    statement.executeUpdate();

                    // Reload the supplied items after deletion
                    loadSuppliedItems();
                } catch (SQLException e) {
                    showErrorDialog("Error deleting supply: " + e.getMessage());
                }
            }
        });
    }


    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
