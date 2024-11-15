package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.common.Discount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Iterator;

public class DiscountManager extends OutlinePage {

    public static final String NAME = "Discount Manager";
    private TableView<Discount> discountTable;
    private ObservableList<Discount> discountData = FXCollections.observableArrayList();
    private ListView<String> productSuggestionsListView;
    private TextField productNameField;
    private Pagination pagination;

    @Override
    public String getName() {
        return NAME;
    }

    public DiscountManager() {
        super();
        initializeUI();
    }

    private void initializeUI() {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));

        // Initialize TableView for Discounts
        discountTable = new TableView<>();
        setupDiscountTable();

        // Pagination setup
        pagination = new Pagination();
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory(this::createPage);

        // TextField for product name input
        productNameField = new TextField();
        productNameField.setPromptText("Enter Product Name");

        // ListView to show product suggestions
        productSuggestionsListView = new ListView<>();
        productSuggestionsListView.setVisible(false); // Initially hidden
        productSuggestionsListView.setPrefHeight(150);

        // Listener to show suggestions when the user types
        productNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                showProductSuggestions(newValue);
            } else {
                productSuggestionsListView.setVisible(false);
            }
        });

        // Handle selecting a product from the suggestions
        productSuggestionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                productNameField.setText(newValue);
                productSuggestionsListView.setVisible(false);
            }
        });

        // Form fields for discount insertion
        TextField discountNameField = new TextField();
        discountNameField.setPromptText("Discount Name");
        TextField percentageField = new TextField();
        percentageField.setPromptText("Discount Percentage");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        // Import Excel Button
        Button importButton = new Button("Import Excel File");
        importButton.setOnAction(e -> handleImportButtonClick());

        // Add Discount Button
        Button addButton = new Button("Add Discount");
        addButton.setOnAction(e -> {
            handleDiscountInsert(
                    productNameField.getText(), discountNameField.getText(),
                    percentageField.getText(), startDatePicker.getValue(),
                    endDatePicker.getValue()
            );
            clearFormFields(discountNameField, percentageField, startDatePicker, endDatePicker);
        });

        // VBox for product name field and suggestions
        VBox productInputLayout = new VBox(5);  // Small vertical spacing
        productInputLayout.getChildren().addAll(productNameField, productSuggestionsListView);

        // Layout arrangement for form
        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(10));
        formLayout.getChildren().addAll(
                productInputLayout, discountNameField, percentageField, startDatePicker, endDatePicker,
                addButton, importButton
        );

        // Create a split layout (left for table, right for form)
        layout.getChildren().addAll(discountTable, formLayout, pagination);
        getChildren().add(layout);

        // Load initial discount data immediately after UI setup
        loadInitialDiscountData();  // Fetch data from the database
    }


    private void setupDiscountTable() {
        TableColumn<Discount, String> productColumn = new TableColumn<>("Product Name");
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<Discount, String> nameColumn = new TableColumn<>("Discount Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("discountName"));

        TableColumn<Discount, Double> percentageColumn = new TableColumn<>("Discount %");
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercentage"));

        TableColumn<Discount, LocalDate> startDateColumn = new TableColumn<>("Start Date");
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Discount, LocalDate> endDateColumn = new TableColumn<>("End Date");
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        discountTable.getColumns().addAll(productColumn, nameColumn, percentageColumn, startDateColumn, endDateColumn);
        discountTable.setItems(discountData);
    }

    private void loadInitialDiscountData() {
        // Load some initial data from SQL when the page is first opened
        discountData.clear(); // Clear existing data if any

        String query = "SELECT p.name AS product_name, d.discount_name, d.discount_percentage, d.start_date, d.end_date " +
                "FROM discount d JOIN products p ON d.product_id = p.id";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String productName = rs.getString("product_name");
                String discountName = rs.getString("discount_name");
                double discountPercentage = rs.getDouble("discount_percentage");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();

                Discount discount = new Discount(productName, discountName, discountPercentage, startDate, endDate);
                discountData.add(discount);
            }

            // Update the pagination control with the number of pages
            pagination.setPageCount((discountData.size() + 9) / 10); // Set number of pages (10 items per page)

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load discount data.");
        }
    }

    private void showProductSuggestions(String searchText) {
        // Query the database for products that match the entered text
        String query = "SELECT name FROM products WHERE name LIKE ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();

            ObservableList<String> productNames = FXCollections.observableArrayList();
            while (rs.next()) {
                productNames.add(rs.getString("name"));
            }

            if (!productNames.isEmpty()) {
                productSuggestionsListView.setItems(productNames);
                productSuggestionsListView.setVisible(true);
            } else {
                productSuggestionsListView.setVisible(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch products.");
        }
    }

    private void handleDiscountInsert(String productName, String discountName, String percentage, LocalDate startDate, LocalDate endDate) {
        if (productName == null || productName.isEmpty()) {
            showAlert("Error", "Product must be selected.");
            return;
        }

        try {
            double discountPercentage = Double.parseDouble(percentage);
            if (discountPercentage < 0 || discountPercentage > 100) {
                showAlert("Error", "Discount percentage must be between 0 and 100.");
                return;
            }

            // Fetch product ID based on product name
            String productIdQuery = "SELECT id FROM products WHERE name = ?";
            int productId = -1;
            try (Connection conn = JDBCConnect.getJDBCConnection();
                 PreparedStatement stmt = conn.prepareStatement(productIdQuery)) {

                stmt.setString(1, productName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    productId = rs.getInt("id");
                } else {
                    showAlert("Error", "Product not found.");
                    return;
                }
            }

            // Insert the discount into the database
            String insertSQL = "INSERT INTO discount (product_id, discount_name, discount_percentage, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = JDBCConnect.getJDBCConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

                stmt.setInt(1, productId);
                stmt.setString(2, discountName);
                stmt.setDouble(3, discountPercentage);
                stmt.setDate(4, Date.valueOf(startDate));
                stmt.setDate(5, Date.valueOf(endDate));

                stmt.executeUpdate();
                Discount discount = new Discount(productName, discountName, discountPercentage, startDate, endDate);
                discountData.add(discount);
                showAlert("Success", "Discount added successfully!");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid discount percentage.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to insert discount.");
        }
    }

    private void handleImportButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        Stage stage = (Stage) getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            importExcelFile(selectedFile);
        }
    }

    private void importExcelFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip the header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String productName = row.getCell(0).getStringCellValue();
                String discountName = row.getCell(1).getStringCellValue();
                double discountPercentage = row.getCell(2).getNumericCellValue();
                LocalDate startDate = row.getCell(3).getLocalDateTimeCellValue().toLocalDate();
                LocalDate endDate = row.getCell(4).getLocalDateTimeCellValue().toLocalDate();

                Discount discount = new Discount(productName, discountName, discountPercentage, startDate, endDate);
                discountData.add(discount);
            }

            showAlert("Success", "Excel file imported successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to import Excel file.");
        }
    }

    private void clearFormFields(TextField discountNameField, TextField percentageField, DatePicker startDatePicker, DatePicker endDatePicker) {
        discountNameField.clear();
        percentageField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Pagination logic
    private Node createPage(int pageIndex) {
        int itemsPerPage = 10;
        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, discountData.size());

        // Slice the data for pagination
        ObservableList<Discount> pagedData = FXCollections.observableArrayList();
        for (int i = start; i < end; i++) {
            pagedData.add(discountData.get(i));
        }

        // Update the existing table with paged data
        discountTable.setItems(pagedData);

        return discountTable;
    }
}
