package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.dialog.AddStaffDialog;
import atlantafx.sampler.admin.page.dialog.ChangePasswordDialog;
import atlantafx.sampler.admin.entity.Staff;
import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.util.Lazy;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class StaffListPage extends OutlinePage {
    public static final String NAME = "Staff List";
    private TableView<Staff> table;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private Pagination pagination;
    private final Lazy<AddStaffDialog> addStaffDialog;
    private final Lazy<ChangePasswordDialog> changePasswordDialog;


    @Override
    public String getName() {
        return NAME;
    }

    public StaffListPage() {
        super();
        initialize();

        addStaffDialog = new Lazy<>(() -> {
            var dialog = new AddStaffDialog(); // Create the dialog for adding a staff member
            dialog.setClearOnClose(true); // Assuming it has this method
            return dialog;
        });


        changePasswordDialog = new Lazy<>(() -> {
            var dialog = new ChangePasswordDialog();
            dialog.setClearOnClose(true);
            return dialog;
        });
    }

    private void initialize() {
        // Search field and status filter
        searchField = new TextField();
        searchField.setPromptText("Search by name, ID, or email");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateTableData());

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Active", "Dropout", "Fire");
        statusFilter.setValue("Active"); // Default filter to show only active staff
        statusFilter.setOnAction(e -> updateTableData());

        // Layout for search and filter
        var searchPane = new HBox(10, searchField, statusFilter);

        // Create columns for the staff table
        var col1 = new TableColumn<Staff, String>("Staff ID");
        col1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffId()));

        var col2 = new TableColumn<Staff, String>("Name");
        col2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        var col3 = new TableColumn<Staff, String>("Contact Number");
        col3.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContactNumber()));

        var col4 = new TableColumn<Staff, String>("Email");
        col4.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        var col5 = new TableColumn<Staff, String>("Gender");
        col5.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGender()));

        var col6 = new TableColumn<Staff, String>("Role");
        col6.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));

        // Action column setup
        var actionCol = new TableColumn<Staff, Void>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button resignBtn = new Button("Resign");
            private final Button fireBtn = new Button("Fire");
            private final Button rehireBtn = new Button("Rehire");

            {
                resignBtn.setStyle("-fx-background-color: orange;");
                resignBtn.setOnAction(e -> confirmAndExecuteAction("resign", getTableView().getItems().get(getIndex()), "dropout"));

                fireBtn.setStyle("-fx-background-color: red;");
                fireBtn.setOnAction(e -> confirmAndExecuteAction("fire", getTableView().getItems().get(getIndex()), "fire"));

                rehireBtn.setStyle("-fx-background-color: green;");
                rehireBtn.setOnAction(e -> updateStaffStatus(getTableView().getItems().get(getIndex()), "active"));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Staff staff = getTableRow().getItem();
                    if ("dropout".equals(staff.getStatus())) {
                        setGraphic(rehireBtn);
                    } else if ("active".equals(staff.getStatus())) {
                        setGraphic(new HBox(5, resignBtn, fireBtn));
                    } else {
                        setGraphic(null); // Hide for fired staff
                    }
                }
            }
        });

        var changePasswordCol = new TableColumn<Staff, Void>("Change Password");
        changePasswordCol.setCellFactory(param -> new TableCell<>() {
            private final Button changePasswordBtn = new Button("Change Password");

            {
                changePasswordBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
                changePasswordBtn.setOnAction(e -> openChangePasswordDialog(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(changePasswordBtn);
                }
            }
        });

        // Table setup
        table = new TableView<>();
        table.getColumns().setAll(col1, col2, col3, col4, col5, col6, actionCol, changePasswordCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Pagination setup
        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);

        // New Staff button
        Button newStaffButton = new Button("Add New Staff");
        newStaffButton.setOnAction(e -> openAddStaffDialog());

        // Layout setup
        var layout = new VBox(10, searchPane, table, pagination, newStaffButton);
        getChildren().addAll(layout);
    }

    private void confirmAndExecuteAction(String action, Staff staff, String status) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setHeaderText("Are you sure you want to " + action + " this staff member?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateStaffStatus(staff, status);
        }
    }

    private void updateTableData() {
        // Get current page index and items per page for pagination
        int currentPage = pagination.getCurrentPageIndex();
        int itemsPerPage = 10; // Assuming 10 items per page

        // Fetch filtered data from the database
        List<Staff> filteredStaff = fetchStaffFromDatabase(currentPage, itemsPerPage);

        // Update table with the filtered data
        table.getItems().setAll(filteredStaff);

        // Update pagination based on total staff count after filtering
        updatePagination();
    }

    private void updatePagination() {
        // Get total count of filtered staff
        int totalItems = fetchFilteredStaffCount();

        // Update pagination with the new total item count and items per page
        int itemsPerPage = 10;
        int pageCount = (int) Math.ceil((double) totalItems / itemsPerPage);
        pagination.setPageCount(pageCount);
    }

    private int fetchFilteredStaffCount() {
        String countQuery = """
        SELECT COUNT(*) AS total
        FROM staff s
        LEFT JOIN staff_status ss ON s.status_id = ss.status_id
        WHERE ss.status_name = ? AND (s.name LIKE ? OR s.staff_id LIKE ? OR s.email LIKE ?)
    """;

        int count = 0;
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(countQuery)) {

            stmt.setString(1, statusFilter.getValue());
            String search = "%" + searchField.getText() + "%";
            stmt.setString(2, search);
            stmt.setString(3, search);
            stmt.setString(4, search);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }


    private List<Staff> fetchStaffFromDatabase(int pageNum, int itemsPerPage) {
        List<Staff> staffList = new ArrayList<>();
        String query = """
                SELECT s.staff_id, s.name, s.contact_number, s.email, s.gender, r.role_name, ss.status_name
                FROM staff s
                LEFT JOIN role r ON s.role_id = r.id
                LEFT JOIN staff_status ss ON s.status_id = ss.status_id
                WHERE ss.status_name = ? AND (s.name LIKE ? OR s.staff_id LIKE ? OR s.email LIKE ?)
                LIMIT ? OFFSET ?""";

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters for status filter, search term, and pagination
            stmt.setString(1, statusFilter.getValue());
            String search = "%" + searchField.getText() + "%";
            stmt.setString(2, search);
            stmt.setString(3, search);
            stmt.setString(4, search);
            stmt.setInt(5, itemsPerPage);
            stmt.setInt(6, pageNum * itemsPerPage);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                staffList.add(new Staff(
                        rs.getString("staff_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getString("role_name"),
                        rs.getString("status_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return staffList;
    }

    private VBox createPage(int pageIndex) {
        int itemsPerPage = 10;
        table.getItems().setAll(fetchStaffFromDatabase(pageIndex, itemsPerPage));
        return new VBox(table);
    }

    private void updateStaffStatus(Staff staff, String status) {
        String updateQuery = """
        UPDATE staff s
        JOIN staff_status ss ON ss.status_name = ?
        SET s.status_id = ss.status_id
        WHERE s.staff_id = ?
    """;

        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Set the status name and staff ID in the query
            stmt.setString(1, status);
            stmt.setString(2, staff.getStaffId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Staff status updated successfully.");
                updateTableData(); // Refresh the table data
            } else {
                System.out.println("Failed to update staff status.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openAddStaffDialog() {
        var dialog = addStaffDialog.get(); // Get the dialog instance
        dialog.show(getScene()); // Show the dialog in the current scene

    }

    private void openChangePasswordDialog(Staff staff) {
        var dialog = changePasswordDialog.get(); // Get the dialog instance
        dialog.setStaffDetails(staff.getStaffId(), staff.getName()); // Set the staff details using the correct method
        dialog.show(getScene()); // Show the dialog
    }


}
