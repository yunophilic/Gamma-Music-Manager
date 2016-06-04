package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.ui.MainUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {
    public static void main(String[] args) {

        /*TextUI userInterface = new TextUI();

        PersistentStorage persistentStorage = new PersistentStorage();
        if (persistentStorage.isThereSavedState()) {
            // Load state from file. Use it to initialize stuff.
        }
        String pathToDir = userInterface.getUserInputForDirectory();

        SongManager songManager = new SongManager(pathToDir);

        System.out.println("The path in song manager is " + songManager.getLibraryRootDirPath());*/
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Music Management");

        //for now!
        String myLibPath = System.getProperty("user.dir") +
                File.separator + "library-sample" + File.separator + "my library";
        String externLibPath = System.getProperty("user.dir") +
                File.separator + "library-sample" + File.separator + "external library";

        SongManager songManager = new SongManager();
        //songManager.setM_externalLibrary(externLibPath);

        if (songManager.addLibrary(myLibPath)){
            System.out.println("New library added");
        } else {
            System.out.println("Duplicate library");
        }
        if (songManager.addLibrary(externLibPath)){
            System.out.println("New library added");
        } else {
            System.out.println("Duplicate library");
        }

        MainUI root = new MainUI(songManager);

        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
    }
}
