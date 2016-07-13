package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

/**
 * Class to hold some helper function for building UIs.
 */
public class UserInterfaceUtils {

    // Constants
    public static final String SELECTED_BACKGROUND_COLOUR = "-fx-background-color: #BFDCF5;";

    /**
     * Function to create a UI indication when mousing over something.
     *
     * @param element  The element to apply UI effect on.
     */
    public static void createMouseOverUIChange(final Node element, String defaultStyle) {
        element.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                element.setStyle(SELECTED_BACKGROUND_COLOUR);
            }
        });
        element.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                element.setStyle(defaultStyle);
            }
        });
    }

    /**
     * Helper function to create a button that displays the image passed in.
     *
     * @param pathToIcon The path to the image to use.
     * @return The button with the image being used.
     */
    public static Button createIconButton(String pathToIcon) {
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent");
        button.setGraphic(createImageViewForImage(pathToIcon));
        return button;
    }

    /**
     * Helper function to convert a path to a image to a actual image you can use.
     *
     * @param imagePath The path to a image.
     * @return A ImageView that contains the image that is passed in.
     */
    public static ImageView createImageViewForImage(String imagePath) {
        // Replace path separator to correct OS.
        imagePath = imagePath.replace("\\", File.separator);
        imagePath = imagePath.replace("/", File.separator);

        // Idea for background image from http://stackoverflow.com/questions/29984228/javafx-button-background-image
        return new ImageView(imagePath);
    }

    /**
     * Helper function to convert the duration obejct to a human readable format. The format is like the following MM:SS
     *
     * @param duration The duration to convert.
     * @return A human readable string of the duration.
     */
    public static String convertDurationToTimeString(Duration duration) {
        String timeString = "";

        double seconds = duration.toSeconds();
        int minutes = 0;
        while ((seconds - MusicPlayerConstants.SECONDS_IN_MINUTE) >= 0) {
            minutes++;
            seconds -= MusicPlayerConstants.SECONDS_IN_MINUTE;
        }
        timeString = minutes + ":";

        long leftOverSeconds = (int) seconds;
        if (leftOverSeconds < 10) {
            // Add on so it looks like 0:05 rather than 0:5
            timeString += "0";
        }
        timeString += leftOverSeconds;
        return timeString;
    }

    /**
     * Delete a song that has been selected by the user.
     *
     * @param selectedSong song selected
     */
    public static void deleteSong(File selectedSong, SongManager model, MusicPlayerManager musicPlayerManager) {
        //confirmation dialog
        if (selectedSong.isDirectory()) {
            if (!PromptUI.recycleLibrary(selectedSong)) {
                return;
            }
        } else {
            if (!PromptUI.recycleSong(selectedSong)) {
                return;
            }
        }
        //try to actually delete (retry if FileSystemException happens)
        final int NUM_TRIES = 2;
        for (int i = 0; i < NUM_TRIES; i++) {
            try {
                model.deleteFile(selectedSong);
                break;
            } catch (IOException ex) {
                musicPlayerManager.stopSong();
                musicPlayerManager.removeSongFromHistory(musicPlayerManager.getCurrentSongPlaying());

                if (musicPlayerManager.isThereANextSong()) {
                    musicPlayerManager.playNextSong();
                } else if (!musicPlayerManager.getHistory().isEmpty()) {
                    musicPlayerManager.playPreviousSong();
                } else {
                    musicPlayerManager.unloadSong();
                }

                if (i == 1) { //if this exception still thrown after retry (for debugging)
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                PromptUI.customPromptError("Error", null, "Exception: \n" + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
        musicPlayerManager.notifyNewSongObservers();
        musicPlayerManager.notifyQueingObserver();
        musicPlayerManager.notifyChangeStateObservers();
    }

    public static void applyBlackBoarder(Node element) {
        final String cssDefault = "-fx-border-color: black;\n";
        element.setStyle(cssDefault);
    }
}
