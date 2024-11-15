package atlantafx.sampler.staff.page.components;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.staff.entity.AttendanceRecord;
import atlantafx.sampler.staff.page.OutlinePage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalaryReportPage extends OutlinePage {
    public static final String NAME = "Salary Report";

    private DatePicker datePicker;
    private Button fetchReportButton;
    private TableView<AttendanceRecord> tableView;
    private Label totalSalaryLabel;

    @Override
    public String getName() {
        return NAME;
    }

    public SalaryReportPage() {
        super();
        createUI();
    }

    private void createUI() {
        datePicker = new DatePicker();
        datePicker.setPromptText("Select a Month");
        fetchReportButton = new Button("Fetch Report");
        totalSalaryLabel = new Label("Total Salary: 0");

        tableView = new TableView<>();
        setupTable();

        fetchReportButton.setOnAction(event -> fetchSalaryReport());

        VBox vbox = new VBox(10, datePicker, fetchReportButton, totalSalaryLabel, tableView);
        getChildren().add(vbox);
    }

    private void setupTable() {
        TableColumn<AttendanceRecord, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        TableColumn<AttendanceRecord, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        tableView.getColumns().addAll(dateColumn, statusColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void fetchSalaryReport() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a date!");
            alert.showAndWait();
            return;
        }

        int month = selectedDate.getMonthValue();
        int year = selectedDate.getYear();

        ObservableList<AttendanceRecord> records = FXCollections.observableArrayList();
        double totalSalary = 0;

        // Tạo danh sách chứa tất cả các ngày trong tháng
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        // Duyệt tất cả các ngày trong tháng
        for (LocalDate date = firstDayOfMonth; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
            String formattedDate = date.format(DateTimeFormatter.ISO_DATE);
            boolean recordFound = false;

            try (Connection connection = JDBCConnect.getJDBCConnection()) {
                String query = "SELECT a.status, r.basic_salary, r.allowance, " +
                        "SUM(CASE WHEN a.status IN ('On Time', 'Late', 'Early Leave') THEN 1 ELSE 0 END) AS work_days, " +
                        "SUM(CASE WHEN a.status = 'Late' THEN 1 ELSE 0 END) AS late_count, " +
                        "SUM(CASE WHEN a.status = 'Early Leave' THEN 1 ELSE 0 END) AS early_leave_count " +
                        "FROM attendance a " +
                        "JOIN staff s ON a.staff_id = s.staff_id " +
                        "JOIN role r ON s.role_id = r.id " +
                        "WHERE MONTH(a.check_in) = ? AND YEAR(a.check_in) = ? AND DAY(a.check_in) = ? " +
                        "GROUP BY a.status, r.basic_salary, r.allowance";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setInt(1, month);
                    pstmt.setInt(2, year);
                    pstmt.setInt(3, date.getDayOfMonth());
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        String status = rs.getString("status");
                        double basicSalary = rs.getDouble("basic_salary");
                        double allowance = rs.getDouble("allowance");
                        int workDays = rs.getInt("work_days");
                        int lateCount = rs.getInt("late_count");
                        int earlyLeaveCount = rs.getInt("early_leave_count");

                        // Tính lương
                        double hourlyRate = basicSalary;
                        int hoursPerDay = 8;
                        double salary = (hoursPerDay * hourlyRate * workDays) + allowance;

                        // Tính tiền phạt
                        double penaltyRate = 0.05;
                        double penalty = (lateCount + earlyLeaveCount) * (penaltyRate * allowance);
                        salary -= penalty;
                        totalSalary += salary;

                        // Thêm bản ghi vào danh sách
                        records.add(new AttendanceRecord(formattedDate, status));
                        recordFound = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Nếu không tìm thấy bản ghi cho ngày hiện tại, thêm bản ghi với trạng thái "No Shift"
            if (!recordFound) {
                records.add(new AttendanceRecord(formattedDate, "No Shift"));
            }
        }

        tableView.setItems(records);

        // Định dạng tổng lương
        totalSalaryLabel.setText("Total Salary: " + String.format("%.2f", totalSalary));
        colorTableRows(records);
    }

    private void colorTableRows(ObservableList<AttendanceRecord> records) {
        tableView.setRowFactory(tv -> new TableRow<AttendanceRecord>() {
            @Override
            protected void updateItem(AttendanceRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    switch (item.getStatus()) {
                        case "On Time":
                            setStyle("-fx-background-color: lightgreen;"); // Màu xanh cho On Time
                            break;
                        case "Late":
                        case "Early Leave":
                            setStyle("-fx-background-color: yellow;"); // Màu vàng cho Late và Early Leave
                            break;
                        case "No Shift":
                        case "Explanation Required":
                            setStyle("-fx-background-color: red;"); // Màu đỏ cho No Shift và Explanation Required
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

}
