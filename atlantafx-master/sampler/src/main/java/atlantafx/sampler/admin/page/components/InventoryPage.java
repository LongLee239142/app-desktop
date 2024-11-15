package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.entity.Supply;
import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.sql.*;

public final class InventoryPage extends OutlinePage {
    public static final String NAME = "Inventory";

    private TableView<Supply> table;
    private Pagination pagination;
    private ObservableList<Supply> allSupplies;

    private ComboBox<String> supplierFilter;
    private TextField searchField;
    private Button resetButton;

    @Override
    public String getName() {
        return NAME;
    }

    public InventoryPage() {
        super();
        createTable();
        loadSuppliers();
        loadData();
    }

    private void createTable() {
        table = new TableView<>();
        initializeTableColumns();
        setupPagination();
        createFilterControls();

        VBox layout = new VBox(supplierFilter, searchField, resetButton, pagination);
        layout.getChildren().addAll(table);
        getChildren().addAll(layout);
    }

    private void initializeTableColumns() {
        table.getColumns().addAll(
                createTableColumn("Supply Code", "supplyCode"),
                createTableColumn("Name", "name"),
                createTableColumn("Unit", "unit"),
                createTableColumn("Price", "price"),
                createTableColumn("Quantity", "quantity"),
                createTableColumn("Supplier Name", "supplierName"),
                createTableColumn("Total Value", "totalValue")
        );
    }

    private TableColumn<Supply, ?> createTableColumn(String title, String property) {
        TableColumn<Supply, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
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

        resetButton = new Button("Reset Filters");
        resetButton.setOnAction(e -> resetFilters());
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
        ObservableList<Supply> data = loadDataForPage(pageIndex);
        table.setItems(data);
        return new StackPane(table);
    }

    private ObservableList<Supply> loadDataForPage(int pageIndex) {
        ObservableList<Supply> supplies = FXCollections.observableArrayList();
        Connection connection = JDBCConnect.getJDBCConnection();

        StringBuilder query = new StringBuilder("SELECT s.id, s.supply_code, s.name, s.unit, s.price, s.quantity, su.name AS supplier_name " +
                "FROM supplies s JOIN suppliers su ON s.suppliers_id = su.suppliers_id "); // Update here

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


    private String buildWhereClause() {
        StringBuilder whereClause = new StringBuilder();
        boolean hasSupplierFilter = supplierFilter.getValue() != null && !supplierFilter.getValue().isEmpty();
        boolean hasSearchText = !searchField.getText().isEmpty();

        if (hasSupplierFilter) {
            whereClause.append("WHERE su.name = ? ");
        }

        if (hasSearchText) {
            if (hasSupplierFilter) {
                whereClause.append("AND ");
            } else {
                whereClause.append("WHERE ");
            }
            whereClause.append("s.name LIKE ? ");
        }
        return whereClause.toString();
    }

    private void setQueryParameters(PreparedStatement statement, int pageIndex) throws SQLException {
        int paramIndex = 1;
        if (supplierFilter.getValue() != null && !supplierFilter.getValue().isEmpty()) {
            statement.setString(paramIndex++, supplierFilter.getValue());
        }

        if (!searchField.getText().isEmpty()) {
            statement.setString(paramIndex++, "%" + searchField.getText() + "%");
        }

        statement.setInt(paramIndex, pageIndex * 10);
    }

    private void loadData() {
        String countQuery = "SELECT COUNT(*) AS total FROM supplies";
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(countQuery);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                int totalRecords = resultSet.getInt("total");
                pagination.setPageCount((int) Math.ceil((double) totalRecords / 10));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTable() {
        loadData();
        createPage(0); // Reset to the first page after filtering
    }

    private void resetFilters() {
        supplierFilter.setValue(null);
        searchField.clear();
        updateTable(); // Refresh the table with all data
    }
}
