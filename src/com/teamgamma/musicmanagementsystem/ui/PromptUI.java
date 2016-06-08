package com.teamgamma.musicmanagementsystem.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * Various prompts for UI
 */
public class PromptUI {

    // ---------------------- Custom Prompts

    /**
     * Custom information prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title
     * @param headerText  (optional)
     * @param bodyMessage
     */
    public static void customPromptInformation(String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);

        alert.showAndWait();
    }

    /**
     * Custom confirmation prompt for use. Contains "OK" and "Cancel" buttons
     *
     * @param title
     * @param headerText  (optional)
     * @param bodyMessage
     * @return false if user clicks "Cancel"
     */
    public static boolean customPromptConfirmation(String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Custom warning prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title
     * @param headerText  (optional)
     * @param bodyMessage
     */
    public static void customPromptWarning(String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);

        alert.showAndWait();
    }

    /**
     * Custom error prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title
     * @param headerText  (optional)
     * @param bodyMessage
     */
    public static void customPromptError(String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);

        alert.showAndWait();
    }

    // ---------------------- Initialization

    /**
     * Initial welcome prompt for first time startup. Browse button allows user
     * to browse directories and choose a folder
     *
     * @return set directory for master panel
     */
    public static String initialWelcome() {
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("Welcome!");
        alert.setHeaderText(null);
        alert.setContentText("Welcome to the Music Management System. Before " +
                "beginning, please select a media library.");

        ButtonType browse = new ButtonType("Browse");
        alert.getButtonTypes().setAll(browse);
        alert.showAndWait();

        DirectoryChooser directory = new DirectoryChooser();
        File selectedFile = directory.showDialog(null);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }

        return null;
    }

    // ----------------------  Error Prompts

    /**
     * File not found in program (copy)
     *
     * @param missingFile
     * @return true if user wishes to delete file reference
     */
    public static boolean fileNotFoundCopy(File missingFile) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("File Not Found");
        alert.setHeaderText("An error occured while copying \"" + missingFile.getName() + "\":");
        alert.setContentText("The file " + missingFile.getAbsolutePath() + " is not found. Delete " +
                "its reference?");

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == deleteReference) {
            return true;
        }
        return false;
    }

    /**
     * File not found in program (move)
     *
     * @param missingFile
     * @return true if user wishes to delete file reference
     */
    public static boolean fileNotFoundMove(File missingFile) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("File Not Found");
        alert.setHeaderText("An error occured while moving \"" + missingFile.getName() + "\":");
        alert.setContentText("The file " + missingFile.getAbsolutePath() + " is not found. Delete " +
                "its reference?");

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == deleteReference) {
            return true;
        }
        return false;
    }

    /**
     * File copied is attempting to paste into a song file as its destination (instead of a folder)
     *
     * @param copiedFile
     * @param destinationFile
     */
    public static void invalidPasteDestination(File copiedFile, File destinationFile) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Copy Error");
        alert.setHeaderText("An error occured while pasting \"" + copiedFile.getName() + "\":");
        alert.setContentText("The file cannot be pasted into the media file " +
                destinationFile.getName() + ". Please paste into a folder instead.");

        alert.showAndWait();
    }


    /**
     * File failed to rename
     *
     * @param file
     */
    public static void failedToRename(File file) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Rename Error");
        alert.setHeaderText(null);
        alert.setContentText("The file \"" + file + "\" could not be renamed.");

    }

    /**
     * Unknown crash; could be used in else statement for error checking
     */
    public static void unexpectedCrash() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Unexpected Crash");
        alert.setHeaderText(null);
        alert.setContentText("Something has caused the program to crash unexpectedly.");

        alert.showAndWait();
        System.exit(0);
    }

    // ---------------------- Information Prompts

    /**
     * File exists in directory, after copy attempt
     *
     * @param duplicate
     * @return 0 if user clicks cancel
     */
    public static int fileAlreadyExists(File duplicate) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("File Name Exists");
        alert.setHeaderText(null);
        alert.setContentText("The file " + duplicate.getAbsolutePath() + " already exists in the folder.");

        ButtonType replace = new ButtonType("Replace Existing");
        ButtonType rename = new ButtonType("Rename Current");
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(replace, rename, cancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == replace) {
            return 1;
        } else if (result.get() == rename) {
            fileRenameDuplicate(duplicate);
            return 2;
        } else {
            return 0;
        }
    }

    // ---------------------- Text Prompts

    /**
     * Prompt when user clicks Add New Library button
     *
     * @return user's library name (null if user cancels)
     */
    public static String addNewLibrary() {
        DirectoryChooser directory = new DirectoryChooser();
        File selectedFile = directory.showDialog(null);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }

        return null;
    }

    // ---------------------- Confirmation Prompts

    /**
     * Delete folder and contents
     *
     * @param folder
     */
    public static void deleteFolder(File folder) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete folder and all contents:");
        alert.setContentText("Are you sure you want do delete the folder " + folder.getName() + "?");

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == deleteReference) {
            folder.delete();
        }
    }

    /**
     * Delete song
     *
     * @param mediaFile
     */
    public static void deleteSong(File mediaFile) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want do delete " + mediaFile.getName() + "?");
        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == deleteReference) {
            mediaFile.delete();
        }
    }


    /**
     * Renames file. Keeps track of "_n" suffix of file if more duplicates found, and increments n
     * (shown as the default value for the text box)
     *
     * @param duplicate
     */
    public static void fileRenameDuplicate(File duplicate) {
        int numIndex = 2;
        String fileNameFull = duplicate.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        String lastChar = fileNameFull.substring(beforeExtension - 1, beforeExtension);

        if (!Character.isLetter(lastChar.charAt(0))) {
            numIndex = Character.getNumericValue(lastChar.charAt(0)) + 1;
            fileNameFull = fileNameFull.substring(0, beforeExtension - 2) +
                    fileNameFull.substring(beforeExtension);
            beforeExtension -= 2;
        }

        TextInputDialog dialog = new TextInputDialog(fileNameFull.substring(0,
                beforeExtension) + "_" + numIndex + fileNameFull.substring(beforeExtension));
        dialog.setTitle("Rename File");
        dialog.setHeaderText("\"" + duplicate.getName() + "\":");
        dialog.setContentText("Rename the file to:");

        Optional<String> result = dialog.showAndWait();

        Path source = Paths.get(duplicate.getAbsolutePath());
        try {
            Files.move(source, source.resolveSibling(result.get()));
        } catch (IOException e) {
            failedToRename(duplicate);
        }
    }

    /**
     * Renames file
     *
     * @return new file name
     */
    public static String fileRename(File duplicate) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename File");
        dialog.setHeaderText("\"" + duplicate.getName() + "\":");
        dialog.setContentText("Rename the file to:");

        Optional<String> result = dialog.showAndWait();

        return result.get();
    }

}