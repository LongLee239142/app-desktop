package atlantafx.sampler.staff.layout;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.LoginPage;
import atlantafx.sampler.cashier.layout.ModalDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

final class LogoutDialog extends ModalDialog {

    public LogoutDialog() {
        super();

        setId("logout-dialog");
        header.setTitle("Logout Confirmation");
        content.setBody(createContent());
        content.setFooter(createFooter());
        content.setPrefSize(400, 200);

        // Add a visible border to the content for better visual clarity
        content.setBorder(new Border(new BorderStroke(
                Color.BLACK, // Border color
                BorderStrokeStyle.SOLID, // Border style
                new CornerRadii(8), // Rounded corners
                new BorderWidths(2) // Border thickness
        )));
    }

    private VBox createContent() {
        Label messageLabel = new Label("Are you sure you want to log out?");
        messageLabel.getStyleClass().add(Styles.TITLE_4);
        messageLabel.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, messageLabel); // Adjusted spacing
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20, 20, 10, 20)); // Padding around the content
        return content;
    }

    private HBox createFooter() {
        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction(event -> {
            navigateToLoginScene();
            close();
        });

        Button cancelButton = new Button("No");
        cancelButton.setOnAction(event -> close());

        // Create an HBox to align buttons horizontally and set spacing/padding
        HBox footer = new HBox(10, confirmButton, cancelButton);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10)); // Add padding around the footer
        return footer;
    }

    private void navigateToLoginScene() {
        Stage stage = (Stage) getScene().getWindow();
        LoginPage loginPage = new LoginPage(stage);
        Scene scene = new Scene(loginPage, 900, 500);
        stage.setScene(scene);
    }
}
