package atlantafx.sampler.staff.page.components;

import atlantafx.sampler.base.service.AttendanceService;
import atlantafx.sampler.staff.entity.AttendanceRecord;
import atlantafx.sampler.staff.page.OutlinePage;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;

public class WorkExplanationPage extends OutlinePage {
    public static final String NAME = "Work Explanation";
    private final AttendanceService attendanceService = new AttendanceService();
    private TableView<AttendanceRecord> tableView;

    @Override
    public String getName() {
        return NAME;
    }

    public WorkExplanationPage() {
        super();
        setupTable();
        loadData();
    }

    private void setupTable() {
        tableView = new TableView<>();

        TableColumn<AttendanceRecord, LocalDateTime> checkInCol = new TableColumn<>("Check In");
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));

        TableColumn<AttendanceRecord, LocalDateTime> checkOutCol = new TableColumn<>("Check Out");
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));

        TableColumn<AttendanceRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<AttendanceRecord, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<AttendanceRecord, Void>() {
            private final Button explainButton = new Button("Explain");

            {
                explainButton.setOnAction(e -> showExplanationForm(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(explainButton);
                }
            }
        });

        tableView.getColumns().addAll(checkInCol, checkOutCol, statusCol, actionCol);
        VBox vbox = new VBox(tableView);
        getChildren().add(vbox); // Add to the page layout
    }

    private void loadData() {
        List<AttendanceRecord> records = attendanceService.getExplanationsRequired();
        tableView.getItems().setAll(records);
    }

    private void showExplanationForm(AttendanceRecord record) {
        // Implement the form to submit explanations
        // You can create a new window or a dialog to show the form
    }
}
