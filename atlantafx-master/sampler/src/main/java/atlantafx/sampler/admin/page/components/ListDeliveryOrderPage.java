package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.entity.DeliveryOrderItem;
import atlantafx.sampler.admin.entity.ImportOrderItem;
import atlantafx.sampler.admin.entity.ListDeliveryOrder;
import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ListDeliveryOrderPage extends OutlinePage {
    public static final String NAME = "Delivery Orders";

    private TableView<ListDeliveryOrder> table;
    // Declare the importOrderItems list at the class level (if it's used across multiple methods)
    private List<ImportOrderItem> importOrderItems = new ArrayList<>();

    private void initializeImportOrderItems() {
        importOrderItems.add(new ImportOrderItem(1, new BigDecimal("10.0"), new BigDecimal("9.0"), new BigDecimal("100.0"))); // Example item with price
        importOrderItems.add(new ImportOrderItem(2, new BigDecimal("5.0"), new BigDecimal("5.0"), new BigDecimal("50.0")));  // Another example item
    }

    private Pagination pagination;

    @Override
    public String getName() {
        return NAME;
    }

    public ListDeliveryOrderPage() {
        super();
        createTable();
        loadData();
    }

    private void createTable() {
        table = new TableView<>();
        initializeTableColumns();
        setupPagination();

        // Ensure the table has a preferred size
        table.setPrefWidth(800);
        table.setPrefHeight(600);

        VBox layout = new VBox(table, pagination);
        layout.setPrefWidth(800);
        layout.setPrefHeight(600);
        getChildren().addAll(layout);
    }

    private void initializeTableColumns() {
        table.getColumns().addAll(
                createTableColumn("Order Number", "orderNumber"),
                createTableColumn("Supplier ID", "supplierId"),
                createTableColumn("Order Date", "orderDate"),
                createTableColumn("Status", "status"),
                createActionButtonColumn() // Thêm cột cho nút nhập hàng
        );
    }

    private TableColumn<ListDeliveryOrder, Void> createActionButtonColumn() {
        TableColumn<ListDeliveryOrder, Void> colBtn = new TableColumn<>("Action");

        Callback<TableColumn<ListDeliveryOrder, Void>, TableCell<ListDeliveryOrder, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<ListDeliveryOrder, Void> call(final TableColumn<ListDeliveryOrder, Void> param) {
                final TableCell<ListDeliveryOrder, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Nhập hàng");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ListDeliveryOrder order = getTableView().getItems().get(getIndex());
                            showOrderDetailPopup(order); // Gọi hàm để hiển thị popup
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        return colBtn;
    }

    private TableColumn<ListDeliveryOrder, ?> createTableColumn(String title, String property) {
        TableColumn<ListDeliveryOrder, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private void setupPagination() {
        pagination = new Pagination();
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory(this::createPage);
    }

    private StackPane createPage(int pageIndex) {
        ObservableList<ListDeliveryOrder> data = loadDataForPage(pageIndex);
        table.setItems(data);
        return new StackPane(table);
    }

    private ObservableList<ListDeliveryOrder> loadDataForPage(int pageIndex) {
        ObservableList<ListDeliveryOrder> orders = FXCollections.observableArrayList();
        Connection connection = JDBCConnect.getJDBCConnection();

        String query = "SELECT order_number, supplier_id, order_date, status FROM delivery_order ";
        query += "LIMIT 10 OFFSET " + (pageIndex * 10);

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                orders.add(new ListDeliveryOrder(
                        resultSet.getString("order_number"),
                        resultSet.getString("supplier_id"),
                        resultSet.getTimestamp("order_date").toLocalDateTime(),
                        resultSet.getString("status")
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Loaded " + orders.size() + " orders for page " + pageIndex); // Debugging line
        return orders;
    }

    private void loadData() {
        String countQuery = "SELECT COUNT(*) AS total FROM delivery_order"; // Adjust your table name
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

    private List<DeliveryOrderItem> loadOrderItems(String orderNumber) {
        List<DeliveryOrderItem> items = new ArrayList<>();
        String query = "SELECT doi.supply_code, doi.quantity, s.price " +
                "FROM delivery_order_items doi " +
                "JOIN supplies s ON doi.supply_code = s.supply_code " +
                "WHERE doi.order_number = ?";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, orderNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String supplyCode = resultSet.getString("supply_code");
                    BigDecimal quantity = resultSet.getBigDecimal("quantity");
                    BigDecimal price = resultSet.getBigDecimal("price");
                    items.add(new DeliveryOrderItem(supplyCode, quantity, price));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }


    private void showOrderDetailPopup(ListDeliveryOrder order) {
        Stage popupStage = new Stage();
        VBox vbox = new VBox();

        // Hiển thị thông tin đơn hàng
        Label orderNumberLabel = new Label("Order Number: " + order.getOrderNumber());
        Label supplierIdLabel = new Label("Supplier ID: " + order.getSupplierId());
        Label orderDateLabel = new Label("Order Date: " + order.getOrderDate().toString());

        // Tạo bảng để hiển thị các mục hàng hóa
        TableView<DeliveryOrderItem> itemsTable = new TableView<>();
        TableColumn<DeliveryOrderItem, String> supplyCodeCol = new TableColumn<>("Supply Code");
        supplyCodeCol.setCellValueFactory(new PropertyValueFactory<>("supplyCode"));

        TableColumn<DeliveryOrderItem, BigDecimal> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter())); // Cho phép chỉnh sửa

        TableColumn<DeliveryOrderItem, BigDecimal> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<DeliveryOrderItem, BigDecimal> totalValueCol = new TableColumn<>("Total Value");
        totalValueCol.setCellValueFactory(new PropertyValueFactory<>("totalValue"));

        itemsTable.getColumns().addAll(supplyCodeCol, quantityCol, priceCol, totalValueCol);
        itemsTable.setItems(FXCollections.observableArrayList(loadOrderItems(order.getOrderNumber())));



        Button saveOrderButton = new Button("Lưu đơn hàng");
        saveOrderButton.setOnAction(e -> {
            // Gọi hàm lưu đơn hàng
            saveOrder();
        });

        // Thêm tất cả vào layout
        vbox.getChildren().addAll(orderNumberLabel, supplierIdLabel, orderDateLabel, itemsTable,  saveOrderButton);
        Scene scene = new Scene(vbox, 500, 400); // Thay đổi kích thước nếu cần
        popupStage.setScene(scene);
        popupStage.setTitle("Chi tiết đơn hàng");
        popupStage.show();
    }



    private void saveOrder() {
        initializeImportOrderItems();
        try (Connection connection = JDBCConnect.getJDBCConnection()) {
            int supplierId = 1; // Example supplier_id, can be taken from UI
            LocalDateTime deliveryDate = LocalDateTime.now(); // Current date
            BigDecimal totalValue = calculateTotalValue(); // Calculate total order value

            // Step 1: Save order into import_order table and retrieve generated order ID
            int importOrderId = insertImportOrder(connection, supplierId, deliveryDate, totalValue);

            // Step 2: Save order details into import_order_detail table
            saveOrderDetails(connection, importOrderId);

            // Step 3: Update the supplies table with received quantities
            updateSuppliesQuantity(connection);

            System.out.println("Order has been successfully saved!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int insertImportOrder(Connection connection, int supplierId, LocalDateTime deliveryDate, BigDecimal totalValue) throws SQLException {
        String insertOrderSQL = "INSERT INTO import_order (supplier_id, delivery_date, total_value) VALUES (?, ?, ?)";
        try (PreparedStatement orderStmt = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
            orderStmt.setInt(1, supplierId);
            orderStmt.setTimestamp(2, Timestamp.valueOf(deliveryDate));
            orderStmt.setBigDecimal(3, totalValue);

            int affectedRows = orderStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
    }

    private void saveOrderDetails(Connection connection, int importOrderId) throws SQLException {
        String insertOrderDetailSQL = "INSERT INTO import_order_detail (import_order_id, supply_id, ordered_quantity, received_quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement detailStmt = connection.prepareStatement(insertOrderDetailSQL)) {
            for (ImportOrderItem item : importOrderItems) {
                detailStmt.setInt(1, importOrderId); // ID of the created order
                detailStmt.setInt(2, item.getSupplyId()); // Supply ID
                detailStmt.setBigDecimal(3, item.getOrderedQuantity()); // Ordered quantity
                detailStmt.setBigDecimal(4, item.getQuantity()); // Received quantity
                detailStmt.addBatch(); // Add to batch for bulk execution
            }
            detailStmt.executeBatch(); // Execute the batch
        }
    }

    private void updateSuppliesQuantity(Connection connection) throws SQLException {
        String updateSupplySQL = "UPDATE supplies SET quantity = quantity + ? WHERE id = ?";
        try (PreparedStatement updateSupplyStmt = connection.prepareStatement(updateSupplySQL)) {
            for (ImportOrderItem item : importOrderItems) {
                updateSupplyStmt.setBigDecimal(1, item.getQuantity()); // Update received quantity
                updateSupplyStmt.setInt(2, item.getSupplyId()); // Update based on supply ID
                updateSupplyStmt.addBatch(); // Add to batch for bulk execution
            }
            updateSupplyStmt.executeBatch(); // Execute the batch
        }
    }

    private BigDecimal calculateTotalValue() {
        BigDecimal totalValue = BigDecimal.ZERO;

        for (ImportOrderItem item : importOrderItems) {
            BigDecimal itemTotal = item.getPrice().multiply(item.getQuantity());
            totalValue = totalValue.add(itemTotal); // Sum up the item totals
        }

        return totalValue;
    }

}
