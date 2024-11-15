package atlantafx.sampler.admin.page.components;


import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.entity.common.Bill;
import atlantafx.sampler.base.entity.common.Tables;
import atlantafx.sampler.base.service.cashier.CashierService;
import atlantafx.sampler.base.service.cashier.TableCoffeeService;
import atlantafx.sampler.cashier.page.components.OrderListPage;
import atlantafx.sampler.cashier.page.components.TableListPage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TableMapPage extends OutlinePage {

  public static final String NAME = "Table List Page";
  private static Label selectedTableLabel;
  private GridPane grid;
  private static String title;
  private int currentPage = 1;
  private final int itemsPerPage = 20;
  private String currentKeyword = ""; // Store the current search keyword
  private TextField searchField; // Text field for keyword search
  static OrderListPage orderListPage = new OrderListPage();


  @Override
  public String getName() {
    return NAME;
  }

  public TableMapPage() {
    super();
    createGrid();
  }

  private void createGrid() {
    VBox layout = new VBox();
    layout.getStyleClass().add("vbox");

    Label labelFloorTables = new Label("List Tables");
    labelFloorTables.getStyleClass().add("label-title");

    // Search TextField for filtering
    searchField = new TextField();
    searchField.setPromptText("Search by table name...");
    searchField.getStyleClass().add("text-field");
    searchField.setOnKeyReleased(this::handleSearch);

    // Create GridPane for tables
    grid = new GridPane();
    grid.getStyleClass().add("grid-pane");

    // Load initial table data based on the current page and search keyword
    ArrayList<String> floorTables = TableCoffeeService.getNameTable(currentPage, itemsPerPage, currentKeyword);
    addButtonsToGrid(grid, floorTables);

    // Pagination controls
    Button prevButton = new Button("Previous");
    Button nextButton = new Button("Next");
    prevButton.getStyleClass().add("button");
    nextButton.getStyleClass().add("button");

    prevButton.setOnAction(e -> {
      if (currentPage > 1) {
        currentPage--;
        refreshGrid();
      }
    });

    nextButton.setOnAction(e -> {
      if ((currentPage - 1) * itemsPerPage + floorTables.size() < TableCoffeeService.getFilteredTableCount(currentPage, itemsPerPage,currentKeyword)) {
        currentPage++;
        refreshGrid();
      }
    });

    HBox paginationControls = new HBox(10, prevButton, nextButton);
    paginationControls.getStyleClass().add("pagination");

    // Add status legend
    VBox statusBox = new VBox();
    statusBox.getStyleClass().add("status-box");
    setupStatusLegend(statusBox);

    layout.getChildren().addAll(labelFloorTables, searchField, grid, statusBox, paginationControls);
    layout.getStylesheets().add(getClass().getResource("/css/listTable.css").toExternalForm());

    getChildren().clear();
    getChildren().add(layout);
  }


  private void handleSearch(javafx.scene.input.KeyEvent keyEvent) {
    currentKeyword = searchField.getText().trim();
    currentPage = 1; // Reset to first page after search
    refreshGrid();
  }

  // Event handler for search field

  private void addButtonsToGrid(GridPane grid, ArrayList<String> tableNames) {
    int count = 0;
    int rows = 5;
    int cols = 4;

    grid.getChildren().clear(); // Clear previous buttons

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if (count >= tableNames.size()) break;

        // Create button for each table
        String tableName = tableNames.get(count);
        Button tableButton = new Button(tableName);
        tableButton.setPrefSize(200, 200);

        // Get table status and set button color
        Tables table = TableCoffeeService.getTableByName(tableName);
        updateTableButtonColor(tableButton, table);

        // Set click event
        tableButton.setOnAction(e -> handleTableButtonClick(tableButton, table));

        grid.add(tableButton, col, row);
        count++;
      }
    }
  }

  private void refreshGrid() {
    ArrayList<String> floorTables = TableCoffeeService.getNameTable(currentPage, itemsPerPage, currentKeyword);
    addButtonsToGrid(grid, floorTables);
  }

  private void setupStatusLegend(VBox statusBox) {
    Label reservedLabel = new Label("Đã đặt");
    reservedLabel.setStyle(
            "-fx-background-color: #28a745; " +  // A green color for a reserved state
                    "-fx-text-fill: #ffffff; " +         // White text for good contrast
                    "-fx-padding: 8px 16px; " +          // Padding for a spacious look
                    "-fx-pref-width: 150px; " +
                    "-fx-alignment: center; " +
                    "-fx-background-radius: 8px;"       // Rounded corners for a modern look
    );

    Label unavailableLabel = new Label("Đang dọn");
    unavailableLabel.setStyle(
            "-fx-background-color: #ff9800; " +  // A vibrant orange color for cleaning status
                    "-fx-text-fill: #ffffff; " +         // White text for contrast
                    "-fx-padding: 8px 16px; " +
                    "-fx-pref-width: 150px; " +
                    "-fx-alignment: center; " +
                    "-fx-background-radius: 8px;"
    );

    Label availableLabel = new Label("Còn Chỗ");
    availableLabel.setStyle(
            "-fx-background-color: #dcdcdc; " +  // A subtle gray for available status
                    "-fx-text-fill: #000000; " +         // Black text for better readability
                    "-fx-padding: 8px 16px; " +
                    "-fx-pref-width: 150px; " +
                    "-fx-alignment: center; " +
                    "-fx-background-radius: 8px;"
    );

    // Adjusting the widths to keep uniformity.
    availableLabel.setMinWidth(150);
    reservedLabel.setMinWidth(150);

    statusBox.getChildren().addAll(availableLabel, unavailableLabel, reservedLabel);
  }


  private void handleTableButtonClick(Button tableButton, Tables table) {
    int status = table.getStatusId();
    if (status == 2) {
      Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
      confirmationDialog.setTitle("Xác nhận bàn đã dọn xong");
      confirmationDialog.setHeaderText("Bạn có chắc chắn là bàn này khách đã dời đi và đã dọn xong");
      confirmationDialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/cssDiaLog.css").toExternalForm());

      Optional<ButtonType> result = confirmationDialog.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        TableCoffeeService.updateStatusTable(3, table.getName());
        table.setStatusId(3);
        updateTableButtonColor(tableButton, table);
      }
    } else {
      selectedTableLabel = new Label(tableButton.getText());
      TableListPage.setTitle(tableButton.getText());
      orderListPage.showCheckOrderDialog();

      updateStatusTableByOrder(tableButton.getText());
      table.setStatusId(TableCoffeeService.getStatusByTableName(table.getName()));
      updateTableButtonColor(tableButton, table);
    }
  }

  private void updateTableButtonColor(Button tableButton, Tables table) {
    int statusId = table.getStatusId();
    switch (statusId) {
      case 1 -> tableButton.setStyle("-fx-background-color: green;");
      case 2 -> tableButton.setStyle("-fx-background-color: orange;");
      default -> tableButton.setStyle("-fx-background-color: lightgray;");
    }
  }

  public static void updateStatusTableByOrder(String tableName) {
    List<Bill> newBill = CashierService.getBillByNameTable(tableName);
    Tables table = TableCoffeeService.getTableByName(tableName);
    int statusId = table.getStatusId();
    if (!newBill.isEmpty()) {
      TableCoffeeService.updateStatusTable(1, tableName);
    } else {
      if (statusId != 2) {
        TableCoffeeService.updateStatusTable(3, tableName);
      }
    }
  }

}