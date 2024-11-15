package atlantafx.sampler.cashier.page.components;

import atlantafx.sampler.admin.page.components.ProductListPage;
import atlantafx.sampler.base.entity.common.Products;
import atlantafx.sampler.base.service.cashier.CashierService;
import atlantafx.sampler.base.util.AlertUtil;
import atlantafx.sampler.cashier.page.OutlinePage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ListProductPage extends OutlinePage {

    public static final String NAME = "List Product";
    List<Products> filteredProducts = new ArrayList<>();
    CashierService cf = new CashierService();
    private static File selectedFile;
    Stage primaryStage;

    @Override
    public String getName() {
        return NAME;
    }

    public ListProductPage() {
        super();
        viewProduct();
    }

    public void viewProduct() {
        // Main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getStylesheets()
                .add(getClass().getResource("/css/coffeeMenuApp.css").toExternalForm());
        mainLayout.getStyleClass().add("root");

        // Search Bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Nhập tên món cần tìm");
        searchBar.setPrefWidth(300);
        searchBar.getStyleClass().add("text-field");

        Button addNewProductButton = new Button("Thêm mới đồ uống");
        addNewProductButton.getStyleClass().add("add-button");
        Button addNewCategoryProductButton = new Button("Thêm mới loại đồ uống");
        addNewCategoryProductButton.getStyleClass().add("add-button");

        // ComboBox for filtering
        ComboBox<String> comboBox = CashierService.createPayCategoriesSelectionBox();
        comboBox.getItems().add("All");
        comboBox.setValue("All");
        comboBox.getStyleClass().add("combo-box");

        // Top HBox layout for search bar, search button, and ComboBox
        HBox topLayout = new HBox(10, searchBar, comboBox);
        topLayout.setPadding(new Insets(10));
        topLayout.setAlignment(Pos.CENTER);

        // Product Grid Layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setAlignment(Pos.CENTER);

        filteredProducts = CashierService.getAllProducts();
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue.trim();
            filteredProducts = CashierService.getProductsByKey(searchText); // Fetch filtered products
            updateProductGrid(gridPane, filteredProducts); // Update the grid with filtered products
        });
        comboBox.setOnAction(event -> {
            String selectFilter = comboBox.getValue();
            if ("All".equals(selectFilter) || selectFilter.isEmpty() || selectFilter == null) {
                filteredProducts = CashierService.getAllProducts();
            } else {
                filteredProducts = CashierService.getProductsByCategory(CashierService.getIdByCategoryName(
                        selectFilter));
            }
            updateProductGrid(gridPane, filteredProducts);
        });

        addNewProductButton.setOnAction(e -> {
            Products newProduct = showNewProductDialog(primaryStage);
            if (newProduct != null) {
                CashierService.addNewProduct(newProduct);
                filteredProducts = CashierService.getAllProducts(); // Lấy lại danh sách sản phẩm
                updateProductGrid(gridPane, filteredProducts); // Cập nhật lại grid với danh sách mới
                AlertUtil.showErrorAlert("Thêm Thành Công");
            } else {
                System.out.println("khong thay doi");
            }
        });
        addNewCategoryProductButton.setOnAction(e -> {
            gridPane.getChildren().clear();
            String categoryName = showNewCategoryDialog(primaryStage);
            if (categoryName != null) {
                if (CashierService.getIdByCategoryName(categoryName) > 0) {
                    AlertUtil.showErrorAlert("Danh mục đã tồn tại");
                } else {
                    CashierService.addNewCategory(categoryName);
                    updateProductGrid(gridPane, filteredProducts);
                    AlertUtil.showErrorAlert("Thêm Thành Công");
                }
            } else {
                System.out.println("khong thay doi");
            }
        });
        cf.createProductGrid(filteredProducts, gridPane);

        // ScrollPane for Product Grid Layout
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Add components to the main layout, including the ScrollPane
        mainLayout.getChildren()
                .addAll(topLayout, scrollPane, addNewProductButton, addNewCategoryProductButton);
        getChildren().add(mainLayout);
    }

    private static String showNewCategoryDialog(Stage primaryStage) {
        Dialog<String> dialog = new Dialog<>();
        AtomicReference<String> categoryNameText = new AtomicReference<>("");
        dialog.setHeaderText(null);

        URL cssFile = ProductListPage.class.getResource("/css/cssDiaLog.css");
        if (cssFile != null) {
            dialog.getDialogPane().getStylesheets().add(cssFile.toExternalForm());
        } else {
            System.out.println("File CSS không tồn tại tại đường dẫn: /css/cssDiaLogAddCategory.css");
        }

        TextField categoryName = new TextField();
        categoryName.getStyleClass().add("dialog-text-field");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Tên loại đồ uống:"), 0, 0);
        grid.add(categoryName, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Thêm ButtonType.OK và ButtonType.CANCEL để tránh NullPointerException
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String getCategoryNameText = categoryName.getText().trim();
                if (!getCategoryNameText.isEmpty()) {
                    return getCategoryNameText;
                } else {
                    AlertUtil.showErrorAlert("Vui lòng nhập tên loại đồ uống");
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }




    private void updateProductGrid(GridPane gridPane, List<Products> productList) {
        gridPane.getChildren().clear();
        cf.createProductGrid(productList, gridPane);
    }

    private static Products showNewProductDialog(Stage primaryStage) {
        Dialog<Products> dialog = new Dialog<>();
        dialog.setHeaderText(null);
        dialog.getDialogPane().getStylesheets().add(
                ProductListPage.class.getResource("/css/cssDiaLogAddNewProduct.css").toExternalForm()
        );

        TextField imageLink = new TextField();
        imageLink.getStyleClass().add("dialog-text-field");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        ImageView imageView = new ImageView();
        imageView.getStyleClass().add("dialog-image-view");
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        Button uploadButton = new Button("Upload Image");
        uploadButton.getStyleClass().add("dialog-button");
        uploadButton.setOnAction(e -> {
            fileChooser.setTitle("Select Image");
            selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                Image image = new Image(selectedFile.toURI().toString());
                imageView.setImage(image);
                imageLink.setText(selectedFile.getName());
            }
        });

        ComboBox<String> categoryField = CashierService.createPayCategoriesSelectionBox();

        TextField name = new TextField();
        name.getStyleClass().add("dialog-text-field");

        TextField price = new TextField();
        price.getStyleClass().add("dialog-text-field");

        grid.add(new Label("Ảnh"), 0, 0);
        grid.add(imageView, 1, 0);
        grid.add(uploadButton, 2, 0);
        grid.add(new Label("Loại Đồ Uống: "), 0, 1);
        grid.add(categoryField, 1, 1);
        grid.add(new Label("Tên Đồ Uống"), 0, 2);
        grid.add(name, 1, 2);
        grid.add(new Label("Giá"), 0, 3);
        grid.add(price, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                // Kiểm tra xem category, name và price có rỗng không
                if (name.getText() == null || name.getText().isEmpty() ||
                        price.getText() == null || price.getText().isEmpty() ||
                        categoryField.getValue() == null) {
                    AlertUtil.showErrorAlert("Vui lòng nhập đầy đủ thông tin.");
                    return null;
                }

                try {
                    // Chuyển đổi giá từ String sang Double
                    double parsedPrice = Double.parseDouble(price.getText());

                    // Kiểm tra và sao chép tệp nếu hợp lệ
                    if (selectedFile != null && selectedFile.exists()) {
                        // Tạo thư mục đích nếu chưa tồn tại
                        File destinationDir = new File("sampler/src/main/resources/images/products/");
                        if (!destinationDir.exists()) {
                            destinationDir.mkdirs();
                        }
                        File destinationFile = new File(destinationDir, selectedFile.getName());
                        Files.copy(selectedFile.toPath(), destinationFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        AlertUtil.showErrorAlert("Tệp không hợp lệ.");
                        return null;
                    }

                    // Trả về đối tượng Products mới
                    return new Products(
                            "/images/products/" + selectedFile.getName(),
                            name.getText(),
                            parsedPrice,
                            CashierService.getIdByCategoryName(categoryField.getValue())
                    );

                } catch (NumberFormatException e) {
                    AlertUtil.showErrorAlert("Giá không hợp lệ.");
                    return null;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    AlertUtil.showErrorAlert("Lỗi khi sao chép tệp.");
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}
