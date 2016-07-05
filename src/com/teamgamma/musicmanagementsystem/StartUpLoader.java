package com.teamgamma.musicmanagementsystem;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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

        final int CLOSING_WINDOW_WIDTH = 400;
        final int CLOSING_WINDOW_HEIGHT = 80;

        BorderPane loadingPane = new BorderPane();
        loadingPane.setCenter(new ProgressBar());

        Label text = new Label("Loading, please wait...");
        text.setFont(new Font(16));
        text.setPadding(new Insets(10, CLOSING_WINDOW_WIDTH/4, 10, CLOSING_WINDOW_WIDTH/4));
        loadingPane.setTop(text);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.getIcons().add(
                getLogoIcon()
        );

        primaryStage.setScene(new Scene(loadingPane, CLOSING_WINDOW_WIDTH, CLOSING_WINDOW_HEIGHT));
        primaryStage.show();
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