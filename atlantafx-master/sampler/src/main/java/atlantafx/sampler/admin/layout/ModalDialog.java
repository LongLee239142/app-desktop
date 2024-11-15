package atlantafx.sampler.admin.layout;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.admin.layout.AdminApp;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class ModalDialog extends ModalBox {

    protected final Card content = new Card();
    protected final Tile header = new Tile();
    private boolean isShown = false;

    public ModalDialog() {
        super("#" + AdminApp.MAIN_MODAL_ID);
        createView();
    }

    // Custom method to show the dialog and wait until it closes
    public void showModalAndWait(Scene scene) {
        var modalPane = (ModalPane) scene.lookup("#" + AdminApp.MAIN_MODAL_ID);
        if (modalPane != null) {
            modalPane.show(this);
            isShown = true;

            // Wait until the dialog is closed
            modalPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    isShown = false;
                    onDialogClosed();
                }
            });
        }
    }

    // Override this method in child classes to handle actions after closing
    protected void onDialogClosed() {
        // Custom actions when the dialog closes, if any.
    }

    public void show(Scene scene) {
        var modalPane = (ModalPane) scene.lookup("#" + AdminApp.MAIN_MODAL_ID);
        modalPane.show(this);
    }

    protected void createView() {
        content.setHeader(header);
        content.getStyleClass().add(Tweaks.EDGE_TO_EDGE);

        setMinWidth(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);

        AnchorPane.setTopAnchor(content, 0d);
        AnchorPane.setRightAnchor(content, 0d);
        AnchorPane.setBottomAnchor(content, 0d);
        AnchorPane.setLeftAnchor(content, 0d);

        addContent(content);
        getStyleClass().add("modal-dialog");
    }

    protected HBox createDefaultFooter() {
        var closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("form-action");
        closeBtn.setCancelButton(true);
        closeBtn.setOnAction(e -> close());

        var footer = new HBox(10, new Spacer(), closeBtn);
        footer.getStyleClass().add("footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        VBox.setVgrow(footer, Priority.NEVER);

        return footer;
    }
}
