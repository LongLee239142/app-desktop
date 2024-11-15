package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.entity.Supplier;
import atlantafx.sampler.admin.page.dialog.EditSupplierDialog;
import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.admin.page.dialog.SuppliedItemsPage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SupplierPage extends OutlinePage {
    public static final String NAME = "Supplier";

    private TableView<Supplier> table;
    private ObservableList<Supplier> allSuppliers;
    private TextField searchField;

    @Override
    public String getName() {
        return NAME;
    }

    public SupplierPage() {
        super();
        createTable();
        loadData();
    }

    private void createTable() {
        table = new TableView<>();

        // Define columns
        TableColumn<Supplier, String> idCol = new TableColumn<>("Supplier ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("suppliersId"));

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Supplier Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Supplier, String> contactNumberCol = new TableColumn<>("Contact Number");
        contactNumberCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Create action columns for Edit and View Supplied Items
        TableColumn<Supplier, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<Supplier, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button viewButton = new Button("Danh sách hàng cung cấp");

            {
                editButton.setOnAction(e -> editSupplierInfo(getTableView().getItems().get(getIndex())));
                viewButton.setOnAction(e -> viewSuppliedItems(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, viewButton);
                    setGraphic(buttons);
                }
            }
        });

        // Add columns to table
        table.getColumns().addAll(idCol, nameCol, contactNumberCol, emailCol, actionCol);

        // Create search field
        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm nhà cung cấp...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTable(newValue));
// Create the "Add New Supplier" button and logic to open the form popup
        Button addButton = new Button("Thêm nhà cung cấp");
        addButton.setOnAction(e -> openAddSupplierDialog());  // Logic to handle the popup

        // Add the button below the table and search field
        VBox layout = new VBox(searchField, table, addButton);
        getChildren().addAll(layout);
    }

    private void loadData() {
        allSuppliers = FXCollections.observableArrayList();
        Connection connection = JDBCConnect.getJDBCConnection();
        String query = "SELECT suppliers_id, name, contact_number, email FROM suppliers";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                allSuppliers.add(new Supplier(
                        resultSet.getString("suppliers_id"),
                        resultSet.getString("name"),
                        resultSet.getString("contact_number"),
                        resultSet.getString("email")
                ));
            }
            table.setItems(allSuppliers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterTable(String searchTerm) {
        ObservableList<Supplier> filteredSuppliers = FXCollections.observableArrayList();
        for (Supplier supplier : allSuppliers) {
            if (supplier.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    supplier.getSuppliersId().toLowerCase().contains(searchTerm.toLowerCase())) {
                filteredSuppliers.add(supplier);
            }
        }
        table.setItems(filteredSuppliers);
    }

    private void editSupplierInfo(Supplier supplier) {
        if (supplier != null) {
            EditSupplierDialog dialog = new EditSupplierDialog(supplier);
            dialog.showAndWait();
            loadData(); // Reload data after editing
        }
    }

    private void viewSuppliedItems(Supplier supplier) {
        if (supplier != null) {
            SuppliedItemsPage suppliedItemsPage = new SuppliedItemsPage(supplier.getSuppliersId());
            // Logic to display the SuppliedItemsPage, you may use a new window or a dialog
            Stage stage = new Stage();
            Scene scene = new Scene(suppliedItemsPage);
            stage.setScene(scene);
            stage.setTitle("Danh sách hàng hóa của " + supplier.getName());
            stage.show();
        }
    }

    private void openAddSupplierDialog() {
        // Tạo một form popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Thêm nhà cung cấp mới");

        // Các trường nhập dữ liệu
        TextField nameField = new TextField();
        nameField.setPromptText("Tên nhà cung cấp");

        TextField contactNumberField = new TextField();
        contactNumberField.setPromptText("Số điện thoại liên lạc");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        // Layout cho form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Tên nhà cung cấp:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Số điện thoại:"), 0, 1);
        grid.add(contactNumberField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        Button saveButton = new Button("Lưu");
        saveButton.setOnAction(e -> {
            if (validateFields(nameField, contactNumberField, emailField)) {
                String newSupplierId = generateUniqueSupplierId();
                addNewSupplier(newSupplierId, nameField.getText(), contactNumberField.getText(), emailField.getText());
                popupStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng nhập đầy đủ thông tin!");
                alert.showAndWait();
            }
        });

        VBox popupLayout = new VBox(grid, saveButton);
        popupLayout.setSpacing(10);
        Scene popupScene = new Scene(popupLayout, 400, 300);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    // Phương thức kiểm tra xem các trường có được nhập đầy đủ không
    private boolean validateFields(TextField nameField, TextField contactNumberField, TextField emailField) {
        return !nameField.getText().isEmpty() && !contactNumberField.getText().isEmpty() && !emailField.getText().isEmpty();
    }

    // Phương thức tạo Supplier ID mới
    private String generateUniqueSupplierId() {
        Connection connection = JDBCConnect.getJDBCConnection();
        String newId = "10" + (int) (Math.random() * 1_000_000);  // Tạo 6 số ngẫu nhiên, cộng với "10" ở đầu

        try (Statement statement = connection.createStatement()) {
            String checkQuery = "SELECT COUNT(*) FROM suppliers WHERE suppliers_id = '" + newId + "'";
            ResultSet resultSet = statement.executeQuery(checkQuery);
            while (resultSet.next() && resultSet.getInt(1) > 0) {
                // Nếu trùng thì tạo lại ID mới
                newId = "10" + (int) (Math.random() * 1_000_000);
                resultSet = statement.executeQuery(checkQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }

    // Phương thức thêm nhà cung cấp mới vào cơ sở dữ liệu
    private void addNewSupplier(String supplierId, String name, String contactNumber, String email) {
        Connection connection = JDBCConnect.getJDBCConnection();
        String query = "INSERT INTO suppliers (suppliers_id, name, contact_number, email) VALUES ('"
                + supplierId + "', '" + name + "', '" + contactNumber + "', '" + email + "')";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            loadData();  // Tải lại dữ liệu để cập nhật bảng
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
