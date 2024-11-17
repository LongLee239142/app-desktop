package atlantafx.sampler.admin.page.dialog;

import atlantafx.sampler.admin.layout.ModalDialog;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.common.BillDetail;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BillDetailDialog extends ModalDialog {
    private final TableView<BillDetail> billDetailTable;
    private int billId;

    public BillDetailDialog() {
        super();

        // Initialize the TableView for displaying bill details
        billDetailTable = new TableView<>();
        setupTableColumns();

        VBox contentBox = new VBox(10, billDetailTable);
        contentBox.setStyle("-fx-padding: 20px; -fx-background-color: white; -fx-border-radius: 15px; -fx-background-radius: 15px;");
        content.setBody(contentBox);
        content.setPrefSize(700, 400);

        // Set border style for the dialog
        content.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 15px;");

        // Title will dynamically display bill information
        header.setTitle("Bill Details");
    }

    private void setupTableColumns() {
        TableColumn<BillDetail, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<BillDetail, String> productNameColumn = new TableColumn<>("Product Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<BillDetail, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<BillDetail, Double> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        billDetailTable.getColumns().addAll(idColumn, productNameColumn, quantityColumn, totalPriceColumn);
    }

    public void loadBillDetails(int billId) {
        this.billId = billId;
        String query = """
            SELECT bd.id, bd.quantity, p.name AS productName, (p.price * bd.quantity) AS totalPrice
            FROM bill_detail bd
            JOIN products p ON bd.product_id = p.id
            WHERE bd.bill_order_id = ?
        """;

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, billId);
            ResultSet resultSet = statement.executeQuery();
            billDetailTable.getItems().clear();

            while (resultSet.next()) {
                BillDetail billDetail = new BillDetail(
                        resultSet.getInt("id"),
                        billId,
                        resultSet.getString("productName"), // Update to use product name
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("totalPrice") // Calculated total price
                );
                billDetailTable.getItems().add(billDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load bill details.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void setBillTitle(String billInfo) {
        header.setTitle("Bill Details - " + billInfo);
    }
}
