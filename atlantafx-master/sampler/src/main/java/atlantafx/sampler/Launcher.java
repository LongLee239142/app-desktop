/* SPDX-License-Identifier: MIT */

package atlantafx.sampler;

import static java.nio.charset.StandardCharsets.UTF_8;
import fr.brouillard.oss.cssfx.CSSFX;
import fr.brouillard.oss.cssfx.api.URIToPathConverter;
import fr.brouillard.oss.cssfx.impl.log.CSSFXLogger;
import fr.brouillard.oss.cssfx.impl.log.CSSFXLogger.LogLevel;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Properties;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Launcher extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        loadApplicationProperties();

        // Show the login page first
        var loginPage = new LoginPage(stage);
        var loginScene = new Scene(loginPage, 900, 500);
        loginScene.getStylesheets().addAll(Resources.resolve("assets/styles/loginpage.css"));


        stage.setScene(loginScene);
        stage.setTitle("BlueOPS");
        stage.setResizable(true);
        stage.show();




    }



    private void loadIcons(Stage stage) {
        int iconSize = 16;
        while (iconSize <= 1024) {
            // we could use the square icons for Windows here
            stage.getIcons().add(new Image(Resources.getResourceAsStream("assets/icon-rounded-" + iconSize + ".png")));
            iconSize *= 2;
        }
    }

    private void loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(Resources.getResourceAsStream("application.properties"),
            UTF_8)) {
            properties.load(in);
            properties.forEach((key, value) -> System.setProperty(
                String.valueOf(key),
                String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("CatchAndPrintStackTrace")
    private void startCssFX(Scene scene) {
        URIToPathConverter fileUrlConverter = uri -> {
            try {
                if (uri != null && uri.startsWith("file:")) {
                    return Paths.get(URI.create(uri));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };

        CSSFX.addConverter(fileUrlConverter).start();
        CSSFXLogger.setLoggerFactory(loggerName -> (level, message, args) -> {
            if (level.ordinal() <= LogLevel.INFO.ordinal()) {
                System.out.println("[" + level + "] CSSFX: " + String.format(message, args));
            }
        });
        CSSFX.start(scene);
    }




}
