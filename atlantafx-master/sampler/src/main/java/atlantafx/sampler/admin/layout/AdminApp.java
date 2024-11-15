/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.admin.layout;

import atlantafx.base.controls.ModalPane;
import atlantafx.sampler.Resources;
import atlantafx.sampler.base.util.NodeUtils;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public final class AdminApp extends AnchorPane {

    public static final int MIN_WIDTH = 1200;
    public static final int SIDEBAR_WIDTH = 250;
    public static final String MAIN_MODAL_ID = "modal-pane";


    public AdminApp() {
        // this is the place to apply user custom CSS,
        // one level below the ':root'
        var body = new StackPane();

        body.getStylesheets().addAll(Resources.resolve("assets/styles/index.css"));
        body.getStyleClass().add("body");
        body.setStyle("-fx-font-size: 16px;");


        var modalPane = new ModalPane();
        modalPane.setId(MAIN_MODAL_ID);

        body.getChildren().setAll(modalPane, new MainLayer());
        NodeUtils.setAnchors(body, Insets.EMPTY);

        getChildren().setAll(body);
    }
}
