package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.entity.Supply;
import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public final class InventoryPage extends OutlinePage {
    public static final String NAME = "Inventory";

    private TableView<Supply> table;
    private Pagination pagination;
    private ObservableList<Supply> allSupplies;

    private ComboBox<String> supplierFilter;
    private TextField searchField;

    @Override
    public String getName() {
        return NAME;
    }

    public InventoryPage() {
        super();
        createTable();
        loadSuppliers();
        loadData(0);  // Load initial data for page 0
    }

    private void createTable() {
        table = new TableView<>();
        initializeTableColumns();
        setupPagination();
        createFilterControls();

        VBox layout = new VBox(supplierFilter, searchField, pagination);
        layout.getChildren().addAll(table);
        getChildren().addAll(layout);
    }

    private void initializeTableColumns() {
        // Add columns for displaying supply data
        table.getColumns().addAll(
                createTableColumn("Supply Code", "supplyCode"),
                createTableColumn("Name", "name"),
                createTableColumn("Unit", "unit"),
                createTableColumn("Price", "price"),
                createTableColumn("Quantity", "quantity"),
                createTableColumn("Supplier Name", "supplierName"),
                createTableColumn("Total Value", "totalValue"),
                createActionColumn() // Add the update button column
        );
    }

    private TableColumn<Supply, ?> createTableColumn(String title, String property) {
        TableColumn<Supply, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private TableColumn<Supply, Void> createActionColumn() {
        TableColumn<Supply, Void> actionColumn = new TableColumn<>("Action");

        // Create a cell factory to create buttons for each row
        actionColumn.setCellFactory(param -> new TableCell<Supply, Void>() {
            private final Button updateButton = new Button("Update Quantity");

            {
                // Set button action
                updateButton.setOnAction(event -> updateQuantity(getTableRow().getItem()));
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(updateButton);
                }
            }
        });

        return actionColumn;
    }

    private void setupPagination() {
        pagination = new Pagination();
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory(this::createPage);
    }

    private void createFilterControls() {
        supplierFilter = new ComboBox<>();
        supplierFilter.setOnAction(e -> updateTable());

        searchField = new TextField();
        searchField.setPromptText("Search by name");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateTable());
    }

    private void loadSuppliers() {
        String query = "SELECT name FROM suppliers";
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                supplierFilter.getItems().add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private StackPane createPage(int pageIndex) {
        loadData(pageIndex);  // Load data for the current page index
        return new StackPane(table);
    }

    private void loadData(int pageIndex) {
        ObservableList<Supply> data = loadDataForPage(pageIndex);
        table.setItems(data);
    }

    private ObservableList<Supply> loadDataForPage(int pageIndex) {
        ObservableList<Supply> supplies = FXCollections.observableArrayList();
        Connection connection = JDBCConnect.getJDBCConnection();

        StringBuilder query = new StringBuilder("SELECT s.id, s.supply_code, s.name, s.unit, s.price, s.quantity, su.name AS supplier_name " +
                "FROM supplies s JOIN suppliers su ON s.suppliers_id = su.suppliers_id ");

        String whereClause = "";
        if (supplierFilter.getValue() != null && !supplierFilter.getValue().isEmpty()) {
            whereClause += "WHERE su.name = '" + supplierFilter.getValue() + "' ";
        }

        if (!searchField.getText().isEmpty()) {
            if (!whereClause.isEmpty()) {
                whereClause += "AND ";
            } else {
                whereClause += "WHERE ";
            }
            whereClause += "s.name LIKE '%" + searchField.getText() + "%' ";
        }

        query.append(whereClause);
        query.append("LIMIT 10 OFFSET ").append(pageIndex * 10);

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query.toString())) {
            while (resultSet.next()) {
                supplies.add(new Supply(
                        resultSet.getString("supply_code"),
                        resultSet.getString("name"),
                        resultSet.getString("unit"),
                        resultSet.getDouble("price"),
                        resultSet.getDouble("quantity"),
                        resultSet.getString("supplier_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supplies;
    }

    private void updateTable() {
        // Reload the data with the current filter and search criteria
        pagination.setCurrentPageIndex(0);  // Reset to the first page
        loadData(0);
    }

    private void updateQuantity(Supply selectedSupply) {
        // If no supply is selected, show an error dialog
        if (selectedSupply == null) {
            showErrorDialog("No product selected", "Please select a product to update the quantity.");
            return;
        }

        // Show a dialog to enter the new quantity
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Quantity");
        dialog.setHeaderText("Enter the new quantity for " + selectedSupply.getName());
        dialog.setContentText("New Quantity:");

        // Set the default value to the current quantity of the supply
        dialog.getEditor().setText(String.valueOf(selectedSupply.getQuantity()));

        // Handle when the user presses OK
        dialog.showAndWait().ifPresent(newQuantity -> {
            try {
                // Validate if the entered quantity is a valid number (non-negative integer)
                int quantity = Integer.parseInt(newQuantity);
                if (quantity < 0) {
                    showErrorDialog("Invalid Quantity", "Quantity cannot be negative.");
                    return;
                }

                // Update the quantity in the database
                String updateQuery = "UPDATE supplies SET quantity = ? WHERE id = ?";
                try (Connection connection = JDBCConnect.getJDBCConnection();
                     PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                    statement.setInt(1, quantity);
                    statement.setInt(2, selectedSupply.getId());

                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        // If update is successful, refresh the table
                        selectedSupply.setQuantity(quantity); // Update the quantity in the table view
                        table.refresh(); // Refresh the table to show updated quantity
                        showInfoDialog("Quantity Updated", "The quantity has been updated successfully.");
                    } else {
                        showErrorDialog("Update Failed", "Failed to update the quantity.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorDialog("Database Error", "An error occurred while updating the quantity.");
                }
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input", "Please enter a valid number for the quantity.");
            }
        });
    }

    // Method to show error dialogs
    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to show success information dialogs
    private void showInfoDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
