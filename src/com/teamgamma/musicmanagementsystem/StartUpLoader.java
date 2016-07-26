package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Start up loader class
 * Modified code from https://blog.codecentric.de/en/2015/09/javafx-how-to-easily-implement-application-preloader-2/
 */
public class StartUpLoader extends Preloader {
    private static final String APP_TITLE = "Gamma Music Manager";
    private Stage m_preloaderStage;
    private Label m_loadingLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        m_loadingLabel = new Label();
        this.m_preloaderStage = primaryStage;
        registerObservers();

        final int SPLASH_WINDOW_WIDTH = 500;
        final int SPLASH_WINDOW_HEIGHT = 300;
        final int LOADING_INDICATOR_SIZE = 60;
        final int TEXT_FONT_SIZE = 10;
        final int VBOX_SPACING = 10;
        final int VBOX_HEIGHT_ALIGNMENT = 65;
        final String SPLASH_BACKGROUND_IMAGE = "res\\splash.png";

        BorderPane loadingPane = new BorderPane();

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(LOADING_INDICATOR_SIZE, LOADING_INDICATOR_SIZE);

        m_loadingLabel.setFont(new Font(TEXT_FONT_SIZE));
        m_loadingLabel.setTextFill(Color.CORNFLOWERBLUE);
        VBox box = new VBox(VBOX_SPACING);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(VBOX_HEIGHT_ALIGNMENT, 0, 0, 0));
        box.getChildren().addAll(progress, m_loadingLabel);
        loadingPane.setCenter(box);

        Image backgroundImage = new Image(SPLASH_BACKGROUND_IMAGE);
        loadingPane.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        primaryStage.setTitle(APP_TITLE);
        primaryStage.getIcons().add(
            getLogoIcon()
        );

        primaryStage.setScene(new Scene(loadingPane, SPLASH_WINDOW_WIDTH, SPLASH_WINDOW_HEIGHT));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    /**
     * Register observers to get notification on a loading update
     */
    private void registerObservers() {
        FileTreeUtils.addObserver(() -> Platform.runLater(() ->
                m_loadingLabel.setText(FileTreeUtils.getLoadingFilePath())));
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == Type.BEFORE_START) {
            m_preloaderStage.hide();
        }
    }

    /**
     * Obtain the Gamma application logo
     *
     * @return logo image
     */
    private Image getLogoIcon() {
        return new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "gamma-logo.png"));
    }
}