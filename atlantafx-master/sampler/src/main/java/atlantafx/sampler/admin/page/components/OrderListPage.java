package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.admin.page.dialog.BillDetailDialog;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.common.BillOrder;
import atlantafx.sampler.base.util.Lazy;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
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
    public static final String NAME = "Order List";

    private DatePicker datePicker;
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
        datePicker = new DatePicker();
        datePicker.setOnAction(event -> loadBillOrders());

        billOrderTable = new TableView<>();
        setupTableColumns();

        VBox vbox = new VBox(10, datePicker, billOrderTable);
        BorderPane borderPane = new BorderPane(vbox);
        getChildren().add(borderPane);
    }

    private void setupTableColumns() {
        TableColumn<BillOrder, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<BillOrder, LocalDateTime> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<BillOrder, Double> totalColumn = new TableColumn<>("Total Amount");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        TableColumn<BillOrder, String> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
//                    viewButton.setOnAction(e -> openBillDetailDialog(getTableRow().getItem()));
                }
            }
        });

        billOrderTable.getColumns().addAll(idColumn, dateColumn, totalColumn, actionColumn);
    }

    private void loadBillOrders() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            String query = "SELECT * FROM bill_order WHERE DATE(created_at) = ?";

            try (Connection connection = JDBCConnect.getJDBCConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, selectedDate.format(DateTimeFormatter.ISO_DATE));
                ResultSet resultSet = statement.executeQuery();
                billOrderTable.getItems().clear();

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
            }
        }
    }

//    private void openBillDetailDialog(BillOrder billOrder) {
//        if (billOrder != null) {
//            BillDetailDialog dialog = billDetailDialog.get();
//            dialog.loadBillDetails(billOrder);  // Adjusted to fetch bill details
//            dialog.show(getScene());  // Show the dialog
//        }
//    }
}
