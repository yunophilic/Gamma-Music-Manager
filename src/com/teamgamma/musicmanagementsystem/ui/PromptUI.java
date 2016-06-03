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
import java.util.Optional;
import javafx.stage.StageStyle;


public class PromptUI {

    // ---------------------- Initialization

    /**
     * Initial welcome prompt for first time startup
     *
     * @return set directory for master panel
     */
    public static String initialWelcome() {

        // TEMPORARY : Text box will be replaced with "Browse" button, so user weill select directory
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Welcome!");
        dialog.setHeaderText(null);
        dialog.setContentText("Welcome to the Music Management System. Please enter your home directory:");

        Optional<String> result = dialog.showAndWait();

        return result.get();
    }

    // ----------------------  Error Prompts

    /**
     * File not found in program
     *
     * @param doesNotExist
     * @return true if user wishes to delete file reference
     */
    public static boolean fileNotFoundMove(File doesNotExist) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("File Not Found");
        alert.setHeaderText("An error occured while moving \"" + doesNotExist.getName() + "\":");
        alert.setContentText("The file " + doesNotExist.getAbsolutePath() + " is not found. Delete " +
                "its reference?");

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == deleteReference){
            return true;
        }
        return false;
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
     * @return 1 if user wishes to replace file
     * @return 2 if user renames file
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

        if (result.get() == replace){
            return 1;
        } else if (result.get() == rename) {
            fileRenameDuplicate(duplicate);
            return 2;
        } else {
            return 0;
        }
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

        if (result.get() == deleteReference){
            folder.delete();
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

        if (!Character.isLetter(lastChar.charAt(0))){
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