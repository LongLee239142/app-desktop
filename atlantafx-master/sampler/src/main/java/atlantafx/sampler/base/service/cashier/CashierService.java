package atlantafx.sampler.base.service.cashier;


import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.common.*;
import atlantafx.sampler.base.enummethod.Payment;
import atlantafx.sampler.base.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CashierService {

  public static List<Products> getAllProducts() {
    List<Products> products = new ArrayList<>();
    String sql = "SELECT * FROM products";
    try (Connection connection = JDBCConnect.getJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        Products product = new Products(resultSet.getInt("id"),
            resultSet.getString("image_link"),
            resultSet.getString("name"),
            resultSet.getDouble("price"),
            resultSet.getInt("category_id")
        );
        products.add(product);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return products;
  }

  public static ListView<String> InitializeProductName() {
    List<Products> listProducts = CashierService.getAllProducts();
    ListView<String> productNames = new ListView<>();
    for (Products product : listProducts) {
      productNames.getItems().add(product.getName());
    }
    return productNames;  // Now returning the ListView
  }

  public static ObservableList<String> InitializeProductName1() {
    ObservableList<String> products = FXCollections.observableArrayList();
    List<Products> listProducts = CashierService.getAllProducts();
    for (Products product : listProducts) {
      products.add(product.getName());
    }
    return products;
  }

  public static ObservableList<String> InitializeProductNameByKey(String key) {
    ObservableList<String> products = FXCollections.observableArrayList();
    List<Products> listProducts = CashierService.getProductsByKey(key);
    for (Products product : listProducts) {
      products.add(product.getName());
    }
    return products;
  }

  public static ObservableList<String> InitializeProductNameCategory(int categorId) {
    ObservableList<String> products = FXCollections.observableArrayList();
    List<Products> listProducts = CashierService.getProductsByCategory(categorId);
    for (Products product : listProducts) {
      products.add(product.getName());
    }
    return products;
  }

  public static boolean addNewBillOrder(BillOrder billOrder) {
    String sql = " INSERT INTO bill_order(table_id,total_amount,payment_method_id,created_at)VALUES(?,?,?,?)";
    try (Connection connection = JDBCConnect.getJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, billOrder.getTableId()); // Replace with the actual table name
      preparedStatement.setDouble(2, billOrder.getTotalAmount()); // Replace with the actual product name
      preparedStatement.setInt(3, billOrder.getPaymentMethodId()); // Replace with the actual quantity
      preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
// Replace with the actual price
      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  public static BillOrder getBillOrderByTableId(int tableId) {
    String sql = "SELECT * FROM bill_order WHERE table_id = ?";

    try (Connection connection = JDBCConnect.getJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, tableId);
      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        // Khởi tạo đối tượng BillOrder từ các giá trị trong ResultSet
        return new BillOrder(
                resultSet.getInt("id"),
                resultSet.getInt("table_id"),
                resultSet.getDouble("total_amount"),
                resultSet.getInt("payment_method_id"),
                resultSet.getTimestamp("created_at")
        );
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null; // Trả về null nếu không tìm thấy bản ghi nào
  }
  public static int getTableIdByName(String name) {
    String sql = "SELECT id FROM tables WHERE name = ?";
    int id = 0;
    try (Connection connection = JDBCConnect.getJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, name);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
       id = resultSet.getInt("id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return id;

  }
  public static boolean addNewBillDetail(BillDetail billDetail) {
    String sql = "INSERT INTO bill_detail (bill_order_id, product_id, quantity, price, voucher_id) VALUES (?, ?, ?, ?, ?)";

    try (Connection connection = JDBCConnect.getJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      // Kiểm tra sự tồn tại của `product_id` trong bảng `products`
      if (!isProductIdExists(connection, billDetail.getProductId())) {
        System.out.println("Error: product_id " + billDetail.getProductId() + " does not exist in products.");
        return false;
      }

      preparedStatement.setInt(1, billDetail.getBillId());
      preparedStatement.setInt(2, billDetail.getProductId());
      preparedStatement.setInt(3, billDetail.getQuantity());
      preparedStatement.setDouble(4, billDetail.getPrice());

      if (billDetail.getVoucherId() == null) {
        preparedStatement.setNull(5, Types.INTEGER);
      } else {
        preparedStatement.setInt(5, billDetail.getVoucherId());
      }

      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Phương thức kiểm tra sự tồn tại của `product_id` trong bảng `products`
  private static boolean isProductIdExists(Connection connection, int productId) throws SQLException {
    String checkSql = "SELECT 1 FROM products WHERE id = ?";
    try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
      checkStatement.setInt(1, productId);
      ResultSet resultSet = checkStatement.executeQuery();
      return resultSet.next();
    }
  }


  // Phương thức kiểm tra sự tồn tại của `bill_order_id` trong `bill_order`
  private static boolean isBillOrderIdExists(Connection connection, int billOrderId) throws SQLException {
    String checkSql = "SELECT 1 FROM bill_order WHERE id = ?";
    try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
      checkStatement.setInt(1, billOrderId);
      ResultSet resultSet = checkStatement.executeQuery();
      return resultSet.next();
    }
  }


  public static Products getProductsByProductName(String nameProduct) {
    Products product = null;
    String sql = "SELECT * FROM products WHERE name =?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, nameProduct);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        product = new Products(resultSet.getString("image_link"),
            resultSet.getString("name"),
            resultSet.getDouble("price"),
            resultSet.getInt("category_id")
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return product;
  }

  public static double getPriceByName(String name) {
    Products product = getProductsByProductName(name);
    return product.getPrice();
  }

  public static boolean addOrderBill(Bill bill) {
    String sql = " INSERT INTO bill(nameTable,productName,quantity,price)VALUES(?,?,?,?)";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, bill.getNameTable()); // Replace with the actual table name
      preparedStatement.setString(2, bill.getProductName()); // Replace with the actual product name
      preparedStatement.setInt(3, bill.getQuantity()); // Replace with the actual quantity
      preparedStatement.setDouble(4, bill.getPrice()); // Replace with the actual price
      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static Bill getOrderBillByNameProduct(String nameTable, String productName) {
    Bill bill = new Bill();
    String sql = "SELECT * FROM bill WHERE nameTable =? AND productName =?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, nameTable); // Replace with the actual table name
      preparedStatement.setString(2, productName); // Replace with the actual product name
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        bill = new Bill(resultSet.getString("nameTable"),
            resultSet.getString("productName"),
            resultSet.getInt("quantity"),
            resultSet.getDouble("price")
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return bill;

  }
  public static Products getProductByName(String name) {
    Products product = null;
    String sql = "SELECT * FROM products WHERE name =?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, name);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        product = new Products(resultSet.getInt("id"),
                resultSet.getString("image_link"),
            resultSet.getString("name"),
            resultSet.getDouble("price"),
            resultSet.getInt("category_id")
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return product;
  }

  public static boolean updateOrderBill(Bill bill) {
    String sql = "UPDATE bill SET price = ?, quantity =?, voucher_id =? WHERE nameTable = ? AND productName = ?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, bill.getPrice()); // Replace with the actual price
      preparedStatement.setInt(2, bill.getQuantity()); // Replace with the actual quantity
      preparedStatement.setString(4, bill.getNameTable()); // Replace with the actual table name
      preparedStatement.setString(5, bill.getProductName()); // Replace with the actual product name
      preparedStatement.setInt(3, bill.getVoucher());
      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static Bill findOrderBillIsExist(String tableName, String productName) {
    Bill bill = null;
    String sql = "SELECT * FROM bill WHERE productName =? AND nameTable =?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, productName); // Replace with the actual product name
      preparedStatement.setString(2, tableName); // Replace with the actual table name
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        bill = new Bill(resultSet.getString("nameTable"),
            resultSet.getString("productName"),
            resultSet.getInt("quantity"),
            resultSet.getDouble("price")
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return bill;
  }

  public static boolean resetOrderBill(String tableName) {
    String sql = "DELETE FROM bill WHERE nameTable = ?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, tableName); // Replace with the actual table name
      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static List<Bill> getBillByNameTable(String tableName) {
    List<Bill> bills = new ArrayList<>(); // Khởi tạo danh sách Bill
    String sql = "SELECT * FROM bill WHERE nameTable = ?"; // Truy vấn với nameTable

    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      // Đặt giá trị tham số cho truy vấn
      preparedStatement.setString(1, tableName);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        // Vòng lặp qua các kết quả của ResultSet
        while (resultSet.next()) {
          // Khởi tạo đối tượng Bill từ kết quả truy vấn
          Bill bill = new Bill(// Lấy giá trị id
              resultSet.getString("nameTable"),     // Lấy giá trị nameTable
              resultSet.getString("productName"),   // Lấy giá trị productName
              resultSet.getInt("quantity"),         // Lấy giá trị quantity
              resultSet.getDouble("price"),
              resultSet.getInt("voucher_id")// Lấy giá trị price
          );
          // Thêm Bill vào danh sách
          bills.add(bill);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return bills; // Trả về danh sách Bill
  }

  public static void removeOrderBill(Bill bill) {
    String sql = "DELETE FROM bill WHERE nameTable = ? AND productName = ? AND quantity = ?";

    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      // Thiết lập các tham số cho câu lệnh xóa
      preparedStatement.setString(1, bill.getNameTable());
      preparedStatement.setString(2, bill.getProductName());
      preparedStatement.setInt(3, bill.getQuantity());

      // Thực thi câu lệnh xóa
      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Hóa đơn đã được xóa thành công.");
      } else {
        System.out.println("Không tìm thấy hóa đơn để xóa.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
    }
  }

  public static List<String> getAllCategoriesProduct() {
    List<String> categories = new ArrayList<>();
    String sql = "SELECT * FROM category";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        categories.add(resultSet.getString("name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return categories;
  }

  public static ComboBox createPayCategoriesSelectionBox() {
    List<String> categories = new ArrayList<>();
    ComboBox<String> ctComboBox = new ComboBox<>();
    categories = CashierService.getAllCategoriesProduct();
    for (String category : categories) {
      ctComboBox.getItems().add(category);
    }
    ctComboBox.setValue(categories.get(0));
    return ctComboBox;
  }

  public static ComboBox createVoucherSelectionBox() {
    List<Voucher> vouchers = new ArrayList<>();
    ComboBox<String> ctComboBox = new ComboBox<>();
    vouchers = CashierService.getAllVoucher();
    for (Voucher voucher : vouchers) {
      ctComboBox.getItems().add(voucher.getVoucherPercentage() + "%");
    }
    return ctComboBox;
  }

  public static List<Voucher> getAllVoucher() {
    List<Voucher> vouchers = new ArrayList<>();
    String sql = "SELECT * FROM voucher";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Voucher voucher = new Voucher(resultSet.getInt("id"),
            resultSet.getString("voucher_code"),
            resultSet.getString("voucher_name"),
            resultSet.getInt("voucher_percentage"),
            resultSet.getDate("start_date").toLocalDate(),
            resultSet.getDate("end_date").toLocalDate(),
            resultSet.getString("status")
        );
        vouchers.add(voucher);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return vouchers;
  }

  public static List<Products> getProductsByCategory(int category) {
    List<Products> products = new ArrayList<>();
    String sql = "SELECT * FROM products WHERE category_id =?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, category);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Products product = new Products(resultSet.getString("image_link"),
            resultSet.getString("name"),
            resultSet.getDouble("price"),
            resultSet.getInt("category_id")
        );
        products.add(product);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return products;
  }

  public static List<Products> getProductsByKey(String key) {
    List<Products> products = new ArrayList<>();
    String sql = "SELECT * FROM products WHERE name LIKE?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, "%" + key + "%");
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Products product = new Products(resultSet.getString("image_link"),
            resultSet.getString("name"),
            resultSet.getDouble("price"),
            resultSet.getInt("category_id")
        );
        products.add(product);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return products;
  }

  public static int getIdByCategoryName(String categoryName) {
    String sql = "SELECT id FROM category WHERE name =?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, categoryName);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt("id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }

  //
  public static boolean addNewProduct(Products obj) {
    String sql = "INSERT INTO products(image_link, category_id, name, price) VALUES(?,?,?,?)";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, obj.getImageLink());
      preparedStatement.setInt(2, obj.getCategoryId());
      preparedStatement.setString(3, obj.getName());
      preparedStatement.setDouble(4, obj.getPrice());

      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean deleteProductsByProductName(String name) {
    String sql = "DELETE FROM products WHERE name =?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, name);

      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static List<PaymentMethod> getAllPayMethod() {
    List<PaymentMethod> Payments = new ArrayList<>();
    String sql = "SELECT * FROM payment_method";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        PaymentMethod method = new PaymentMethod(
            resultSet.getInt("id"),
            Payment.valueOf(resultSet.getString("method"))
        );
        Payments.add(method);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Payments;
  }


  public void createProductGrid(List<Products> productList, GridPane gridPane) {
    int column = 0;
    int row = 0;
    gridPane.getChildren().clear();

    for (Products product : productList) {
      String imagePath = product.getImageLink();

      if (imagePath != null) {
        String imagePathFull = "sampler/src/main/resources" + imagePath;
        File destinationFile = new File(imagePathFull);
        Image productImage = new Image(destinationFile.toURI().toString(), 180, 175,
            false, false);
        ImageView imageView = new ImageView(productImage);

        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(new Font("Arial", 18));
        nameLabel.getStyleClass().add("label-bold");

        Label priceLabel = new Label("$" + product.getPrice());
        priceLabel.setFont(new Font("Arial", 14));
        priceLabel.getStyleClass().add("label-price");

        Button editButton = new Button("Sửa");
        Button deleteButton = new Button("Xóa");

        // Chức năng chỉnh sửa sản phẩm
        editButton.setOnAction(e -> {
          Optional<Products> editedProduct = showEditProductDialog(product);
          editedProduct.ifPresent(updatedProduct -> {
            CashierService.updateProduct(updatedProduct);  // Lưu vào cơ sở dữ liệu
            productList.set(productList.indexOf(product), updatedProduct); // Cập nhật danh sách
            createProductGrid(productList, gridPane); // Tải lại lưới sau khi cập nhật
            System.out.println("Sửa thành công");
          });
        });

        // Chức năng xóa sản phẩm
        deleteButton.setOnAction(e -> {
          Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
          confirmationDialog.setTitle("Xác Nhận Xóa");
          confirmationDialog.setHeaderText("Bạn có chắc chắn muốn xóa sản phẩm này?");
          confirmationDialog.setContentText("Hành động này không thể hoàn tác.");
          confirmationDialog.getDialogPane().getStylesheets().add(
              getClass().getResource("/css/cssDiaLog.css").toExternalForm()
          );

          Optional<ButtonType> result = confirmationDialog.showAndWait();
          if (result.isPresent() && result.get() == ButtonType.OK) {
            String filePath = "sampler/src/main/resources" + product.getImageLink();
            File file = new File(filePath);
            if (file.exists()) {
              if (file.delete()) {
                CashierService.deleteProductsByProductName(product.getName());
                productList.remove(product);  // Cập nhật danh sách
                createProductGrid(productList, gridPane); // Tải lại lưới sau khi xóa
                AlertUtil.showErrorAlert("Xóa Thành Công");
              } else {
                AlertUtil.showErrorAlert("Lỗi Hệ Thống");
              }
            } else {
              System.out.println("File không tồn tại.");
            }
          } else {
            System.out.println("Người dùng đã hủy thao tác xóa.");
          }
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox productBox = new VBox(10, imageView, nameLabel, priceLabel, buttonBox);
        productBox.setPadding(new Insets(10));
        productBox.setAlignment(Pos.CENTER);
        productBox.getStyleClass().add("product-box");
        productBox.setOnMouseEntered(ev -> productBox.setStyle("-fx-background-color: #f0f0f0;"));
        productBox.setOnMouseExited(ev -> productBox.setStyle("-fx-background-color: #f9f9f9;"));

        gridPane.add(productBox, column, row);
        column++;
        if (column == 3) {
          column = 0;
          row++;
        }
      } else {
        System.out.println("Đường dẫn ảnh không hợp lệ cho sản phẩm: " + product.getName());
      }
    }
  }


  public static boolean updateProduct(Products product) {
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement statement = connection.prepareStatement(
            "UPDATE products SET name=?, price=?, image_link=?, category_id =? WHERE id=?")) {
      statement.setString(1, product.getName());
      statement.setDouble(2, product.getPrice());
      statement.setString(3, product.getImageLink());
      statement.setInt(4, product.getCategoryId());
      statement.setInt(5, product.getId());
      statement.executeUpdate();
      System.out.println("Product updated successfully.");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return true;
  }
  public static Integer getIdByMethodEnum(String method) throws SQLException {
    String query = "SELECT id FROM payment_method WHERE method = ?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

      preparedStatement.setString(1, method);
      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        return resultSet.getInt("id");
      } else {
        return null; // or throw an exception if needed
      }
    }
  }

  public static ComboBox createPayMethodSelectionBox() {
    List<PaymentMethod> Payments = new ArrayList<>();
    ComboBox<String> payComboBox = new ComboBox<>();
    Payments = CashierService.getAllPayMethod();
    for (PaymentMethod Payment : Payments) {
      Payment currentMethod = atlantafx.sampler.base.enummethod.Payment.fromDisplayName(
          Payment.getMethod().getStatus());
      payComboBox.getItems().add(currentMethod.getStatus());
      payComboBox.setValue(currentMethod.getStatus());
    }
    return payComboBox;
  }

  public static String getCategoryById(int categoryId) {
    String categoryName = "";
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement statement = connection.prepareStatement(
            "SELECT name FROM category WHERE id=?")) {
      statement.setInt(1, categoryId);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        categoryName = resultSet.getString("name");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return categoryName;
  }

  public Optional<Products> showEditProductDialog(Products product) {
    // Tạo dialog để chỉnh sửa sản phẩm
    Dialog<Products> dialog = new Dialog<>();
    dialog.setTitle("Chỉnh sửa sản phẩm");
    dialog.setHeaderText("Chỉnh sửa thông tin sản phẩm");

    // Các trường nhập liệu cho sản phẩm
    ComboBox<String> categoryField = CashierService.createPayCategoriesSelectionBox();
    categoryField.setValue(CashierService.getCategoryById(product.getCategoryId()));
    TextField nameField = new TextField(product.getName());
    TextField priceField = new TextField(String.valueOf(product.getPrice()));
    Button chooseImageButton = new Button("Chọn ảnh mới");
    Label imageLabel = new Label(product.getImageLink());

    File[] selectedFile = {null}; // Lưu file được chọn

    // Nút chọn ảnh
    chooseImageButton.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Chọn ảnh sản phẩm");
      fileChooser.getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
      selectedFile[0] = fileChooser.showOpenDialog(dialog.getOwner());
      if (selectedFile[0] != null) {
        imageLabel.setText(selectedFile[0].getName());
      }
    });

    // Tạo lưới hiển thị thông tin trong dialog
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Danh mục:"), 0, 0);
    grid.add(categoryField, 1, 0);
    grid.add(new Label("Tên sản phẩm:"), 0, 1);
    grid.add(nameField, 1, 1);
    grid.add(new Label("Giá:"), 0, 2);
    grid.add(priceField, 1, 2);
    grid.add(new Label("Ảnh:"), 0, 3);
    grid.add(chooseImageButton, 1, 3);
    grid.add(imageLabel, 2, 3);

    dialog.getDialogPane().setContent(grid);

    // Thêm các nút lưu và hủy
    ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // Logic khi nhấn nút "Lưu"
    dialog.setResultConverter(button -> {
      if (button == saveButtonType) {
        // Kiểm tra xem các trường có trống không
        if (nameField.getText() == null || nameField.getText().isEmpty() ||
            priceField.getText() == null || priceField.getText().isEmpty()) {
          AlertUtil.showErrorAlert("Vui lòng nhập đầy đủ thông tin.");
          return null;
        }

        try {
          // Chuyển đổi giá từ String sang Double
          double parsedPrice = Double.parseDouble(priceField.getText());

          // Xử lý file ảnh mới (nếu có)
          String imagePath = product.getImageLink(); // Giữ nguyên ảnh cũ nếu không thay đổi
          if (selectedFile[0] != null && selectedFile[0].exists()) {
            // Xóa ảnh cũ
            File oldFile = new File("sampler/src/main/resources" + product.getImageLink());
            if (oldFile.exists()) {
              oldFile.delete();
            }

            // Sao chép ảnh mới
            File destinationFile = new File(
                "sampler/src/main/resources/images/products/" + selectedFile[0].getName());
            Files.copy(selectedFile[0].toPath(), destinationFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

            // Cập nhật đường dẫn ảnh mới
            imagePath = "/images/products/" + selectedFile[0].getName();
          }

          // Trả về đối tượng sản phẩm đã chỉnh sửa
          return new Products(
              product.getId(),
              imagePath,
              nameField.getText(),
              parsedPrice,
              CashierService.getIdByCategoryName(categoryField.getValue())
          );

        } catch (NumberFormatException e) {
          AlertUtil.showErrorAlert("Giá không hợp lệ.");
        } catch (IOException ioException) {
          ioException.printStackTrace();
          AlertUtil.showErrorAlert("Lỗi khi sao chép tệp.");
        }
      }
      return null;
    });

    return dialog.showAndWait();
  }

  public static boolean addNewCategory(String category) {
    try (Connection connection = JDBCConnect.getJDBCConnection();
        PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO category (name) VALUES (?)")) {
      statement.setString(1, category);
      statement.executeUpdate();
      System.out.println("Category added successfully.");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return true;
  }

}
