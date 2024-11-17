package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.admin.page.dialog.BillDetailDialog;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.common.BillOrder;
import atlantafx.sampler.base.util.Lazy;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class OrderListPage extends OutlinePage {
    public static final String NAME = "Danh sách hóa đơn";

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextField minAmountField;
    private TextField maxAmountField;
    private TableView<BillOrder> billOrderTable;
    private final Lazy<BillDetailDialog> billDetailDialog;

    @Override
    public String getName() {
        return NAME;
    }

    public OrderListPage() {
        super();
        initializeUI();

        billDetailDialog = new Lazy<>(() -> {
            var dialog = new BillDetailDialog();
            dialog.setClearOnClose(true);
            return dialog;
        });
    }

    private void initializeUI() {
        // Các thành phần bộ lọc
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        TextField minAmountField = new TextField();
        minAmountField.setPromptText("Tối thiểu");
        TextField maxAmountField = new TextField();
        maxAmountField.setPromptText("Tối đa");
        Button filterButton = new Button("Lọc");
        filterButton.setOnAction(event -> loadFilteredBillOrders(startDatePicker, endDatePicker, minAmountField, maxAmountField));

        // Sắp xếp giao diện bộ lọc
        HBox dateFilterBox = new HBox(10, new Label("Bộ lọc:"), startDatePicker, endDatePicker);
        HBox amountFilterBox = new HBox(10, new Label("Số tiền:"), minAmountField, maxAmountField);
        VBox filterBox = new VBox(10, dateFilterBox, amountFilterBox, filterButton);

        // Thiết lập bảng
        billOrderTable = new TableView<>();
        setupTableColumns();

        // Thêm vào giao diện chính
        VBox vbox = new VBox(10, filterBox, billOrderTable);
        BorderPane borderPane = new BorderPane(vbox);
        getChildren().add(borderPane);
    }

    private void setupTableColumns() {
        TableColumn<BillOrder, Integer> idColumn = new TableColumn<>("Mã hóa đơn");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(100);

        TableColumn<BillOrder, LocalDateTime> dateColumn = new TableColumn<>("Ngày tạo");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateColumn.setMinWidth(150);

        TableColumn<BillOrder, Double> totalColumn = new TableColumn<>("Tổng tiền");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalColumn.setMinWidth(120);

        TableColumn<BillOrder, String> actionColumn = new TableColumn<>("Hành động");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("Xem");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                    viewButton.setOnAction(e -> openBillDetailDialog(getTableRow().getItem()));
                }
            }
        });
        actionColumn.setMinWidth(100);

        billOrderTable.getColumns().addAll(idColumn, dateColumn, totalColumn, actionColumn);
    }

    private void loadFilteredBillOrders(DatePicker startDatePicker, DatePicker endDatePicker,
                                        TextField minAmountField, TextField maxAmountField) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        Double minAmount = null;
        Double maxAmount = null;

        // Xử lý giá trị min/max từ TextField
        try {
            if (!minAmountField.getText().isEmpty()) {
                minAmount = Double.parseDouble(minAmountField.getText());
            }
            if (!maxAmountField.getText().isEmpty()) {
                maxAmount = Double.parseDouble(maxAmountField.getText());
            }
        } catch (NumberFormatException e) {
            // Hiển thị lỗi khi giá trị không hợp lệ
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng nhập số hợp lệ cho Số tiền!");
            alert.show();
            return;
        }

        // Tạo câu truy vấn SQL
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM bill_order WHERE 1=1");
        if (startDate != null) {
            queryBuilder.append(" AND DATE(created_at) >= ?");
        }
        if (endDate != null) {
            queryBuilder.append(" AND DATE(created_at) <= ?");
        }
        if (minAmount != null) {
            queryBuilder.append(" AND total_amount >= ?");
        }
        if (maxAmount != null) {
            queryBuilder.append(" AND total_amount <= ?");
        }

        // Thực thi truy vấn
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {

            int parameterIndex = 1;
            if (startDate != null) {
                statement.setString(parameterIndex++, startDate.toString());
            }
            if (endDate != null) {
                statement.setString(parameterIndex++, endDate.toString());
            }
            if (minAmount != null) {
                statement.setDouble(parameterIndex++, minAmount);
            }
            if (maxAmount != null) {
                statement.setDouble(parameterIndex++, maxAmount);
            }

            ResultSet resultSet = statement.executeQuery();
            billOrderTable.getItems().clear();

            // Duyệt qua kết quả và thêm vào bảng
            while (resultSet.next()) {
                BillOrder billOrder = new BillOrder(
                        resultSet.getInt("id"),
                        resultSet.getInt("table_id"),
                        resultSet.getDouble("total_amount"),
                        resultSet.getInt("payment_method_id"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()
                );
                billOrderTable.getItems().add(billOrder);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi tải dữ liệu hóa đơn!");
            alert.show();
        }
    }


    private void openBillDetailDialog(BillOrder billOrder) {
        if (billOrder != null) {
            BillDetailDialog dialog = billDetailDialog.get();
            dialog.loadBillDetails(billOrder.getId()); // Truyền mã hóa đơn
            dialog.setBillTitle("Hóa đơn #" + billOrder.getId()); // Tùy chỉnh tiêu đề
            dialog.show(getScene()); // Hiển thị hộp thoại
        }
    }
}
