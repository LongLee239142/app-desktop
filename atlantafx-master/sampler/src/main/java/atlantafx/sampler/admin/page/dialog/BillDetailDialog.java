package atlantafx.sampler.admin.page.dialog;

import atlantafx.sampler.admin.layout.ModalDialog;
import atlantafx.sampler.base.entity.common.BillDetail;
import atlantafx.sampler.base.util.Lazy;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class BillDetailDialog extends ModalDialog {

    private Label titleLabel;
    private TableView<BillDetail> billDetailTable;
    private Lazy<Button> closeButton;

    public BillDetailDialog() {
        super();
        closeButton = new Lazy<>(() -> {
            var button = new Button("Close");
            button.setOnAction(e -> close());
            return button;
        });
        initializeUI();
    }

    protected void initializeUI() {
        titleLabel = new Label("Bill Details");

        billDetailTable = new TableView<>();
//        setupTableColumns();

        VBox vbox = new VBox(10, titleLabel, billDetailTable, closeButton.get());
        BorderPane borderPane = new BorderPane(vbox);
        getChildren().add(borderPane);
    }

//    private void setupTableColumns() {
//        TableColumn<BillDetail, Integer> productColumn = new TableColumn<>("Product");
//        productColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProductId()));
//
//        TableColumn<BillDetail, Integer> quantityColumn = new TableColumn<>("Quantity");
//        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
//
//        TableColumn<BillDetail, Double> priceColumn = new TableColumn<>("Price");
//        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
//
//        billDetailTable.getColumns().addAll(productColumn, quantityColumn, priceColumn);
//    }
//
//    public void getBillDetails(BillOrder billOrder) {
//        titleLabel.setText("Bill #" + billOrder.getId());
//        loadBillDetails(billOrder.getId());
//    }
//
//    private void loadBillDetails(int billId) {
//        String query = "SELECT p.name AS product_name, bd.quantity, bd.price FROM bill_detail bd " +
//                "JOIN products p ON bd.product_id = p.id WHERE bd.bill_id = ?";
//
//        try (Connection connection = JDBCConnect.getJDBCConnection();
//             PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setInt(1, billId);
//            ResultSet resultSet = statement.executeQuery();
//
//            List<BillDetail> billDetails = new ArrayList<>();
//            while (resultSet.next()) {
//                String productName = resultSet.getString("product_name");
//                int quantity = resultSet.getInt("quantity");
//                double price = resultSet.getDouble("price");
//
//                BillDetail detail = new BillDetail();
//                detail.setProductId(productId);
//                detail.setQuantity(quantity);
//                detail.setPrice(price);
//                billDetails.add(detail);
//            }
//
//            billDetailTable.getItems().setAll(billDetails);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
