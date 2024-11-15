package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.entity.OrderItem;
import java.time.LocalDate;
import java.util.Random;
import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DeliveryOrderPage extends OutlinePage {
    public static final String NAME = "Order";

    private TableView<OrderItem> availableItemsTable;
    private TableView<OrderItem> orderItemsTable;
    private ObservableList<OrderItem> availableItems;
    private ObservableList<OrderItem> orderItems;
    private TextField supplierCodeField;
    private Label supplierNameLabel;
    private Button checkOrderButton;
    private DatePicker orderDatePicker;
    private Button saveOrderButton;

    @Override
    public String getName() {
        return NAME;
    }

    public DeliveryOrderPage() {
        super();
        initializeUI();
        orderItems = FXCollections.observableArrayList();
        availableItems = FXCollections.observableArrayList();
        createAvailableItemsTableColumns();
        createOrderTableColumns();
    }

    private void initializeUI() {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new javafx.geometry.Insets(20));

        supplierCodeField = new TextField();
        supplierCodeField.setPromptText("Nhập Mã Nhà Cung Cấp");
        supplierCodeField.textProperty().addListener((observable, oldValue, newValue) -> checkSupplier(newValue));
        mainLayout.getChildren().add(supplierCodeField);

        supplierNameLabel = new Label("Tên Nhà Cung Cấp: ");
        mainLayout.getChildren().add(supplierNameLabel);

        HBox tablesLayout = new HBox(10);
        availableItemsTable = new TableView<>();
        orderItemsTable = new TableView<>();

        tablesLayout.getChildren().addAll(availableItemsTable, orderItemsTable);
        mainLayout.getChildren().add(tablesLayout);

        orderDatePicker = new DatePicker();
        orderDatePicker.setPromptText("Chọn Ngày Đặt Hàng");
        mainLayout.getChildren().add(orderDatePicker);



        saveOrderButton = new Button("Lưu Đơn Hàng");
        saveOrderButton.setOnAction(e -> saveOrder());
        mainLayout.getChildren().add(saveOrderButton);

        Scene scene = new Scene(mainLayout, 800, 500);
        getChildren().add(mainLayout);
    }

    private void createAvailableItemsTableColumns() {
        TableColumn<OrderItem, String> supplyCodeColumn = new TableColumn<>("Mã Hàng");
        supplyCodeColumn.setCellValueFactory(new PropertyValueFactory<>("supplyCode"));
        supplyCodeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        supplyCodeColumn.setEditable(true);

        // Xử lý sửa đổi mã hàng
        supplyCodeColumn.setOnEditCommit(event -> {
            String newSupplyCode = event.getNewValue();
            int rowIndex = event.getTablePosition().getRow();
            OrderItem orderItem = availableItems.get(rowIndex);
            orderItem.setSupplyCode(newSupplyCode);
        });

        TableColumn<OrderItem, String> itemNameColumn = new TableColumn<>("Tên Hàng Hóa");
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemNameColumn.setEditable(false);

        // Thêm cột để thêm item vào order
        TableColumn<OrderItem, Void> addColumn = new TableColumn<>("Thêm");
        addColumn.setCellFactory(col -> new TableCell<OrderItem, Void>() {
            private final Button addButton = new Button("Thêm");

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(addButton);
                    addButton.setOnAction(event -> {
                        int index = getIndex();
                        OrderItem selectedItem = availableItems.get(index);

                        OrderItem orderItem = new OrderItem(); // Khởi tạo một đối tượng mới
                        orderItem.setSupplyCode(selectedItem.getSupplyCode());
                        orderItem.setItemName(selectedItem.getItemName());
                        orderItem.setUnit(selectedItem.getUnit()); // Lấy đơn vị từ selectedItem
                        orderItem.setPrice(selectedItem.getPrice()); // Thiết lập giá
                        orderItem.setQuantity(1); // Thiết lập số lượng mặc định là 1
                        orderItem.setTotalValue(orderItem.getPrice() * orderItem.getQuantity()); // Tính tổng giá trị
                        orderItems.add(orderItem); // Thêm vào orderItems

                        availableItems.remove(index); // Xóa khỏi available items
                        availableItemsTable.setItems(availableItems); // Cập nhật bảng available
                        orderItemsTable.setItems(orderItems); // Cập nhật bảng order
                    });


                }
            }
        });

        availableItemsTable.getColumns().addAll(supplyCodeColumn, itemNameColumn, addColumn);
        availableItemsTable.setItems(availableItems); // Initialize with available items
    }

    private void createOrderTableColumns() {
        // Đảm bảo orderItemsTable có thể chỉnh sửa
        orderItemsTable.setEditable(true);

        // Mã hàng (editable)
        TableColumn<OrderItem, String> supplyCodeColumn = new TableColumn<>("Mã Hàng");
        supplyCodeColumn.setCellValueFactory(new PropertyValueFactory<>("supplyCode"));
        supplyCodeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        supplyCodeColumn.setEditable(true);

        // Số Lượng (editable)
        TableColumn<OrderItem, Integer> quantityColumn = new TableColumn<>("Số Lượng");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityColumn.setEditable(true);

        // Đơn Vị
        TableColumn<OrderItem, String> unitColumn = new TableColumn<>("Đơn Vị");
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitColumn.setEditable(false);

        // Giá (không thể sửa đổi)
        TableColumn<OrderItem, Double> priceColumn = new TableColumn<>("Giá");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setEditable(false);

        // Tổng Giá Trị (được tính toán)
        TableColumn<OrderItem, Double> totalValueColumn = new TableColumn<>("Tổng Giá Trị");
        totalValueColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        totalValueColumn.setEditable(false);

        // Thêm cột xóa
        TableColumn<OrderItem, Void> deleteColumn = new TableColumn<>("Xóa");
        deleteColumn.setCellFactory(col -> new TableCell<OrderItem, Void>() {
            private final Button deleteButton = new Button("Xóa");

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    deleteButton.setOnAction(event -> {
                        int index = getIndex();
                        OrderItem orderItemToRemove = orderItems.get(index);
                        orderItems.remove(index); // Xóa OrderItem tại index

                        // Thêm lại OrderItem vào danh sách availableItems
                        availableItems.add(orderItemToRemove);
                        availableItemsTable.setItems(availableItems); // Cập nhật bảng availableItems
                        orderItemsTable.setItems(orderItems); // Cập nhật bảng orderItems
                    });
                }
            }
        });

        // Thêm cột vào bảng
        orderItemsTable.getColumns().addAll(supplyCodeColumn, quantityColumn, unitColumn, priceColumn, totalValueColumn, deleteColumn);

        // Xử lý sửa đổi số lượng
        quantityColumn.setOnEditCommit(event -> {
            Integer newQuantity = event.getNewValue();
            int rowIndex = event.getTablePosition().getRow();
            OrderItem orderItem = orderItems.get(rowIndex);
            orderItem.setQuantity(newQuantity); // Cập nhật số lượng
            orderItem.setTotalValue(orderItem.getPrice() * newQuantity); // Cập nhật tổng giá trị
            orderItemsTable.refresh(); // Làm mới bảng để hiển thị thay đổi
        });
    }


    private void refreshTables() {
        availableItemsTable.setItems(availableItems);
        orderItemsTable.setItems(orderItems);
    }

    private void checkSupplier(String supplierId) {
        availableItems.clear();
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT supply_code, name, unit, price FROM supplies WHERE suppliers_id = ?")) {
            statement.setString(1, supplierId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                OrderItem item = new OrderItem();
                item.setSupplyCode(resultSet.getString("supply_code"));
                item.setItemName(resultSet.getString("name"));
                item.setUnit(resultSet.getString("unit")); // Lưu đơn vị
                item.setPrice(resultSet.getDouble("price")); // Lưu giá
                availableItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        availableItemsTable.setItems(availableItems);
    }


    private void updateItemDetails(OrderItem orderItem) {
        // Logic to fetch updated item details from the database
        // For example, retrieve price and unit based on the new supply code
    }


    private void saveOrder() {
        LocalDate selectedDate = orderDatePicker.getValue();
        LocalDate currentDate = LocalDate.now();

        // Check if selected date is less than 2 days from today
        if (selectedDate.isBefore(currentDate.plusDays(2))) {
            showAlert("Ngày đặt hàng không thể thấp hơn ngày hiện tại 2 ngày.");
            return;
        }

        String orderNumber;
        while (true) {
            orderNumber = generateOrderNumber();
            if (isOrderNumberUnique(orderNumber)) {
                break; // Unique order number found
            }
        }

        String supplierId = supplierCodeField.getText();
        String orderDate = selectedDate.toString();

        try (Connection connection = JDBCConnect.getJDBCConnection()) {
            // Save to delivery_order
            String insertOrderSQL = "INSERT INTO delivery_order (order_number, supplier_id, order_date) VALUES (?, ?, ?)";
            try (PreparedStatement orderStatement = connection.prepareStatement(insertOrderSQL)) {
                orderStatement.setString(1, orderNumber);
                orderStatement.setString(2, supplierId);
                orderStatement.setString(3, orderDate);
                orderStatement.executeUpdate();
            }

            // Save each item to delivery_order_items
            String insertItemSQL = "INSERT INTO delivery_order_items (order_number, supply_code, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement itemStatement = connection.prepareStatement(insertItemSQL)) {
                for (OrderItem orderItem : orderItems) {
                    itemStatement.setString(1, orderNumber);
                    itemStatement.setString(2, orderItem.getSupplyCode());
                    itemStatement.setDouble(3, orderItem.getQuantity());
                    itemStatement.executeUpdate();
                }
            }

            // Optionally, refresh or clear the tables after saving
            orderItems.clear();
            availableItems.clear();
            refreshTables();

            showAlert("Đơn hàng đã được đặt thành công với số: " + orderNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu đơn hàng.");
        }
    }

    private boolean isOrderNumberUnique(String orderNumber) {
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM delivery_order WHERE order_number = ?")) {
            statement.setString(1, orderNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0; // Return true if count is 0 (unique)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Assume not unique if there's an error
    }

    private String generateOrderNumber() {
        Random random = new Random();
        int number = 400000000 + random.nextInt(1000000000 - 400000000); // Generates a number starting with "40"
        return String.valueOf(number);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông Báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
