package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.common.Voucher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VoucherManager extends OutlinePage {

    public static final String NAME = "Voucher Manager";
    private TableView<Voucher> voucherTable;
    private ObservableList<Voucher> voucherData = FXCollections.observableArrayList();
    private Pagination pagination;

    @Override
    public String getName() {
        return NAME;
    }

    public VoucherManager() {
        super();
        initializeUI();
    }

    private void initializeUI() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Initialize TableView
        voucherTable = new TableView<>();
        setupVoucherTable();

        // Populate table with data immediately
        loadInitialVoucherData();

        // File import button for Excel file
        Button importButton = new Button("Import Vouchers from Excel");
        importButton.setOnAction(e -> handleFileImport());

        // Form fields for voucher insertion
        TextField codeField = new TextField();
        codeField.setPromptText("Voucher Code");
        codeField.setDisable(true); // Disable code field as it's auto-generated
        TextField nameField = new TextField();
        nameField.setPromptText("Voucher Name");
        TextField percentageField = new TextField();
        percentageField.setPromptText("Discount Percentage");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        // Status will be set to 'ACTIVE' by default
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("ACTIVE", "INACTIVE", "USED");
        statusComboBox.setValue("ACTIVE"); // Default value

        Button addButton = new Button("Add Voucher");
        addButton.setOnAction(e -> {
            handleVoucherInsert(nameField.getText(), percentageField.getText(),
                    startDatePicker.getValue(), endDatePicker.getValue());
            clearFormFields(codeField, nameField, percentageField, startDatePicker, endDatePicker, statusComboBox);
        });

        // Layout arrangement for form fields
        GridPane formLayout = new GridPane();
        formLayout.setHgap(10);
        formLayout.setVgap(10);
        formLayout.add(importButton, 0, 0, 2, 1);
        formLayout.add(new Label("Voucher Code:"), 0, 1);
        formLayout.add(codeField, 1, 1);
        formLayout.add(new Label("Voucher Name:"), 0, 2);
        formLayout.add(nameField, 1, 2);
        formLayout.add(new Label("Discount Percentage:"), 0, 3);
        formLayout.add(percentageField, 1, 3);
        formLayout.add(new Label("Start Date:"), 0, 4);
        formLayout.add(startDatePicker, 1, 4);
        formLayout.add(new Label("End Date:"), 0, 5);
        formLayout.add(endDatePicker, 1, 5);
        formLayout.add(addButton, 1, 6);

        // HBox for layout (Table on the left, Form on the right)
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(voucherTable, formLayout);
        layout.getChildren().add(mainLayout);

        getChildren().add(layout);

        // Pagination setup
        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);
        layout.getChildren().add(pagination);
    }

    private void setupVoucherTable() {
        TableColumn<Voucher, String> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("voucherCode"));

        TableColumn<Voucher, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("voucherName"));

        TableColumn<Voucher, Double> percentageColumn = new TableColumn<>("Discount %");
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("voucherPercentage"));

        TableColumn<Voucher, LocalDate> startDateColumn = new TableColumn<>("Start Date");
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Voucher, LocalDate> endDateColumn = new TableColumn<>("End Date");
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<Voucher, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Action Column with Deactivate Button
        TableColumn<Voucher, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(col -> new TableCell<Voucher, Void>() {
            private final Button deactivateButton = new Button("Deactivate");

            {
                deactivateButton.setOnAction(e -> {
                    Voucher voucher = getTableView().getItems().get(getIndex());
                    if (voucher.getStatus().equals("ACTIVE")) {
                        deactivateVoucher(voucher);
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deactivateButton);
                }
            }
        });

        voucherTable.getColumns().addAll(codeColumn, nameColumn, percentageColumn, startDateColumn, endDateColumn, statusColumn, actionColumn);
    }

    private void deactivateVoucher(Voucher voucher) {
        // Update status to 'INACTIVE' in the database
        String updateSQL = "UPDATE voucher SET status = 'INACTIVE' WHERE voucher_code = ?";
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(updateSQL)) {

            statement.setString(1, voucher.getVoucherCode());
            statement.executeUpdate();

            // Update status in the list and refresh the table
            voucher.setStatus("INACTIVE");
            voucherTable.refresh();
            showAlert("Success", "Voucher deactivated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to deactivate voucher.");
        }
    }

    private void loadInitialVoucherData() {
        // Clear any existing data
        voucherData.clear();

        // SQL query to load voucher data
        String query = "SELECT voucher_code, voucher_name, voucher_percentage, start_date, end_date, status FROM voucher";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Loop through the result set and populate the voucherData list
            while (resultSet.next()) {
                String voucherCode = resultSet.getString("voucher_code");
                String voucherName = resultSet.getString("voucher_name");
                double voucherPercentage = resultSet.getDouble("voucher_percentage");
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                String status = resultSet.getString("status");

                Voucher voucher = new Voucher(voucherCode, voucherName, voucherPercentage, startDate, endDate, status);
                voucherData.add(voucher);
            }

            // Set the fetched data to the TableView
            voucherTable.setItems(voucherData);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load voucher data from database.");
        }
    }


    private void handleFileImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(getScene().getWindow());

        if (file != null) {
            importVouchersFromExcel(file);
        }
    }

    private void importVouchersFromExcel(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Voucher> vouchers = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String code = row.getCell(0).getStringCellValue();
                String name = row.getCell(1).getStringCellValue();
                int percentage = (int) row.getCell(2).getNumericCellValue();
                LocalDate startDate = row.getCell(3).getLocalDateTimeCellValue().toLocalDate();
                LocalDate endDate = row.getCell(4).getLocalDateTimeCellValue().toLocalDate();
                String status = row.getCell(5).getStringCellValue();

                vouchers.add(new Voucher(code, name, percentage, startDate, endDate, status));
            }

            saveVouchersToDatabase(vouchers); // Save to database
            voucherData.addAll(vouchers); // Add to table view
            showAlert("Success", "Vouchers imported successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to import vouchers.");
        }
    }

    private void saveVouchersToDatabase(List<Voucher> vouchers) {
        String insertSQL = "INSERT INTO voucher (voucher_code, voucher_name, voucher_percentage, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {

            for (Voucher voucher : vouchers) {
                statement.setString(1, voucher.getVoucherCode());
                statement.setString(2, voucher.getVoucherName());
                statement.setInt(3, voucher.getVoucherPercentage());
                statement.setDate(4, java.sql.Date.valueOf(voucher.getStartDate()));
                statement.setDate(5, java.sql.Date.valueOf(voucher.getEndDate()));
                statement.setString(6, voucher.getStatus());

                statement.executeUpdate();
            }

            showAlert("Success", "Vouchers imported successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save vouchers to database.");
        }
    }

    private void handleVoucherInsert(String name, String percentage, LocalDate startDate, LocalDate endDate) {
        String voucherCode = generateVoucherCode();
        String status = "ACTIVE"; // Automatically set to ACTIVE

        double percentageValue = 0;
        try {
            percentageValue = Double.parseDouble(percentage);
            if (percentageValue < 0 || percentageValue > 100) {
                showAlert("Error", "Discount percentage must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid discount percentage value.");
            return;
        }

        if (endDate.isBefore(LocalDate.now())) {
            showAlert("Error", "End date must be greater than the current date.");
            return;
        }

        Voucher voucher = new Voucher(voucherCode, name, percentageValue, startDate, endDate, status);
        saveVouchersToDatabase(List.of(voucher)); // Save to database
        voucherData.add(voucher); // Add to table view
    }

    private String generateVoucherCode() {
        return "VOUCHER-" + System.currentTimeMillis();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFormFields(TextField codeField, TextField nameField, TextField percentageField,
                                 DatePicker startDatePicker, DatePicker endDatePicker, ComboBox<String> statusComboBox) {
        codeField.clear();
        nameField.clear();
        percentageField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        statusComboBox.setValue("ACTIVE"); // Reset to default value
    }

    private int getPageCount() {
        return (int) Math.ceil((double) voucherData.size() / 10);
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * 10;
        int toIndex = Math.min(fromIndex + 10, voucherData.size());
        List<Voucher> pageItems = voucherData.subList(fromIndex, toIndex);
        ObservableList<Voucher> pageObservableList = FXCollections.observableArrayList(pageItems);

        voucherTable.setItems(pageObservableList);
        return new VBox(voucherTable);
    }
}
