/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.admin.layout;


import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.Resources;
import atlantafx.sampler.base.util.Lazy;
import atlantafx.sampler.admin.layout.MainModel;
import atlantafx.sampler.admin.layout.NavTree;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import static atlantafx.base.theme.Styles.TEXT_MUTED;
import static atlantafx.base.theme.Styles.TEXT_SMALL;

final class Sidebar extends VBox {

    private final NavTree navTree;
    private final Lazy<SearchDialog> searchDialog;
    private final Lazy<LogoutDialog> logoutDialog;


    public Sidebar(MainModel model) {
        super();

        this.navTree = new NavTree(model);

        createView();

        searchDialog = new Lazy<>(() -> {
            var dialog = new SearchDialog(model);
            dialog.setClearOnClose(true);
            return dialog;
        });

        logoutDialog = new Lazy<>(() -> {
            var dialog = new LogoutDialog();
            dialog.setClearOnClose(true);
            return dialog;
        });


        model.selectedPageProperty().addListener((obs, old, val) -> {
            if (val != null) {
                navTree.getSelectionModel().select(model.getTreeItemForPage(val));
            }
        });




    }

    private void createView() {
        var header = new Header();

        VBox.setVgrow(navTree, Priority.ALWAYS);

        setId("sidebar");
        getChildren().addAll(header, navTree);
    }

    void begForFocus() {
        navTree.requestFocus();
    }
    private void openSearchDialog() {
        var dialog = searchDialog.get();
        dialog.show(getScene());
        Platform.runLater(dialog::begForFocus);
    }

    private void openLogoutDialog() {
        var dialog = logoutDialog.get();
        dialog.show(getScene());
        Platform.runLater(dialog::requestFocus);
    }





    ///////////////////////////////////////////////////////////////////////////

    private class Header extends VBox {

        public Header() {
            super();

            getStyleClass().add("header");
            getChildren().setAll(
                    createLogo(),
                    createSearchButton()

            );
        }

        private HBox createLogo() {
            var image = new ImageView(
                    new Image(Resources.getResource("assets/app-icon.png").toString())
            );
            image.setFitWidth(32);
            image.setFitHeight(32);

            var imageBorder = new Insets(1);
            var imageBox = new StackPane(image);
            imageBox.getStyleClass().add("image");
            imageBox.setPadding(imageBorder);
            imageBox.setPrefSize(
                    image.getFitWidth() + imageBorder.getRight() * 2,
                    image.getFitWidth() + imageBorder.getTop() * 2
            );
            imageBox.setMaxSize(
                    image.getFitHeight() + imageBorder.getTop() * 2,
                    image.getFitHeight() + imageBorder.getRight() * 2
            );

            var titleLbl = new Label("BlueOPS");

            var logoutBtn = new Button();
            logoutBtn.getStyleClass().add("palette");
            logoutBtn.setGraphic(new FontIcon(Material2MZ.POWER_SETTINGS_NEW));
            logoutBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            logoutBtn.getStyleClass().addAll(Styles.BUTTON_CIRCLE, Styles.FLAT);
            logoutBtn.setAlignment(Pos.CENTER_RIGHT);
            logoutBtn.setOnAction(e -> openLogoutDialog());


            var root = new HBox(10, imageBox, titleLbl, new Spacer(), logoutBtn);
            root.getStyleClass().add("logo");
            root.setAlignment(Pos.CENTER_LEFT);

            return root;
        }

        private Button createSearchButton() {
            var titleLbl = new Label("Search", new FontIcon(Material2MZ.SEARCH));

            var hintLbl = new Label("Press /");
            hintLbl.getStyleClass().addAll("hint", TEXT_MUTED, TEXT_SMALL);

            var searchBox = new HBox(titleLbl, new Spacer(), hintLbl);
            searchBox.getStyleClass().add("content");
            searchBox.setAlignment(Pos.CENTER_LEFT);

            var root = new Button();
            root.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            root.getStyleClass().addAll("search-button");
            root.setGraphic(searchBox);
            root.setOnAction(e -> openSearchDialog());
            root.setMaxWidth(Double.MAX_VALUE);

            return root;
        }
    }
}
