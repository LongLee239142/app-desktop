package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class ImportPage extends OutlinePage {
    public static final String NAME = "Import";
    private TextField orderNumberInput;
    private TableView<DeliveryOrderItem> tableView;
    private Button saveButton;

    @Override
    public String getName() {
        return NAME;
    }

    public ImportPage() {
        super();
        initializeUI();
    }

    public void initializeUI() {
        orderNumberInput = new TextField();
        orderNumberInput.setPromptText("Enter Order Number");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> searchOrder());

        saveButton = new Button("Save");
        saveButton.setOnAction(event -> saveData());

        VBox vbox = new VBox(orderNumberInput, searchButton, tableView, saveButton);
        getChildren().add(vbox);
    }

    private void searchOrder() {
        String orderNumber = orderNumberInput.getText();
        List<DeliveryOrderItem> items = fetchDeliveryOrderItems(orderNumber);
        ObservableList<DeliveryOrderItem> data = FXCollections.observableArrayList(items);
        tableView.setItems(data);
    }

    private List<DeliveryOrderItem> fetchDeliveryOrderItems(String orderNumber) {
        List<DeliveryOrderItem> items = new ArrayList<>();
        String query = "SELECT doi.supply_code, doi.quantity " +
                "FROM delivery_order_items doi " +
                "JOIN delivery_order do ON doi.order_number = do.order_number " +
                "WHERE do.order_number = ?";

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, orderNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String supplyCode = rs.getString("supply_code");
                    double quantity = rs.getDouble("quantity");
                    items.add(new DeliveryOrderItem(supplyCode, quantity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private void saveData() {
        String orderNumber = orderNumberInput.getText();
        double totalValue = 0.0;

        // 1. Lưu thông tin vào import_order
        String insertImportOrderQuery = "INSERT INTO import_order (supplier_id, delivery_date, total_value) VALUES (?, ?, ?)";

        // Thay đổi id của nhà cung cấp dựa trên order_number
        int supplierId = getSupplierIdByOrderNumber(orderNumber);
        if (supplierId == -1) {
            // Thông báo nếu không tìm thấy nhà cung cấp
            showAlert("Supplier not found for order number: " + orderNumber);
            return;
        }

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement importOrderStmt = conn.prepareStatement(insertImportOrderQuery, Statement.RETURN_GENERATED_KEYS)) {

            importOrderStmt.setInt(1, supplierId);
            importOrderStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            importOrderStmt.setDouble(3, totalValue);
            importOrderStmt.executeUpdate();

            // Lấy ID của import_order mới tạo
            ResultSet generatedKeys = importOrderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int importOrderId = generatedKeys.getInt(1);

                // 2. Lưu thông tin vào import_order_detail
                for (DeliveryOrderItem item : tableView.getItems()) {
                    double receivedQuantity = item.getQuantity(); // Lấy số lượng nhận được từ TableView

                    // Cập nhật tổng giá trị (ví dụ: tính toán theo giá từ bảng supplies)
                    double unitPrice = getUnitPriceBySupplyCode(item.getSupplyCode());
                    totalValue += unitPrice * receivedQuantity;

                    // Lưu vào bảng import_order_detail
                    String insertImportOrderDetailQuery = "INSERT INTO import_order_detail (import_order_id, supply_id, ordered_quantity, received_quantity) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement importOrderDetailStmt = conn.prepareStatement(insertImportOrderDetailQuery)) {
                        importOrderDetailStmt.setInt(1, importOrderId);
                        importOrderDetailStmt.setInt(2, getSupplyIdBySupplyCode(item.getSupplyCode()));
                        importOrderDetailStmt.setDouble(3, receivedQuantity); // ordered_quantity
                        importOrderDetailStmt.setDouble(4, receivedQuantity); // received_quantity
                        importOrderDetailStmt.executeUpdate();
                    }

                    // 3. Cập nhật received_quantity vào bảng supplies
                    updateReceivedQuantityInSupplies(item.getSupplyCode(), receivedQuantity);

                    // 4. Cập nhật ordered_quantity thành received_quantity trong delivery_order_items
                    updateOrderedQuantityInDeliveryOrderItems(orderNumber, item.getSupplyCode(), receivedQuantity);
                }

                // Cập nhật tổng giá trị vào bảng import_order
                updateTotalValueInImportOrder(importOrderId, totalValue);
                showAlert("Import order saved successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error while saving import order: " + e.getMessage());
        }
    }

    // Phương thức giúp hiển thị thông báo
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Lấy ID nhà cung cấp từ order_number
    private int getSupplierIdByOrderNumber(String orderNumber) {
        String query = "SELECT supplier_id FROM delivery_order WHERE order_number = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("supplier_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }


    // Lấy đơn giá từ supply_code
    private double getUnitPriceBySupplyCode(String supplyCode) {
        String query = "SELECT unit_price FROM supplies WHERE supply_code = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, supplyCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("unit_price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Trả về 0.0 nếu không tìm thấy
    }

    // Lấy ID của supplies từ supply_code
    private int getSupplyIdBySupplyCode(String supplyCode) {
        String query = "SELECT id FROM supplies WHERE supply_code = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, supplyCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }

    // Cập nhật received_quantity vào bảng supplies
    private void updateReceivedQuantityInSupplies(String supplyCode, double receivedQuantity) {
        String updateQuery = "UPDATE supplies SET quantity = quantity + ? WHERE supply_code = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDouble(1, receivedQuantity);
            stmt.setString(2, supplyCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật ordered_quantity trong delivery_order_items
    private void updateOrderedQuantityInDeliveryOrderItems(String orderNumber, String supplyCode, double receivedQuantity) {
        String updateQuery = "UPDATE delivery_order_items SET quantity = ? WHERE order_number = ? AND supply_code = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDouble(1, receivedQuantity);
            stmt.setString(2, orderNumber);
            stmt.setString(3, supplyCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật tổng giá trị trong import_order
    private void updateTotalValueInImportOrder(int importOrderId, double totalValue) {
        String updateQuery = "UPDATE import_order SET total_value = ? WHERE id = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDouble(1, totalValue);
            stmt.setInt(2, importOrderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Định nghĩa lớp DeliveryOrderItem để hiển thị trong TableView
    public static class DeliveryOrderItem {
        private SimpleStringProperty supplyCode;
        private SimpleDoubleProperty quantity;

        public DeliveryOrderItem(String supplyCode, double quantity) {
            this.supplyCode = new SimpleStringProperty(supplyCode);
            this.quantity = new SimpleDoubleProperty(quantity);
        }

        public String getSupplyCode() {
            return supplyCode.get();
        }

        public double getQuantity() {
            return quantity.get();
        }
    }
}
