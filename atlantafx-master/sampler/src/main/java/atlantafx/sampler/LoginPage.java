package atlantafx.sampler;


import atlantafx.sampler.base.service.AuthService;
import atlantafx.sampler.base.service.ForgotPasswordService;
import atlantafx.sampler.base.service.UserSession;
import atlantafx.sampler.cashier.layout.CashierApp;
import atlantafx.sampler.staff.layout.StaffApp;
import atlantafx.sampler.admin.layout.AdminApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.util.Objects;

public class LoginPage extends HBox {

    private final Stage stage;

    public LoginPage(Stage stage) {
        this.stage = stage;
        setupUI();
    }

    private void setupUI() {
        // Left Pane with Image
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(Resources.getResourceAsStream("images/login.jpg"))));
        imageView.setFitWidth(400);
        imageView.setFitHeight(500);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        StackPane leftPane = new StackPane(imageView);
        leftPane.setPrefSize(400, 500);
        leftPane.setStyle("-fx-background-color: #FFFFFF;");

        // Right Pane with Login Form
        GridPane rightPane = new GridPane();
        rightPane.setPadding(new Insets(20));
        rightPane.setVgap(10);
        rightPane.setHgap(10);
        rightPane.getStyleClass().add("login-form");
        rightPane.setAlignment(Pos.CENTER);

        // Set Column Constraints for Right Pane
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(40);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(60);
        rightPane.getColumnConstraints().addAll(column1, column2);

        Label welcomeLabel = new Label("Welcome!");
        welcomeLabel.setFont(new Font(36));
        welcomeLabel.setTextFill(javafx.scene.paint.Color.BLUE);
        welcomeLabel.setPadding(new Insets(0, 0, 15, 0));

        // Input Labels and Fields
        Label userLabel = new Label("Username");
        userLabel.setFont(new Font(16));
        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        userField.setPrefWidth(200);

        Label passLabel = new Label("Password");
        passLabel.setFont(new Font(16));
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.setPrefWidth(200);

        Label roleLabel = new Label("Role");
        roleLabel.setFont(new Font(16));
        ComboBox<String> roleSelector = new ComboBox<>();
        roleSelector.setValue("Select role");
        roleSelector.getItems().addAll("Admin", "Cashier", "Staff");
        roleSelector.setPrefWidth(200);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnAction(event -> {
            String username = userField.getText();
            String password = passField.getText();
            String role = roleSelector.getValue();

            // Authentication logic
            AuthService authService = new AuthService();
            boolean isAuthenticated = authService.authenticate(username, password, role);
            if (isAuthenticated) {
                if ("Admin".equals(role)) {
                    onLoginSuccess(new AdminApp());
                } else if ("Staff".equals(role)) {
                    UserSession.getInstance().setStaffId(username);
                    onLoginSuccess(new StaffApp());
                } else if ("Cashier".equals(role)) {
                    onLoginSuccess(new CashierApp());
                }
            } else {
                System.out.println("Login failed!");
            }
        });

        // Forgot Password Link
        Hyperlink forgotPasswordLink = new Hyperlink("Forgot Password?");

        // Show the "Forgot Password" link only if the selected role is not "Cashier"
        forgotPasswordLink.setVisible(true);
        roleSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Show the link if the role is not "Cashier"
            forgotPasswordLink.setVisible(!"Cashier".equals(newValue)); // Hide the link if the role is "Cashier"
        });

        forgotPasswordLink.setOnAction(event -> {
            String username = userField.getText();
            String role = roleSelector.getValue();

            if (!username.isEmpty() && role != null) {
                ForgotPasswordService.handleForgotPassword(role, username);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Reset");
                alert.setHeaderText(null);
                alert.setContentText("A password reset link has been sent to your email.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing Information");
                alert.setHeaderText(null);
                alert.setContentText("Please enter your username and select a role.");
                alert.showAndWait();
            }
        });

        // Add controls to the right pane
        rightPane.add(welcomeLabel, 0, 0, 2, 1); // Add welcome label spanning two columns
        rightPane.add(userLabel, 0, 1);
        rightPane.add(userField, 1, 1);
        rightPane.add(passLabel, 0, 2);
        rightPane.add(passField, 1, 2);
        rightPane.add(roleLabel, 0, 3);
        rightPane.add(roleSelector, 1, 3);
        rightPane.add(loginButton, 1, 4);
        rightPane.add(forgotPasswordLink, 1, 5); // Place forgot password link below the login button

        // Main layout with image on the left and login form on the right
        this.getChildren().addAll(leftPane, rightPane);
        this.setPrefSize(900, 500);
        this.setStyle("-fx-background-color: #FFFFFF;");
    }

    private void onLoginSuccess(Pane app) {
        System.out.println("Login successful!");
        var scene = new Scene(app, AdminApp.MIN_WIDTH + 80, 768);
        stage.setScene(scene);
    }
}
