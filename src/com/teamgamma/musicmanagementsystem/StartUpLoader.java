package com.teamgamma.musicmanagementsystem;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Start up loader class
 * Modified code from https://blog.codecentric.de/en/2015/09/javafx-how-to-easily-implement-application-preloader-2/
 */
public class StartUpLoader extends Preloader {
    private Stage preloaderStage;
    private static final String APP_TITLE = "Gamma Music Manager";

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;

        splashScreenDisplay(primaryStage);
        primaryStage.show();
    }

    public static void splashScreenDisplay(Stage primaryStage) {
        final int CLOSING_WINDOW_WIDTH = 500;
        final int CLOSING_WINDOW_HEIGHT = 281;
        final int LOADING_SIZE = 70;
        final String SPLASH_BACKGROUND_IMAGE = "res\\splash.png";

        BorderPane loadingPane = new BorderPane();

        ProgressIndicator progress = new ProgressIndicator();
        loadingPane.setBottom(progress);
        loadingPane.setAlignment(progress, Pos.BASELINE_CENTER);
        progress.setPrefSize(LOADING_SIZE, LOADING_SIZE);

        Image backgroundImage = new Image(SPLASH_BACKGROUND_IMAGE);
        loadingPane.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        primaryStage.setTitle(APP_TITLE);

        primaryStage.setScene(new Scene(loadingPane, CLOSING_WINDOW_WIDTH, CLOSING_WINDOW_HEIGHT));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == Type.BEFORE_START) {
            preloaderStage.hide();
        }
    }

    private Image getLogoIcon() {
        return new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "gamma-logo.png"));
    }
}