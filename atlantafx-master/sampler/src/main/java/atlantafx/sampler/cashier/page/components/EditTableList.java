package atlantafx.sampler.cashier.page.components;

import atlantafx.sampler.base.entity.common.Tables;
import atlantafx.sampler.base.service.cashier.TableCoffeeService;
import atlantafx.sampler.base.util.AlertUtil;
import atlantafx.sampler.cashier.page.OutlinePage;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.Optional;

public class EditTableList extends OutlinePage {

  public static final String NAME = "Edit List Tables";
  private GridPane grid;
  private static String title;
  private int currentPage = 1;
  private final int itemsPerPage = 12;
  private String currentKeyword = ""; // Store the current search keyword
  private TextField searchField; // Text field for keyword search


  @Override
  public String getName() {
    return NAME;
  }

  public EditTableList() {
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

    // "New Table" button
    Button newTableButton = new Button("Thêm mới bàn");
    newTableButton.getStyleClass().add("button-add");
    newTableButton.setOnAction(e -> handleNewTable());

    // Container for "New Table" button and search field
    HBox topControls = new HBox(10, newTableButton, searchField);
    topControls.setAlignment(Pos.CENTER_LEFT);
    topControls.getStyleClass().add("top-controls");

    // Center the searchField
    HBox.setHgrow(searchField, Priority.ALWAYS); // Allows the searchField to grow
    searchField.setMinWidth(300); // Set a minimum width for better appearance

    // Create GridPane for tables
    grid = new GridPane();
    grid.getStyleClass().add("grid-pane");

    // Load initial table data based on the current page and search keyword
    ArrayList<String> floorTables = TableCoffeeService.getNameTable(currentPage, itemsPerPage,
            currentKeyword);
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
      if ((currentPage - 1) * itemsPerPage + floorTables.size()
              < TableCoffeeService.getFilteredTableCount(currentPage, itemsPerPage, currentKeyword)) {
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

    // Add elements to the layout
    layout.getChildren().addAll(labelFloorTables, topControls, grid, statusBox, paginationControls);
    layout.getStylesheets().add(getClass().getResource("/css/editListTable.css").toExternalForm());

    // Set the layout as the root content
    getChildren().clear();
    getChildren().add(layout);
  }

  private void handleSearch(javafx.scene.input.KeyEvent keyEvent) {
    currentKeyword = searchField.getText().trim();
    currentPage = 1; // Reset to first page after search
    refreshGrid();
  }

  // Event handler for search field

  private void addButtonsToGrid(GridPane grid, ArrayList<String> floorTables) {
    grid.getChildren().clear(); // Clear the grid before adding new buttons

    int row = 0;
    int col = 0;

    for (String tableName : floorTables) {
      Button tableButton = new Button(tableName);
      tableButton.getStyleClass().add("button-grid");
      updateTableButtonColor(tableButton, TableCoffeeService.getTableByName(tableName));
      // Create Edit and Delete buttons for each table
      Button deleteButton = new Button("Xóa");

      // Add style classes for modern look

      deleteButton.getStyleClass().add("delete-button");

      // Set actions for the Edit and Delete buttons

      deleteButton.setOnAction(e -> handleDeleteAction(tableName));

      // Create an HBox to hold the edit and delete buttons
      HBox buttonBox = new HBox(5, deleteButton);
      buttonBox.setAlignment(Pos.CENTER);

      // Add the table button and the edit/delete buttons to a VBox
      VBox tableBox = new VBox(5, tableButton, buttonBox);
      tableBox.setAlignment(Pos.CENTER);

      // Add the VBox to the grid
      grid.add(tableBox, col, row);

      // Update column and row for next button
      col++;
      if (col == 3) { // Adjust the number based on how many columns you want per row
        col = 0;
        row++;
      }
    }
  }


  private void handleDeleteAction(String tableName) {
    Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
    confirmationDialog.setTitle("Xác Nhận Xóa");
    confirmationDialog.setHeaderText("Bạn có chắc chắn muốn xóa?");
    confirmationDialog.setContentText("Hành động này không thể hoàn tác.");
    confirmationDialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/cssDiaLog.css").toExternalForm()
    );
    // Hiển thị dialog và xử lý lựa chọn của người dùng
    Optional<ButtonType> result = confirmationDialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      int StatusId = TableCoffeeService.getStatusByTableName(tableName);
      switch (StatusId) {
        case 1, 2:
          AlertUtil.showErrorAlert("Lỗi Không xóa được bàn");
          break;
        case 3:
          TableCoffeeService.deleteTableByName(tableName);
          AlertUtil.showErrorAlert("Xóa Thành Công");
          refreshGrid();
          break;
        default:
          break;
      }
    } else {
      System.out.println("Hủy xóa");
    }
    // Confirm deletion and remove the table from the service/database if confirmed
  }


  private void refreshGrid() {
    ArrayList<String> floorTables = TableCoffeeService.getNameTable(currentPage, itemsPerPage,
            currentKeyword);
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


  private void updateTableButtonColor(Button tableButton, Tables table) {
    int statusId = table.getStatusId();
    switch (statusId) {
      case 1 -> tableButton.setStyle("-fx-background-color: green;");
      case 2 -> tableButton.setStyle("-fx-background-color: orange;");
      default -> tableButton.setStyle("-fx-background-color: lightgray;");
    }
  }

  private void handleNewTable() {
    // Create a new dialog
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Add New Table");
    dialog.setHeaderText("Enter new table details");

    // Set the dialog icon
    dialog.initModality(Modality.APPLICATION_MODAL);

    // Create a VBox layout for the dialog content
    VBox dialogLayout = new VBox(10);
    dialogLayout.getStyleClass().add("dialog-layout");

    // Add input fields for table information
    TextField tableNameField = new TextField();
    tableNameField.setPromptText("Enter table number");
    tableNameField.getStyleClass().add("text-field");

    // Add input fields to the layout
    dialogLayout.getChildren().addAll(new Label("Table Number:"), tableNameField);

    // Add buttons to the dialog
    ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(addButtonType, cancelButtonType);
    dialog.getDialogPane().setContent(dialogLayout);

    // Apply styles to the dialog buttons
    Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
    Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
    addButton.getStyleClass().add("button");
    cancelButton.getStyleClass().add("button cancel");

    // Set up "Add" button behavior
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == addButtonType) {
        String numberTable = tableNameField.getText();
        boolean isNumericNoSpaces = numberTable.matches("^[0-9]+$");

        if (isNumericNoSpaces) {
          String tableName = numberTable.length() == 1 ? "Table A0" + numberTable : "Table A" + numberTable;

          // Check if the table name already exists
          Tables table = TableCoffeeService.getTableByName(tableName);
          if (table == null) {  // Table does not exist, so add it
            TableCoffeeService.addNewTable(tableName);
            AlertUtil.showErrorAlert("Thêm bàn mới thành công");
            refreshGrid();
          } else {
            AlertUtil.showErrorAlert("Bàn đã tồn tại");
          }
        } else {
          AlertUtil.showErrorAlert("Vui lòng nhập một số hợp lệ");
        }
      }
      return null;
    });

    // Show the dialog
    dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/dialogEditListTable.css").toExternalForm());
    dialog.showAndWait();
  }


}
