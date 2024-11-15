package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.entity.admin.Admin;
import atlantafx.sampler.base.entity.cashier.Cashier;
import atlantafx.sampler.base.entity.common.StoreInfo;
import atlantafx.sampler.base.service.admin.SettingsService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminSettingsPage extends OutlinePage {
    public static final String NAME = "Admin Settings";

    private Label messageLabel = new Label();

    @Override
    public String getName() {
        return NAME;
    }

    public AdminSettingsPage() {
        super();
        initializeUI();
    }

    private void initializeUI() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        // Add individual sections as cards
        layout.getChildren().addAll(
                createAdminInfoSettingCard(),
                createCashierInfoSettingCard(),
                createStoreInfoSettingCard()
        );

        // Wrap the layout in a ScrollPane to make it scrollable
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true); // Ensure the layout fits within the scroll pane
        getChildren().add(scrollPane);
    }

    private BorderPane createAdminInfoSettingCard() {
        BorderPane card = new BorderPane();
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #007bff; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        // Title section with left-aligned blue bar
        HBox titleBox = new HBox();
        titleBox.setStyle("-fx-background-color: #ADD8E6; -fx-padding: 5;");
        Label title = new Label("Change Admin Information");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fff;");
        titleBox.getChildren().add(title);
        card.setTop(titleBox);

        // Message label for feedback
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
        card.setBottom(messageLabel);

        // Get current admin information from the database
        SettingsService adminDataFetcher = new SettingsService();
        Admin currentAdmin = adminDataFetcher.getAdminInfo();

        GridPane formLayout = new GridPane();
        formLayout.setVgap(10);
        formLayout.setHgap(10);
        formLayout.setAlignment(Pos.CENTER);

        // Username and Email Fields (pre-filled with existing data)
        TextField usernameField = new TextField(currentAdmin.getUsername());
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(200); // Ensure input fields are wide enough

        TextField emailField = new TextField(currentAdmin.getEmail());
        emailField.setPromptText("Email");
        emailField.setPrefWidth(200);

        // Admin password fields (current password is not shown for security reasons)
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        Button updateButton = new Button("Update Admin Information");
        updateButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        updateButton.setPrefWidth(200); // Set button width equal to the input fields
        updateButton.setOnAction(event -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Validate and update the information
            if (updateAdminInfo(username, email, currentPassword, newPassword, confirmPassword)) {
                messageLabel.setText("Admin information updated successfully.");
            } else {
                messageLabel.setText("Failed to update admin information. Check your credentials.");
            }
        });

        formLayout.add(usernameField, 0, 0);
        formLayout.add(emailField, 0, 1);
        formLayout.add(currentPasswordField, 0, 2);
        formLayout.add(newPasswordField, 0, 3);
        formLayout.add(confirmPasswordField, 0, 4);
        formLayout.add(updateButton, 0, 5);

        card.setCenter(formLayout);
        return card;
    }

    private BorderPane createCashierInfoSettingCard() {
        BorderPane card = new BorderPane();
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #007bff; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        // Title section with left-aligned blue bar
        HBox titleBox = new HBox();
        titleBox.setStyle("-fx-background-color: #ADD8E6; -fx-padding: 5;");
        Label title = new Label("Change Cashier Information");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fff;");
        titleBox.getChildren().add(title);
        card.setTop(titleBox);

        // Message label for feedback
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
        card.setBottom(messageLabel);

        // Get current cashier information from the database
        SettingsService cashierDataFetcher = new SettingsService();
        Cashier currentCashier = cashierDataFetcher.getCashierInfo();

        GridPane formLayout = new GridPane();
        formLayout.setVgap(10);
        formLayout.setHgap(10);
        formLayout.setAlignment(Pos.CENTER);

        // Username Field (pre-filled with existing data)
        TextField usernameField = new TextField(currentCashier.getUsername());
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(200);

        // Cashier password fields (current password is not shown for security reasons)
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        Button updateButton = new Button("Update Cashier Information");
        updateButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        updateButton.setPrefWidth(200); // Set button width equal to the input fields
        updateButton.setOnAction(event -> {
            String username = usernameField.getText();
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Validate and update the information
            if (updateCashierInfo(username, currentPassword, newPassword, confirmPassword)) {
                messageLabel.setText("Cashier information updated successfully.");
            } else {
                messageLabel.setText("Failed to update cashier information. Check your credentials.");
            }
        });

        formLayout.add(usernameField, 0, 0);
        formLayout.add(currentPasswordField, 0, 1);
        formLayout.add(newPasswordField, 0, 2);
        formLayout.add(confirmPasswordField, 0, 3);
        formLayout.add(updateButton, 0, 4);

        card.setCenter(formLayout);
        return card;
    }

    private BorderPane createStoreInfoSettingCard() {
        BorderPane card = new BorderPane();
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #007bff; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        // Title section with left-aligned blue bar
        HBox titleBox = new HBox();
        titleBox.setStyle("-fx-background-color: #ADD8E6; -fx-padding: 5;");
        Label title = new Label("Change Store Information");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fff;");
        titleBox.getChildren().add(title);
        card.setTop(titleBox);

        // Message label for feedback
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
        card.setBottom(messageLabel);

        // Get current store information from the database
        SettingsService storeInfoDataFetcher = new SettingsService();
        StoreInfo currentStoreInfo = storeInfoDataFetcher.getStoreInfo();

        GridPane formLayout = new GridPane();
        formLayout.setVgap(10);
        formLayout.setHgap(10);
        formLayout.setAlignment(Pos.CENTER);

        // Store Info Fields (pre-filled with existing data)
        TextField storeNameField = new TextField(currentStoreInfo.getStoreName());
        storeNameField.setPromptText("Store Name");
        storeNameField.setPrefWidth(200); // Set width to match other input fields

        TextField storeEmailField = new TextField(currentStoreInfo.getStoreEmail());
        storeEmailField.setPromptText("Store Email");
        storeEmailField.setPrefWidth(200);

        TextField phoneField = new TextField(currentStoreInfo.getPhone());
        phoneField.setPromptText("Phone");
        phoneField.setPrefWidth(200);

        TextArea storeAddressField = new TextArea(currentStoreInfo.getStoreAddress());
        storeAddressField.setPromptText("Store Address");
        storeAddressField.setPrefWidth(200);
        storeAddressField.setWrapText(true); // Allow text to wrap

        TextField taxCodeField = new TextField(currentStoreInfo.getTaxCode());
        taxCodeField.setPromptText("Tax Code");
        taxCodeField.setPrefWidth(200);

        // Update button with action to update store info
        Button updateButton = new Button("Update Store Information");
        updateButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        updateButton.setPrefWidth(200); // Make the button the same width as the input fields
        updateButton.setOnAction(event -> {
            String storeName = storeNameField.getText();
            String storeEmail = storeEmailField.getText();
            String phone = phoneField.getText();
            String storeAddress = storeAddressField.getText();
            String taxCode = taxCodeField.getText();

            // Validate and update the information
            if (updateStoreInfo(storeName, storeEmail, phone, storeAddress, taxCode)) {
                messageLabel.setText("Store information updated successfully.");
            } else {
                messageLabel.setText("Failed to update store information.");
            }
        });

        // Adding fields to the form
        formLayout.add(storeNameField, 0, 0);
        formLayout.add(storeEmailField, 0, 1);
        formLayout.add(phoneField, 0, 2);
        formLayout.add(storeAddressField, 0, 3);
        formLayout.add(taxCodeField, 0, 4);
        formLayout.add(updateButton, 0, 5);

        card.setCenter(formLayout);
        return card;
    }

    private boolean updateAdminInfo(String username, String email, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("New password and confirmation do not match.");
            return false;
        }

        SettingsService settingsService = new SettingsService();
        int adminId = 1; // Assuming adminId is 1 for the logged-in admin
        boolean isUpdated = settingsService.updateAdminInfo(adminId, username, email, currentPassword, newPassword);

        return isUpdated;
    }
    private boolean updateCashierInfo(String username, String currentPassword, String newPassword, String confirmPassword) {
        // Check if new password matches confirm password
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("New password and confirmation do not match.");
            return false;
        }

        // Call the SettingsService to update cashier information
        SettingsService settingsService = new SettingsService();
        int cashierId = 1; // Assuming cashierId is 1 for the logged-in cashier
        boolean isUpdated = settingsService.updateCashierInfo(cashierId, username, currentPassword, newPassword);

        if (isUpdated) {
            messageLabel.setText("Cashier information updated successfully.");
        } else {
            messageLabel.setText("Failed to update cashier information. Check your credentials.");
        }

        return isUpdated;
    }
    private boolean updateStoreInfo(String storeName, String storeEmail, String phone, String storeAddress, String taxCode) {
        // Call the SettingsService to update store information
        SettingsService settingsService = new SettingsService();
        int storeId = 1; // Assuming storeId is 1 for the logged-in store
        boolean isUpdated = settingsService.updateStoreInfo(storeId, storeName, storeEmail, phone, storeAddress, taxCode);

        if (isUpdated) {
            messageLabel.setText("Store information updated successfully.");
        } else {
            messageLabel.setText("Failed to update store information.");
        }

        return isUpdated;
    }

}
