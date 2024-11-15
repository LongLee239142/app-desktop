package atlantafx.sampler.cashier.page.components;

import atlantafx.sampler.base.entity.common.Bill;
import atlantafx.sampler.base.entity.common.BillDetail;
import atlantafx.sampler.base.entity.common.BillOrder;
import atlantafx.sampler.base.entity.common.Voucher;
import atlantafx.sampler.base.enummethod.Payment;
import atlantafx.sampler.base.service.cashier.CashierService;
import atlantafx.sampler.base.service.cashier.TableCoffeeService;
import atlantafx.sampler.base.util.AlertUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OrderListPage {

  public static final String NAME = "Order List";
  static ListView<String> drinkList = new ListView<>();

  public void showCheckOrderDialog() {
    Dialog<VBox> dialog = new Dialog<>();
    dialog.setTitle("Quản lý hóa đơn");
    dialog.setHeaderText("Xem và quản lý hóa đơn của bàn: " + TableListPage.getTitle());
    dialog.setWidth(900);
    dialog.setHeight(700);
    dialog.setResizable(true);

    ButtonType doneButtonType = new ButtonType("Hoàn tất", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(doneButtonType, ButtonType.CANCEL);

    Label selectedTableLabel = new Label(TableListPage.getTitle());

    TextField searchBar = new TextField();
    searchBar.setPromptText("Nhập mã/Tên món cần tìm");
    searchBar.setPrefWidth(300);
    searchBar.getStyleClass().add("text-field");

    Button checkOut = new Button("Thanh Toán");

    ComboBox<String> comboBox = CashierService.createPayCategoriesSelectionBox();
    comboBox.getItems().add("All");
    comboBox.setValue("All");
    comboBox.getStyleClass().add("combo-box");

    drinkList = CashierService.InitializeProductName();

    comboBox.setOnAction(e -> {
      String selectedFilter = comboBox.getValue();
      if ("All".equals(selectedFilter)) {
        drinkList.setItems(CashierService.InitializeProductName1());
      } else {
        drinkList.setItems(
                CashierService.InitializeProductNameCategory(CashierService.getIdByCategoryName(
                        selectedFilter)));
      }
    });

    searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
      String searchText = newValue.trim();
      if (searchText.isEmpty()) {
        var items = CashierService.InitializeProductName1();
        if (items != null && !items.isEmpty()) {
          drinkList.setItems(items);
        }
      } else {
        var items = CashierService.InitializeProductNameByKey(searchText);
        if (items != null && !items.isEmpty()) {
          drinkList.setItems(items);
        }
      }
    });
    if (!drinkList.getItems().isEmpty() && drinkList.getSelectionModel().getSelectedIndex() >= 0) {
      drinkList = CashierService.InitializeProductName();
    }

    Label quantityLabel = new Label("Số lượng:");
    Spinner<Integer> quantitySpinner = new Spinner<>(1, 20, 1);
    Button addButton = new Button("Thêm vào hóa đơn");

    VBox billContainer = new VBox(10);

    Label totalLabel = new Label("Tổng cộng:");
    TextField totalField = new TextField("0");
    totalField.setEditable(false);

    ComboBox<String> methodComboBox = CashierService.createPayMethodSelectionBox();

    comboBox.setOnAction(e -> {
      String selectedFilter = comboBox.getValue();
      if ("All".equals(selectedFilter)) {
        drinkList.setItems(CashierService.InitializeProductName1());
      } else {
        drinkList.setItems(
                CashierService.InitializeProductNameCategory(CashierService.getIdByCategoryName(
                        (selectedFilter))));
      }

      // Check for empty list state after setting items
      if (drinkList.getItems() == null || drinkList.getItems().isEmpty()) {
        drinkList.getSelectionModel().clearSelection();
      }
    });
    updateAllBill(billContainer, totalField);
    addButton.setOnAction(e -> {
      String selectedDrink = drinkList.getSelectionModel().getSelectedItem();
      if (selectedDrink == null) {
        AlertUtil.showErrorAlert("Vui lòng chọn món để thêm vào hóa đơn");
      } else {
        int quantity = quantitySpinner.getValue();
        handleAddToBill(selectedDrink, quantity, billContainer, totalField);
      }
    });

    checkOut.setOnAction(e -> {
      List<Bill> newBill = CashierService.getBillByNameTable(TableListPage.getTitle());
      billContainer.getChildren().clear();

      if (newBill == null || newBill.isEmpty()) {
        AlertUtil.showErrorAlert("Bàn " + TableListPage.getTitle() + " chưa có đặt đồ uống");
      } else {
        double totalAmount = Double.parseDouble(totalField.getText());


        // Kiểm tra nếu tổng cộng sau giảm giá > 0
        if (totalAmount > 0) {
          updateAllBill(billContainer, totalField);
          String selectedMethod = methodComboBox.getValue();
          TableCoffeeService.updateStatusTable(2, TableListPage.getTitle()); // Cập nhật trạng thái bàn thành "đã thanh toán"

          if (selectedMethod != null) {
            try {

              BillOrder billOrder = new BillOrder(CashierService.getTableIdByName(TableListPage.getTitle()),Double.valueOf(totalField.getText()),
                      CashierService.getIdByMethodEnum(String.valueOf(Payment.fromDisplayName(selectedMethod))));
              CashierService.addNewBillOrder(billOrder);
              BillOrder billOrderCurrent = CashierService.getBillOrderByTableId(CashierService.getTableIdByName(TableListPage.getTitle()));
              List<Bill> bills = CashierService.getBillByNameTable(TableListPage.getTitle());
              for (Bill bill : bills) {
                int productId = CashierService.getProductByName(bill.getProductName()).getId();
                BillDetail billDetail = new BillDetail(billOrderCurrent.getId(),productId,
                        bill.getQuantity(),bill.getPrice(),bill.getVoucher());
                CashierService.addNewBillDetail(billDetail);
              }
            } catch (SQLException ex) {
              throw new RuntimeException(ex);
            }

            AlertUtil.showErrorAlert(
                    "Thanh toán thành công!\n"
                            + "Tổng tiền: " + totalField.getText() + " VND\n"
                            + "Phương thức thanh toán: " + selectedMethod);

            // Reset đơn hàng và làm sạch giao diện
            CashierService.resetOrderBill(TableListPage.getTitle());
            billContainer.getChildren().clear();
            updateTotalField(totalField, 0); // Reset tổng đơn về 0 sau khi thanh toán
          }
        } else {
          AlertUtil.showErrorAlert("Số tiền sau giảm giá không hợp lệ!");
        }
      }
    });


    ScrollPane billScrollPane = new ScrollPane(billContainer);
    billScrollPane.setFitToWidth(true);
    billScrollPane.setPrefHeight(300);
    billScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    VBox drinkListContainer = new VBox(drinkList);
    drinkListContainer.setPrefWidth(300);
    drinkListContainer.getStyleClass().add("drink-list-container");

    VBox billContainerContainer = new VBox(billScrollPane);
    billContainerContainer.setPrefWidth(700);
    billContainerContainer.getStyleClass().add("bill-container");

    HBox containersHBox = new HBox(10, drinkListContainer, billContainerContainer);

    VBox orderLayout = new VBox(10, selectedTableLabel, searchBar, comboBox, quantityLabel,
            quantitySpinner, addButton, containersHBox, totalLabel, totalField,
            methodComboBox, checkOut);
    orderLayout.setPadding(new Insets(20));
    orderLayout.setAlignment(Pos.CENTER);
    dialog.setResultConverter(button -> {
      if (button == doneButtonType) {

      }
      return null; // Trả về null nếu không có hành động nào được thực hiện
    });

    dialog.getDialogPane().setContent(orderLayout);

    dialog.showAndWait().orElse(null);
  }

  private void handleAddToBill(String selectedDrink, int quantity, VBox billContainer,
                               TextField totalField) {
    Bill billCheck = CashierService.findOrderBillIsExist(TableListPage.getTitle(), selectedDrink);
    if (billCheck != null) {
      billCheck.setQuantity(quantity + billCheck.getQuantity());
      CashierService.updateOrderBill(billCheck);
      updateAllBill(billContainer, totalField);
    } else {
      double price = CashierService.getPriceByName(selectedDrink);
      double subTotal = price * quantity;
      Bill bill = new Bill(TableListPage.getTitle(), selectedDrink, quantity, price);
      CashierService.addOrderBill(bill);

      HBox billRow = createBillRow(bill, billContainer, totalField, subTotal, bill.getVoucher());
      billContainer.getChildren().add(billRow);

      updateTotalField(totalField, subTotal);
      updateAllBill(billContainer, totalField);
    }
  }

  private HBox createBillRow(Bill bill, VBox billContainer, TextField totalField, double subTotal,
                             int valueVoucher) {
    HBox billRow = new HBox(10);
    Label billInfo = new Label(bill.getProductName() + " - Số lượng: " + bill.getQuantity()
            + " - Giá: " + bill.getPrice() * bill.getQuantity() + " VND");
    billInfo.getStyleClass().add("bill-container-label");

    Button editButton = new Button("Sửa");
    editButton.getStyleClass().add("bill-button");
    editButton.setOnAction(e -> handleEditBill(bill, billContainer, totalField));

    Button deleteButton = new Button("Xóa");
    deleteButton.getStyleClass().add("bill-button");
    deleteButton.setOnAction(e -> {
      CashierService.removeOrderBill(bill);
      billContainer.getChildren().remove(billRow);
      updateTotalField(totalField, -subTotal);
      updateAllBill(billContainer, totalField);
    });

    ComboBox<String> comboBoxVoucher = CashierService.createVoucherSelectionBox();
    comboBoxVoucher.getItems().add("Không");
    if (bill.getVoucher() != 0) {
      comboBoxVoucher.setValue(valueVoucher + "%");
    } else {
      comboBoxVoucher.setValue("Giảm giá");
    }
    comboBoxVoucher.getStyleClass().add("combo-box");
    comboBoxVoucher.setOnAction(e -> {
      String selectedVoucher = comboBoxVoucher.getValue();
      if(!selectedVoucher.equals("Không")) {
        int discountValue = Integer.parseInt(removeLastChar(selectedVoucher));

        // Apply the voucher and update the bill display
        handleVoucher(bill, billContainer, totalField, discountValue);
      }else {
        handleVoucher(bill, billContainer, totalField, 0);
      }
    });

    billRow.getChildren().addAll(billInfo, editButton, deleteButton, comboBoxVoucher);
    return billRow;
  }

  private void handleVoucher(Bill bill, VBox billContainer, TextField totalField,
                             int valueVoucher) {
    double originPrice = CashierService.getProductByName(bill.getProductName()).getPrice();
    double discount = originPrice * ((double) valueVoucher / 100);
    double discountedPrice = originPrice - discount;
    bill.setPrice(discountedPrice); // Cập nhật giá đã giảm vào Bill
    bill.setVoucher(valueVoucher);
    CashierService.updateOrderBill(bill);
    updateAllBill(billContainer, totalField);
  }


  public static String removeLastChar(String str) {
    if (str == null || str.isEmpty()) {
      return str; // Trả về chuỗi ban đầu nếu null hoặc rỗng
    }
    return str.substring(0, str.length() - 1);
  }

  private void handleEditBill(Bill bill, VBox billContainer, TextField totalField) {
    Dialog<Bill> editDialog = new Dialog<>();
    editDialog.setTitle("Sửa số lượng đồ uống");
    editDialog.setHeaderText("Chỉnh sửa đồ uống: " + bill.getProductName());
    ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
    editDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    Label quantityLabel = new Label("Số lượng:");
    Spinner<Integer> quantitySpinner = new Spinner<>(1, 20, bill.getQuantity());
    VBox layout = new VBox(10, quantityLabel, quantitySpinner);
    layout.setPadding(new Insets(20));

    editDialog.getDialogPane().setContent(layout);

    editDialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        bill.setQuantity(quantitySpinner.getValue());
        return bill;
      }
      return null;
    });

    Optional<Bill> result = editDialog.showAndWait();

    result.ifPresent(updatedBill -> {
      // Lưu lại giá trị cũ trước khi cập nhật
      double oldTotal = bill.getPrice() * bill.getQuantity();

      // Cập nhật số lượng trong cơ sở dữ liệu
      CashierService.updateOrderBill(updatedBill);

      // Tính toán lại tổng tiền sau khi chỉnh sửa
      double newTotal = updatedBill.getPrice() * updatedBill.getQuantity();
      updateTotalField(totalField, newTotal - oldTotal);

      // Cập nhật lại hiển thị trong billContainer
      for (Node node : billContainer.getChildren()) {
        if (node instanceof HBox) {
          HBox billRow = (HBox) node;
          Label label = (Label) billRow.getChildren().get(0);

          // Kiểm tra nếu sản phẩm trong row là sản phẩm đã chỉnh sửa
          if (label.getText().contains(bill.getProductName())) {
            label.setText(updatedBill.getProductName() + " - Số lượng: " + updatedBill.getQuantity()
                    + " - Giá: " + updatedBill.getPrice() * updatedBill.getQuantity() + " VND");
          }
        }
      }
      updateAllBill(billContainer, totalField);
    });
  }

  private void updateAllBill(VBox billContainer, TextField totalField) {
    List<Bill> bills = CashierService.getBillByNameTable(TableListPage.getTitle());
    billContainer.getChildren().clear();
    double total = 0;

    if (bills == null || bills.isEmpty()) {
      AlertUtil.showErrorAlert("Đặt đồ uống cho Bàn " + TableListPage.getTitle());
      // Reset các ComboBox nếu không có hóa đơn
    } else {
      for (Bill bill : bills) {
        double subTotal = bill.getQuantity() * bill.getPrice();
        HBox billRow = createBillRow(bill, billContainer, totalField, subTotal, bill.getVoucher());
        billContainer.getChildren().add(billRow);
        total += subTotal;
      }
      updateTotalField(totalField, total);
      // Cập nhật các ComboBox dựa trên các hóa đơn hiện có
    }
  }
  private void voucherAllBill(VBox billContainer, TextField totalField,int voucher) {
    List<Bill> bills = CashierService.getBillByNameTable(TableListPage.getTitle());
    billContainer.getChildren().clear();
    double total = 0;

    if (bills == null || bills.isEmpty()) {
      AlertUtil.showErrorAlert("Đặt đồ uống cho Bàn " + TableListPage.getTitle());
      // Reset các ComboBox nếu không có hóa đơn
    } else {
      for (Bill bill : bills) {
        double subTotal = bill.getQuantity() * bill.getPrice();
        HBox billRow = createBillRow(bill, billContainer, totalField, subTotal, bill.getVoucher());
        billContainer.getChildren().add(billRow);
        total += subTotal;
      }
      total = total * (100 - voucher)/100;
      updateTotalField(totalField, total);
    }
  }



  private static void updateTotalField(TextField totalField, double newTotal) {
    totalField.setText(String.valueOf(newTotal));
  }

}