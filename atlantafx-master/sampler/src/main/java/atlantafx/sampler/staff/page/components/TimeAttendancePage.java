package atlantafx.sampler.staff.page.components;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.service.UserSession;
import atlantafx.sampler.staff.entity.AttendanceRecord;
import atlantafx.sampler.staff.page.OutlinePage;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeAttendancePage extends OutlinePage {
    public static final String NAME = "Time Attendance";

    private Label checkInStatus;
    private Label checkOutStatus;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private Button checkInButton;
    private Button checkOutButton;
    private Button viewAttendanceButton;
    private TableView<AttendanceRecord> attendanceTable;

    @Override
    public String getName() {
        return NAME;
    }

    public TimeAttendancePage() {
        super();
        initializeUI();
    }

    private void initializeUI() {
        checkInStatus = new Label("Chưa vào ca");
        checkOutStatus = new Label("Chưa ra ca");

        fromDatePicker = new DatePicker(LocalDate.now());
        toDatePicker = new DatePicker(LocalDate.now());

        checkInButton = new Button("Vào ca");
        checkInButton.setOnAction(e -> handleCheckIn());

        checkOutButton = new Button("Ra ca");
        checkOutButton.setOnAction(e -> handleCheckOut());

        viewAttendanceButton = new Button("Xem danh sách công");
        viewAttendanceButton.setOnAction(e -> loadAttendanceRecords());

        HBox datePickers = new HBox(10, new Label("Từ ngày:"), fromDatePicker, new Label("Đến ngày:"), toDatePicker);
        datePickers.setPadding(new Insets(10));

        attendanceTable = new TableView<>();
        setupTable();

        // Sử dụng GridPane để tổ chức các phần tử UI
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setVgap(20);
        layout.setHgap(10);
        layout.add(datePickers, 0, 0, 4, 1);
        layout.add(checkInButton, 0, 1);
        layout.add(checkInStatus, 1, 1);
        layout.add(checkOutButton, 2, 1);
        layout.add(checkOutStatus, 3, 1);
        layout.add(viewAttendanceButton, 0, 2);
        layout.add(attendanceTable, 0, 3, 4, 1);

        // Thêm vào Scene graph
        getChildren().add(layout); // Thay vì sử dụng BorderPane, trực tiếp thêm layout vào Scene graph
    }

    private void setupTable() {
        TableColumn<AttendanceRecord, String> dateColumn = new TableColumn<>("Ngày");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        TableColumn<AttendanceRecord, String> checkInColumn = new TableColumn<>("Giờ vào");
        checkInColumn.setCellValueFactory(cellData -> cellData.getValue().checkInProperty());

        TableColumn<AttendanceRecord, String> checkOutColumn = new TableColumn<>("Giờ ra");
        checkOutColumn.setCellValueFactory(cellData -> cellData.getValue().checkOutProperty());

        attendanceTable.getColumns().addAll(dateColumn, checkInColumn, checkOutColumn);
    }

    private void handleCheckIn() {
        UserSession session = UserSession.getInstance();
        String staffId = session.getStaffId();

        if (staffId == null || staffId.isEmpty()) {
            checkInStatus.setText("Lỗi: Không tìm thấy nhân viên");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO attendance (staff_id, check_in) VALUES (?, ?)")) {

            statement.setString(1, staffId);
            statement.setTimestamp(2, Timestamp.valueOf(now));

            statement.executeUpdate();

            checkInStatus.setText("Đã vào ca lúc " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        } catch (SQLException e) {
            e.printStackTrace();
            checkInStatus.setText("Lỗi khi vào ca.");
        }
    }

    private void handleCheckOut() {
        UserSession session = UserSession.getInstance();
        String staffId = session.getStaffId();

        if (staffId == null || staffId.isEmpty()) {
            checkOutStatus.setText("Lỗi: Không tìm thấy nhân viên");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE attendance SET check_out = ? WHERE staff_id = ? AND check_out IS NULL")) {

            statement.setTimestamp(1, Timestamp.valueOf(now));
            statement.setString(2, staffId);

            int updatedRows = statement.executeUpdate();

            if (updatedRows > 0) {
                checkOutStatus.setText("Đã ra ca lúc " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                checkOutStatus.setText("Lỗi: Không tìm thấy bản ghi vào ca.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            checkOutStatus.setText("Lỗi khi ra ca.");
        }
    }

    private void loadAttendanceRecords() {
        UserSession session = UserSession.getInstance();
        String staffId = session.getStaffId();

        if (staffId == null || staffId.isEmpty()) {
            checkInStatus.setText("Lỗi: Không tìm thấy nhân viên");
            return;
        }

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        attendanceTable.getItems().clear();

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT DATE(check_in) as attendance_date, check_in, check_out FROM attendance WHERE staff_id = ? AND check_in BETWEEN ? AND ?")) {

            statement.setString(1, staffId);
            statement.setDate(2, Date.valueOf(fromDate));
            statement.setDate(3, Date.valueOf(toDate));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LocalDate attendanceDate = resultSet.getDate("attendance_date").toLocalDate();
                Timestamp checkIn = resultSet.getTimestamp("check_in");
                Timestamp checkOut = resultSet.getTimestamp("check_out");

                attendanceTable.getItems().add(new AttendanceRecord(
                        attendanceDate.toString(),
                        checkIn != null ? checkIn.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "Chưa vào",
                        checkOut != null ? checkOut.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "Chưa ra"
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
