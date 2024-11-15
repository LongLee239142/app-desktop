package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.entity.StaffSchedule;
import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MakeSchedulePage extends OutlinePage {
    public static final String NAME = "Make Schedule";
    private final DatePicker weekPicker;
    private final TableView<StaffSchedule> scheduleTable;
    private final ObservableList<StaffSchedule> staffSchedules;
    private final Map<Integer, String> shifts;
    private final Label weekLabel;

    public MakeSchedulePage() {
        super();
        weekPicker = new DatePicker(LocalDate.now());
        scheduleTable = new TableView<>();
        staffSchedules = FXCollections.observableArrayList();
        shifts = new HashMap<>();
        weekLabel = new Label();

        initializeShifts();
        initializeUI();
        updateWeekLabel(); // Cập nhật nhãn hiển thị khoảng thời gian tuần
    }

    private void initializeShifts() {
        // Khởi tạo các ca làm việc từ cơ sở dữ liệu
        try (Connection conn = JDBCConnect.getJDBCConnection()) {
            String sql = "SELECT shift_id, shift_name FROM shift";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int shiftId = rs.getInt("shift_id");
                String shiftName = rs.getString("shift_name");
                shifts.put(shiftId, shiftName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().add(createHeader());
        mainLayout.getChildren().add(createTable());

        // Nút để lưu lịch
        Button saveButton = new Button("Save Schedule");
        saveButton.setOnAction(e -> saveSchedule());

        mainLayout.getChildren().add(saveButton);

        getChildren().add(mainLayout);

        // Load the schedule for the initial selected week
        weekPicker.setOnAction(event -> {
            updateWeekLabel();
            loadScheduleForWeek(weekPicker.getValue()); // Tải lịch đã lưu cho tuần được chọn
        });
    }

    private ToolBar createHeader() {
        ToolBar header = new ToolBar();
        header.getItems().add(new Label("Select Week:"));
        header.getItems().add(weekPicker);

        // Cập nhật nhãn hiển thị khoảng thời gian tuần
        weekPicker.setOnAction(event -> updateWeekLabel());

        return header;
    }

    private TableView<StaffSchedule> createTable() {
        // Tạo cột cho tên nhân viên
        TableColumn<StaffSchedule, String> nameColumn = new TableColumn<>("Staff Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        scheduleTable.getColumns().add(nameColumn);

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (String day : daysOfWeek) {
            TableColumn<StaffSchedule, Integer> dayColumn = new TableColumn<>(day);
            dayColumn.setCellValueFactory(cellData -> cellData.getValue().getShiftProperty(day));
            dayColumn.setCellFactory(col -> new TableCell<StaffSchedule, Integer>() {
                private final ComboBox<Integer> shiftComboBox = new ComboBox<>(FXCollections.observableArrayList(shifts.keySet()));

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        shiftComboBox.setValue(item);
                        shiftComboBox.setOnAction(e -> {
                            StaffSchedule schedule = getTableView().getItems().get(getIndex());
                            schedule.setShift(day, shiftComboBox.getValue());
                        });
                        setGraphic(shiftComboBox);
                    }
                }
            });
            scheduleTable.getColumns().add(dayColumn);
        }

        // Tải dữ liệu nhân viên vào bảng
        loadStaffData();

        return scheduleTable;
    }

    private void loadStaffData() {
        // Tải dữ liệu nhân viên từ cơ sở dữ liệu
        try (Connection conn = JDBCConnect.getJDBCConnection()) {
            String sql = "SELECT staff_id, name FROM staff";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String staffId = rs.getString("staff_id");
                String name = rs.getString("name");
                staffSchedules.add(new StaffSchedule(staffId, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduleTable.setItems(staffSchedules);
    }

    private void saveSchedule() {
        boolean hasShiftAssigned = false; // Kiểm tra xem có ca làm việc nào được gán không

        try (Connection conn = JDBCConnect.getJDBCConnection()) {
            String sql = "INSERT INTO shift_assignment (staff_id, shift_id, assigned_date) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (StaffSchedule schedule : staffSchedules) {
                for (String day : schedule.getDays()) {
                    Integer shiftId = schedule.getShift(day);
                    if (shiftId != null) {
                        hasShiftAssigned = true;
                        stmt.setString(1, schedule.getStaffId());
                        stmt.setInt(2, shiftId);
                        stmt.setDate(3, java.sql.Date.valueOf(weekPicker.getValue().with(java.time.DayOfWeek.valueOf(day.toUpperCase()))));
                        stmt.addBatch();
                    }
                }
            }

            if (hasShiftAssigned) {
                stmt.executeBatch();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Schedule saved successfully!");
                successAlert.showAndWait();

                // Cập nhật lại bảng với lịch mới được lưu
                loadScheduleForWeek(weekPicker.getValue());
            } else {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Warning");
                warningAlert.setHeaderText(null);
                warningAlert.setContentText("No shifts assigned. Please assign at least one shift before saving.");
                warningAlert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("An error occurred while saving the schedule: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }


    private void loadScheduleForWeek(LocalDate selectedWeek) {
        // Xóa lịch hiện tại
        staffSchedules.clear();

        // Truy xuất dữ liệu ca làm việc từ cơ sở dữ liệu cho tuần đã chọn
        try (Connection conn = JDBCConnect.getJDBCConnection()) {
            String sql = "SELECT sa.staff_id, s.name, sa.shift_id, sa.assigned_date " +
                    "FROM shift_assignment sa " +
                    "JOIN staff s ON sa.staff_id = s.staff_id " +
                    "WHERE sa.assigned_date BETWEEN ? AND ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Tính toán khoảng thời gian của tuần được chọn
            LocalDate startDate = selectedWeek;
            LocalDate endDate = selectedWeek.plusDays(6);
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            Map<String, StaffSchedule> scheduleMap = new HashMap<>();
            while (rs.next()) {
                String staffId = rs.getString("staff_id");
                String name = rs.getString("name");
                int shiftId = rs.getInt("shift_id");
                LocalDate assignedDate = rs.getDate("assigned_date").toLocalDate();

                StaffSchedule schedule = scheduleMap.computeIfAbsent(staffId, id -> new StaffSchedule(staffId, name));
                schedule.setShift(assignedDate.getDayOfWeek().name(), shiftId);
            }

            staffSchedules.addAll(scheduleMap.values());
            scheduleTable.refresh(); // Cập nhật lại bảng với dữ liệu mới
        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("An error occurred while loading the schedule: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }


    private void loadStaffDataForWeek(LocalDate startOfWeek) {
        // Clear the current schedule list
        staffSchedules.clear();

        LocalDate endOfWeek = startOfWeek.plusDays(6); // Define the end of the selected week

        try (Connection conn = JDBCConnect.getJDBCConnection()) {
            // Query to fetch schedule data for the selected week
            String sql = "SELECT staff.staff_id, staff.name, shift_assignment.shift_id, shift_assignment.assigned_date " +
                    "FROM staff " +
                    "LEFT JOIN shift_assignment ON staff.staff_id = shift_assignment.staff_id " +
                    "AND shift_assignment.assigned_date BETWEEN ? AND ? " +
                    "ORDER BY staff.staff_id";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, java.sql.Date.valueOf(startOfWeek));
            stmt.setDate(2, java.sql.Date.valueOf(endOfWeek));
            ResultSet rs = stmt.executeQuery();

            // Create a map to store staff schedules for the selected week
            Map<String, StaffSchedule> staffScheduleMap = new HashMap<>();

            while (rs.next()) {
                String staffId = rs.getString("staff_id");
                String name = rs.getString("name");
                Integer shiftId = rs.getInt("shift_id");
                LocalDate assignedDate = rs.getDate("assigned_date").toLocalDate();

                StaffSchedule schedule = staffScheduleMap.computeIfAbsent(staffId, id -> new StaffSchedule(staffId, name));

                if (shiftId != null) {
                    String dayOfWeek = assignedDate.getDayOfWeek().name(); // Get day of the week
                    schedule.setShift(dayOfWeek, shiftId); // Assign the shift to the correct day
                }
            }

            staffSchedules.setAll(staffScheduleMap.values()); // Update the observable list
            scheduleTable.setItems(staffSchedules); // Refresh the table
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateWeekLabel() {
        // Cập nhật nhãn hiển thị khoảng thời gian tuần
        LocalDate startDate = weekPicker.getValue();
        LocalDate endDate = startDate.plusDays(6);
        weekLabel.setText("Week from " + startDate + " to " + endDate);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
