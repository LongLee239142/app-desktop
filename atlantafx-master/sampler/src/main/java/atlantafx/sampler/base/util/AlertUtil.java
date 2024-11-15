package atlantafx.sampler.base.util;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class AlertUtil {

  public static void showErrorAlert(String s) {
    Stage alertStage = new Stage();
    alertStage.initModality(Modality.APPLICATION_MODAL);

    // Load the cartoon image (replace with your local image path or resource)
    ImageView imageView = new ImageView(new Image(
        Objects.requireNonNull(AlertUtil.class.getResource("/images/ErrorLogin.png")).toExternalForm()));
    imageView.setFitHeight(100);
    imageView.setFitWidth(100);

    // Create the label for the error message
    Label messageLabel = new Label(s);
    messageLabel.getStyleClass().add("error-message");

    // Create layout and add image and message
    VBox vbox = new VBox(10, imageView, messageLabel);
    vbox.setAlignment(Pos.CENTER);
    vbox.getStyleClass().add("custom-alert");

    Scene alertScene = new Scene(vbox, 450, 200);
    // Apply the external CSS file
    alertScene.getStylesheets().add(
        Objects.requireNonNull(AlertUtil.class.getResource("/css/errorLoginAlert.css")).toExternalForm());

    alertStage.setScene(alertScene);
    alertStage.setTitle("Login Error");
    alertStage.showAndWait();
  }
}
