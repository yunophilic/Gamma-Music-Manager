package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.Playlist;
import com.teamgamma.musicmanagementsystem.model.Song;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Various prompts for UI
 */
public class PromptUI {
    private static final String WELCOME_TITLE = "Welcome!";
    private static final String RENAME_LIBRARY_TITLE = "Rename Library";
    private static final String RENAME_MEDIA_TITLE = "Rename Media File";
    private static final String CREATE_PLAYLIST_TITLE = "Create New Playlist";
    private static final String ADD_PLAYLIST_TITLE = "Add Playlist";
    private static final String EDIT_PLAYLIST_TITLE = "Edit Playlist";
    private static final String REMOVE_PLAYLIST_TITLE = "Remove Playlist";
    private static final String ADD_TO_PLAYLIST_TITLE = "Add to Playlist";
    private static final String REMOVE_FROM_PLAYLIST_TITLE = "Remove from Playlist";
    private static final String FILE_NOT_FOUND_TITLE = "File Not Found";
    private static final String COPY_ERROR_TITLE = "Copy Error";
    private static final String RENAME_ERROR_TITLE = "Rename Error";
    private static final String UNEXPECTED_CRASH_TITLE = "Unexpected Crash";
    private static final String FILE_EXISTS_TITLE = "File Name Exists";
    private static final String EDIT_METADATA_TITLE = "Edit Song Metadata";
    private static final String REMOVE_LIBRARY_TITLE = "Remove Library";
    private static final String REMOVE_MEDIA_TITLE = "Remove Media File";
    private static final String DELETE_LIBRARY_TITLE = "Delete Library";
    private static final String DELETE_MEDIA_TITLE = "Delete Media File";
    private static final String PLAYLIST_EMPTY_HEADER = "Please enter at least one character for the playlist name";
    private static final String RENAME_FILE_LABEL = "Rename the file to:";
    private static final String RENAME_FOLDER_LABEL = "Rename the folder to:";
    private static final String RENAME_LIBRARY_LABEL = "Rename the library to:";
    private static final String INVALID_MEDIA = "Invalid Media File";
    private static final String NAME_ALREADY_EXISTS = "Name Already Exists";
    private static final String NEW_PLAYLIST_LABEL = "New playlist:";
    private static final String RENAME_PLAYLIST_LABEL = "Rename playlist:";
    private static final String SELECT_PLAYLIST_LABEL = "Select a playlist:";
    private static final String WELCOME_MESSAGE = "\nWelcome to the Gamma Music Manager. Before " +
            "beginning, please select a media library.";
    private static final String UNEXPECTED_CRASH_MESSAGE = "Something has caused the program to crash unexpectedly.";
    private static final String RECYCLE_FOLDER_CONFIRM = "Are you sure you want to move this folder and its contents to the Recycle Bin?";
    private static final String RECYCLE_SONG_CONFIRM_MESSAGE = "Are you sure you want to move this song to the Recycle Bin?";
    private static final String DELETE_FOLDER_CONFIRM = "Are you sure you want to permanently delete this folder and all of its contents?";
    private static final String DELETE_SONG_CONFIRM = "Are you sure you want to permanently delete this song?";
    private static final String CORRUPTED_FILE_WARNING = "The program has detected that this file is either corrupted or an invalid MP3 file.";
    private static final String REMOVE_PLAYLIST_CONFIRMATION = "Are you sure you want to remove this playlist?";
    private static final String REMOVE_SONG_PLAYLIST_CONFIRMATION = "Are you sure you want to remove this song from the playlist?";
    private static final Image PROMPT_ICON = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
            "gamma-logo.png"));
    private static final int CONVERT_SIZE_DIVISOR = 1024;
    private static final String UNKNOWN_ARTIST = "Unknown Artist";
    private static final String UNKNOWN_ALBUM = "Unknown Album";
    private static final int CONVERT_TO_NEXT_SIZE_TYPE = 1000;

    // ---------------------- Custom Prompts

    /**
     * Custom information prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title       of prompt
     * @param headerText  (optional)
     * @param bodyMessage within prompt
     */
    public static void customPromptInformation(String title, String headerText, String bodyMessage) {
        Alert alert = makeAlertPrompt(AlertType.INFORMATION, title, headerText, bodyMessage);
        alert.showAndWait();
    }


    /**
     * Custom confirmation prompt for use. Contains "OK" and "Cancel" buttons
     *
     * @param title       of prompt
     * @param headerText  (optional)
     * @param bodyMessage within prompt
     * @return false if user clicks "Cancel"
     */
    public static boolean customPromptConfirmation(String title, String headerText, String bodyMessage) {
        Alert alert = makeAlertPrompt(AlertType.CONFIRMATION, title, headerText, bodyMessage);
        Optional<ButtonType> result = alert.showAndWait();
        return (result.isPresent() && result.get() == ButtonType.OK);
    }

    /**
     * Custom warning prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title       of prompt
     * @param headerText  (optional)
     * @param bodyMessage within prompt
     */
    public static void customPromptWarning(String title, String headerText, String bodyMessage) {
        Alert alert = makeAlertPrompt(AlertType.WARNING, title, headerText, bodyMessage);
        alert.showAndWait();
    }

    /**
     * Custom error prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title       of prompt
     * @param headerText  (optional)
     * @param bodyMessage within prompt
     */
    public static void customPromptError(String title, String headerText, String bodyMessage) {
        Alert alert = makeAlertPrompt(AlertType.ERROR, title, headerText, bodyMessage);
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
        Dialog dialog = makePlainDialog(WELCOME_TITLE, null, WELCOME_MESSAGE);
        setLogoDialog(dialog);

        ButtonType browse = new ButtonType("Browse");
        dialog.getDialogPane().getButtonTypes().addAll(browse);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == browse) {
            DirectoryChooser directory = new DirectoryChooser();
            File selectedFile = directory.showDialog(null);
            if (selectedFile != null) {
                return selectedFile.getAbsolutePath();
            }
        }
        return null;
    }


    // ----------------------  Error Prompts

    /**
     * File not found in program (copy)
     *
     * @param missingFile not found
     * @return true if user wishes to delete file reference
     */
    public static boolean fileNotFoundCopy(File missingFile) {
        final String HEADER_TEXT = "An error occurred while copying \"" + missingFile.getName() + "\"";
        final String BODY_MESSAGE = "The file " + missingFile.getAbsolutePath() + " is not found. Delete " +
                "its reference?";
        Alert alert = makeAlertPrompt(AlertType.ERROR, FILE_NOT_FOUND_TITLE, HEADER_TEXT, BODY_MESSAGE);

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        return (result.isPresent() && result.get() == deleteReference);

    }

    /**
     * File not found in program (move)
     *
     * @param missingFile not found
     * @return true if user wishes to delete file reference
     */
    public static boolean fileNotFoundMove(File missingFile) {
        final String HEADER_TEXT = "An error occurred while moving \"" + missingFile.getName() + "\"";
        final String BODY_MESSAGE = "The file " + missingFile.getAbsolutePath() + " is not found. Delete " +
                "its reference?";
        Alert alert = makeAlertPrompt(AlertType.ERROR, FILE_NOT_FOUND_TITLE, HEADER_TEXT, BODY_MESSAGE);

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        return (result.isPresent() && result.get() == deleteReference);

    }

    /**
     * File copied is attempting to paste into a song file as its destination (instead of a folder)
     *
     * @param copiedFile      copied
     * @param destinationFile paste
     */
    public static void invalidPasteDestination(File copiedFile, File destinationFile) {
        final String HEADER_TEXT = "An error occured while pasting \"" + copiedFile.getName() + "\"";
        final String BODY_MESSAGE = "The file cannot be pasted into the media file " +
                destinationFile.getName() + ". Please paste into a folder instead.";
        Alert alert = makeAlertPrompt(AlertType.ERROR, COPY_ERROR_TITLE, HEADER_TEXT, BODY_MESSAGE);
        alert.showAndWait();
    }


    /**
     * File failed to rename
     *
     * @param file renamed
     */
    private static void failedToRename(File file) {
        final String BODY_MESSAGE = "The file \"" + file + "\" could not be renamed.";
        makeAlertPrompt(AlertType.ERROR, RENAME_ERROR_TITLE, null, BODY_MESSAGE);
    }

    /**
     * Unknown crash; could be used in else statement for error checking
     */
    public static void unexpectedCrash() {
        Alert alert = makeAlertPrompt(AlertType.ERROR, UNEXPECTED_CRASH_TITLE, null, UNEXPECTED_CRASH_MESSAGE);

        alert.showAndWait();
        System.exit(0);
    }

    // ---------------------- Information Prompts

    /**
     * File exists in directory, after copy attempt
     *
     * @param duplicate file
     * @return 0 if user clicks cancel, 1 if user wishes to replace, 2 if user wishes to rename current
     */
    public static int fileAlreadyExists(File duplicate) {
        final String BODY_MESSAGE = "The file " + duplicate.getAbsolutePath() + " already exists in the folder.";
        Alert alert = makeAlertPrompt(AlertType.INFORMATION, FILE_EXISTS_TITLE, null, BODY_MESSAGE);

        ButtonType replace = new ButtonType("Replace Existing");
        ButtonType rename = new ButtonType("Rename Current");
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(replace, rename, cancel);

        Optional<ButtonType> result = alert.showAndWait();
        final int REPLACE_FILE = 1;
        final int RENAME_CURRENT = 2;
        if (result.isPresent() && result.get() == replace) {
            return REPLACE_FILE;
        } else if (result.isPresent() && result.get() == rename) {
            fileRenameDuplicate(duplicate);
            return RENAME_CURRENT;
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

    /**
     * Prompt for editing song metadata
     *
     * @param song file for editing
     */
    public static void editMetadata(Song song) {
        final String SONG_INFO_HEADER = song.getM_title() + "\n" +
                song.getM_artist() + "\n" +
                song.getM_album();
        Dialog<ButtonType> dialog = makeDialog(PROMPT_ICON, EDIT_METADATA_TITLE, "edit-metadata.png", SONG_INFO_HEADER, null);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        final int GAP_SIZE = 10;
        GridPane grid = new GridPane();
        grid.setHgap(GAP_SIZE);
        grid.setVgap(GAP_SIZE);

        final int LEFT = 100;
        final int BOTTOM = 10;
        final int TOP = 20;
        final int RIGHT = 10;
        grid.setPadding(new Insets(TOP, LEFT, BOTTOM, RIGHT));

        final int SET_WIDTH = 200;
        TextField title = new TextField();
        title.setText(song.getM_title());
        title.setPrefWidth(SET_WIDTH);
        TextField artist = new TextField();
        artist.setText(song.getM_artist());
        TextField album = new TextField();
        album.setText(song.getM_album());
        TextField genre = new TextField();
        genre.setText(song.getM_genre());

        ChoiceBox<String> rating = new ChoiceBox<>();

        // Rating of a song, from 1 (poor) to 5 (excellent)
        rating.getItems().addAll("No rating", "1", "2", "3", "4", "5");
        if (song.getM_rating() == 0) {
            rating.getSelectionModel().select("No rating");
        } else if (song.getM_rating() == 1) {
            rating.getSelectionModel().select("1");
        } else if (song.getM_rating() == 2) {
            rating.getSelectionModel().select("2");
        } else if (song.getM_rating() == 3) {
            rating.getSelectionModel().select("3");
        } else if (song.getM_rating() == 4) {
            rating.getSelectionModel().select("4");
        } else if (song.getM_rating() == 5) {
            rating.getSelectionModel().select("5");
        } else {
            throw new IllegalArgumentException("File rating is out of range!");
        }

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Artist:"), 0, 1);
        grid.add(artist, 1, 1);
        grid.add(new Label("Album:"), 0, 2);
        grid.add(album, 1, 2);
        grid.add(new Label("Genre:"), 0, 3);
        grid.add(genre, 1, 3);
        grid.add(new Label("Rating:"), 0, 4);
        grid.add(rating, 1, 4);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == okButton) {
            song.setTitle(title.getText());
            song.setArtist(artist.getText());
            song.setAlbum(album.getText());
            song.setGenre(genre.getText());

            String ratingStr = rating.getValue();
            song.setRating(ratingStr.equals("No rating") ? 0 : Integer.parseInt(ratingStr));
        }
    }

    // ---------------------- Confirmation Prompts

    /**
     * Move library and contents to recyele bin
     *
     * @param folder to recycle
     * @return true if user clicks OK
     */
    public static boolean recycleLibrary(File folder) {
        Long sizeInKB = folderSize(folder) / CONVERT_SIZE_DIVISOR;
        Long sizeInMB = sizeInKB / CONVERT_SIZE_DIVISOR;
        String fileSize = String.format("%,d", sizeInKB) + " kilobytes";
        if (CONVERT_TO_NEXT_SIZE_TYPE <= sizeInKB) {
            fileSize = String.format("%,d", sizeInMB) + " megabytes";
        }
        BasicFileAttributes fileInfo;
        try {
            fileInfo = Files.readAttributes(folder.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            fileInfo = null;
        }
        FileTime dateCreation = fileInfo.creationTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        String dateCreated = dateFormat.format(dateCreation.toMillis());

        FileTime dateModify = fileInfo.lastModifiedTime();
        String dateModified = dateFormat.format(dateModify.toMillis());

        final String HEADER_TEXT = folder.getName() + "\n\nSize: " + fileSize + "\nCreated: " + dateCreated +
                "\nLast Modified: " + dateModified;
        Dialog dialog = makePlainDialog(REMOVE_LIBRARY_TITLE, HEADER_TEXT, RECYCLE_FOLDER_CONFIRM);
        setDialogIcon(dialog, "recycle-library.png");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Optional result = dialog.showAndWait();

        return result.isPresent() && result.get() == okButton;
    }


    /**
     * Move song to recycle bin
     *
     * @param mediaFile to recycle
     * @return true if user clicks OK
     */
    public static boolean recycleSong(File mediaFile) {
        Long sizeInKB = mediaFile.length() / CONVERT_SIZE_DIVISOR;
        Long sizeInMB = sizeInKB / CONVERT_SIZE_DIVISOR;
        String fileSize = String.format("%,d", sizeInKB) + " kilobytes";
        if (CONVERT_TO_NEXT_SIZE_TYPE <= sizeInKB) {
            fileSize = String.format("%,d", sizeInMB) + " megabytes";
        }
        Song songInfo = new Song(mediaFile.getAbsolutePath());
        String songArtist = songInfo.getM_artist();
        if (songArtist.isEmpty()) {
            songArtist = UNKNOWN_ARTIST;
        }
        String songAlbum = songInfo.getM_album();
        if (songAlbum.isEmpty()) {
            songAlbum = UNKNOWN_ALBUM;
        }
        String fileNameFullNoExtension = getFileNameNoExtension(mediaFile);
        Duration lengthOfSong = new Duration(songInfo.getM_length() * MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);
        String songLength = UserInterfaceUtils.convertDurationToTimeString(lengthOfSong);

        final String HEADER_TEXT = fileNameFullNoExtension + "\n\n" + songArtist + "\n" +
                songAlbum + "\nLength: " + songLength + "\nSize: " + fileSize;
        Dialog dialog = makePlainDialog(REMOVE_MEDIA_TITLE, HEADER_TEXT, RECYCLE_SONG_CONFIRM_MESSAGE);
        setDialogIcon(dialog, "recycle-song.png");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Optional result = dialog.showAndWait();

        return result.isPresent() && result.get() == okButton;
    }

    /**
     * Delete library and contents
     *
     * @param folder to delete
     * @return true if user clicks OK
     */
    public static boolean deleteLibrary(File folder) {
        Long sizeInKB = folderSize(folder) / CONVERT_SIZE_DIVISOR;
        Long sizeInMB = sizeInKB / CONVERT_SIZE_DIVISOR;
        String fileSize = String.format("%,d", sizeInKB) + " kilobytes";
        if (CONVERT_TO_NEXT_SIZE_TYPE <= sizeInKB) {
            fileSize = String.format("%,d", sizeInMB) + " megabytes";
        }
        BasicFileAttributes fileInfo;
        try {
            fileInfo = Files.readAttributes(folder.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            fileInfo = null;
        }
        FileTime dateCreation = fileInfo.creationTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        String dateCreated = dateFormat.format(dateCreation.toMillis());

        FileTime dateModify = fileInfo.lastModifiedTime();
        String dateModified = dateFormat.format(dateModify.toMillis());

        final String HEADER_TEXT = folder.getName() + "\n\nSize: " + fileSize + "\nCreated: " + dateCreated +
                "\nLast Modified: " + dateModified;
        Dialog dialog = makePlainDialog(DELETE_LIBRARY_TITLE, HEADER_TEXT, DELETE_FOLDER_CONFIRM);
        setDialogIcon(dialog, "delete-library.png");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Optional result = dialog.showAndWait();

        return result.isPresent() && result.get() == okButton;
    }

    /**
     * Delete song
     *
     * @param mediaFile to delete
     * @return true if user clicks OK
     */
    public static boolean deleteSong(File mediaFile) {
        Long sizeInKB = mediaFile.length() / CONVERT_SIZE_DIVISOR;
        Long sizeInMB = sizeInKB / CONVERT_SIZE_DIVISOR;
        String fileSize = String.format("%,d", sizeInKB) + " kilobytes";
        if (CONVERT_TO_NEXT_SIZE_TYPE <= sizeInKB) {
            fileSize = String.format("%,d", sizeInMB) + " megabytes";
        }
        Song songInfo = new Song(mediaFile.getAbsolutePath());
        String songArtist = songInfo.getM_artist();
        if (songArtist.isEmpty()) {
            songArtist = UNKNOWN_ARTIST;
        }
        String songAlbum = songInfo.getM_album();
        if (songAlbum.isEmpty()) {
            songAlbum = UNKNOWN_ALBUM;
        }
        String fileNameFullNoExtension = getFileNameNoExtension(mediaFile);

        Duration lengthOfSong = new Duration(songInfo.getM_length() * MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);
        String songLength = UserInterfaceUtils.convertDurationToTimeString(lengthOfSong);

        final String HEADER_TEXT = fileNameFullNoExtension + "\n\n" + songArtist + "\n" +
                songAlbum + "\nLength: " + songLength + "\nSize: " + fileSize;
        Dialog dialog = makePlainDialog(DELETE_MEDIA_TITLE, HEADER_TEXT, DELETE_SONG_CONFIRM);
        setDialogIcon(dialog, "delete-song.png");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Optional result = dialog.showAndWait();

        return result.isPresent() && result.get() == okButton;
    }


    /**
     * Corrupted or invalid .mp3 file. Leaves user no choice but to delete the file
     *
     * @param corruptedFile detected in the system
     * @return true if file has been deleted
     */
    public static boolean invalidMediaFile(File corruptedFile) {
        final String HEADER_TEXT = "\"" + corruptedFile.getName() + "\"";
        Dialog dialog = makeDialog(PROMPT_ICON, INVALID_MEDIA, "missing-song.png", HEADER_TEXT, CORRUPTED_FILE_WARNING);
        ButtonType okButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton);

        dialog.showAndWait();

        return corruptedFile.delete();
    }

    /**
     * Renames folder. Keeps track of "(n)" suffix of folder if more duplicates found, and increments n
     * (shown as the default value for the text box)
     *
     * @param duplicate folder
     * @return the path of the renamed folder, null if user cancels
     */
    private static Path folderRenameDuplicate(File duplicate) {
        String folderName = duplicate.getName();
        File duplicateWithIndex = incrementDuplicateFolderIndex(duplicate, folderName);

        TextInputDialog dialog = new TextInputDialog(duplicateWithIndex.getName());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(NAME_ALREADY_EXISTS);
        setDialogIcon(dialog, "rename-folder-exists.png");
        dialog.setHeaderText("The folder name \"" + duplicate.getName() + "\" already exists in the directory!");
        dialog.setContentText(RENAME_FOLDER_LABEL);

        Optional<String> result = dialog.showAndWait();

        Path source = rename(duplicate, "", result);
        if (source != null) {
            return source;
        }
        return null;
    }

    /**
     * Renames folder. Keeps track of "(n)" suffix of file if more duplicates found, and increments n
     * (shown as the default value for the text box)
     *
     * @param duplicate file
     * @return the path of the renamed file, null if user cancels
     */
    private static Path fileRenameDuplicate(File duplicate) {
        String fileNameFull = duplicate.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        String fileNameFullNoExtension = fileNameFull.substring(0, beforeExtension);
        String extension = fileNameFull.substring(beforeExtension);

        File duplicateWithIndex = incrementDuplicateFileIndex(duplicate, fileNameFullNoExtension, extension);

        TextInputDialog dialog = new TextInputDialog(duplicateWithIndex.getName().substring(0, beforeExtension + 4));
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(NAME_ALREADY_EXISTS);
        setDialogIcon(dialog, "rename-file-exists.png");
        dialog.setHeaderText("The file name \"" + duplicate.getName() + "\" already exists in the folder!");
        dialog.setContentText(RENAME_FILE_LABEL);

        Optional<String> result = dialog.showAndWait();

        Path source = rename(duplicate, extension, result);
        if (source != null) {
            return source;
        }
        return null;
    }

    /**
     * Dialog that renames file or a library folder
     *
     * @param fileToRename file to rename
     * @return the path of the duplicate file or library, null if user cancels
     */
    public static Path fileRename(File fileToRename) {
        // Rename library
        if (fileToRename.isDirectory()) {
            String fileNameFull = fileToRename.getName();
            TextInputDialog dialog = new TextInputDialog(fileNameFull);
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(PROMPT_ICON);
            dialog.setTitle(RENAME_LIBRARY_TITLE);
            dialog.setHeaderText(fileNameFull);
            setDialogIcon(dialog, "rename-library.png");
            dialog.setContentText(RENAME_LIBRARY_LABEL);
            Optional<String> result = dialog.showAndWait();

            Path source = rename(fileToRename, "", result);
            if (source != null) {
                return source;
            }

            // Rename media file
        } else {
            String fileNameFull = fileToRename.getName();
            int beforeExtension = fileNameFull.lastIndexOf('.');
            String fileName = fileNameFull.substring(0, beforeExtension);
            String extension = fileNameFull.substring(beforeExtension);

            TextInputDialog dialog = new TextInputDialog(fileName);
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(PROMPT_ICON);
            dialog.setTitle(RENAME_MEDIA_TITLE);
            dialog.setHeaderText(fileNameFull);
            setDialogIcon(dialog, "rename-song.png");
            dialog.setContentText(RENAME_FILE_LABEL);

            Optional<String> result = dialog.showAndWait();

            Path source = rename(fileToRename, extension, result);
            if (source != null) {
                return source;
            }
        }
        return null;
    }

    /**
     * Check for illegal character
     *
     * @param toExamine file name to examine
     */
    private static boolean containsIllegalChar(String toExamine) {
        Pattern pattern = Pattern.compile("[<>:\"/\\|?*]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }

    /**
     * Rename file after invalid character is found on previous rename attempt
     *
     * @param fileToRename file to rename
     * @return the path of the renamed file, null if user cancels
     */
    private static Path fileRenameInvalidChar(File fileToRename) {
        String fileNameFull = fileToRename.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        String fileName = fileNameFull.substring(0, beforeExtension);
        String extension = fileNameFull.substring(beforeExtension);

        TextInputDialog dialog = new TextInputDialog(fileName);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(RENAME_MEDIA_TITLE);
        dialog.setHeaderText("The song name \"" + fileNameFull + "\" cannot contain any of the following characters:\n" +
                "< > : \" / \\ | ? *");
        setDialogIcon(dialog, "rename-song.png");
        dialog.setContentText(RENAME_FILE_LABEL);

        Optional<String> result = dialog.showAndWait();

        Path source = rename(fileToRename, extension, result);
        if (source != null) {
            return source;
        }
        return null;
    }

    /**
     * Rename library after invalid character is found on previous rename attempt
     *
     * @param folderToRename file to rename
     * @return the path of the renamed file, null if user cancels
     */
    private static Path folderRenameInvalidChar(File folderToRename) {
        String folderName = folderToRename.getName();

        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(RENAME_LIBRARY_TITLE);
        dialog.setHeaderText("The library name \"" + folderName + "\" cannot contain any of the following characters:\n" +
                "< > : \" / \\ | ? *");
        setDialogIcon(dialog, "rename-library.png");
        dialog.setContentText(RENAME_FILE_LABEL);

        Optional<String> result = dialog.showAndWait();

        Path source = rename(folderToRename, "", result);
        if (source != null) {
            return source;
        }
        return null;
    }

    /**
     * Renames file after previous rename attempt has blank in text box
     *
     * @param fileToRename file going for rename
     * @return the path of the renamed file, null if user cancels
     */
    private static Path fileRenameRetry(File fileToRename) {
        String fileNameFull = fileToRename.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        String fileName = fileNameFull.substring(0, beforeExtension);
        String extension = fileNameFull.substring(beforeExtension);

        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(RENAME_MEDIA_TITLE);
        dialog.setHeaderText("Please enter at least one character \n to rename \"" + fileName + "\"");
        setDialogIcon(dialog, "rename-song.png");
        dialog.setContentText(RENAME_FILE_LABEL);

        Optional<String> result = dialog.showAndWait();

        Path source = rename(fileToRename, extension, result);
        if (source != null) {
            return source;
        }
        return null;
    }

    /**
     * Renames folder after previous rename attempt has blank in text box
     *
     * @param folderToRename library going for rename
     * @return the path of the renamed file, null if user cancels
     */
    private static Path folderRenameRetry(File folderToRename) {
        String folderName = folderToRename.getName();

        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(RENAME_MEDIA_TITLE);
        dialog.setHeaderText("Please enter at least one character \n to rename \"" + folderName + "\"");
        setDialogIcon(dialog, "rename-library.png");
        dialog.setContentText(RENAME_FILE_LABEL);

        Optional<String> result = dialog.showAndWait();

        Path source = rename(folderToRename, "", result);
        if (source != null) {
            return source;
        }

        return null;
    }

    /**
     * Prompt to create new playlist
     *
     * @return playlistName, otherwise null if user clicks cancel
     */
    public static String createNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(CREATE_PLAYLIST_TITLE);

        dialog.setHeaderText(null);
        setDialogIcon(dialog, "add-playlist.png");
        dialog.setContentText(NEW_PLAYLIST_LABEL);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String playlistName = result.get();
            while (playlistName != null && playlistName.isEmpty()) {
                playlistName = createNewPlaylistRetry();
            }
            return playlistName;
        }
        return null;
    }

    /**
     * Prompt to edit playlist after previous add playlist attempt has blank text box
     *
     * @param playlistToEdit to edit
     * @return newPlaylistName, or null if user clicks cancel
     */
    public static String editPlaylist(Playlist playlistToEdit) {
        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(EDIT_PLAYLIST_TITLE);

        dialog.setHeaderText("Rename \"" + playlistToEdit.getM_playlistName() + "\"");
        setDialogIcon(dialog, "rename-playlist.png");
        dialog.setContentText(RENAME_PLAYLIST_LABEL);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newPlaylistName = result.get();
            while (newPlaylistName != null && newPlaylistName.isEmpty()) {
                newPlaylistName = editPlaylistRetry(playlistToEdit);
            }
            return newPlaylistName;
        }
        return null;
    }

    /**
     * Prompt to edit playlist
     *
     * @param playlistToEdit to edit
     * @return newPlaylistName, or null if user clicks cancel
     */
    private static String editPlaylistRetry(Playlist playlistToEdit) {
        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(EDIT_PLAYLIST_TITLE);

        dialog.setHeaderText(PLAYLIST_EMPTY_HEADER);
        setDialogIcon(dialog, "rename-playlist.png");
        dialog.setContentText("Rename playlist \"" + playlistToEdit.getM_playlistName() + "\":");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * Prompt to remove playlist
     *
     * @param playlistToRemove to remove
     * @return true if user confirms prompt to delete playlist
     */
    public static boolean removePlaylist(Playlist playlistToRemove) {
        Dialog dialog = makeDialog(PROMPT_ICON, REMOVE_PLAYLIST_TITLE, "remove-playlist.png", playlistToRemove.getM_playlistName(), REMOVE_PLAYLIST_CONFIRMATION);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Optional result = dialog.showAndWait();

        return result.isPresent() && result.get() == okButton;
    }

    /**
     * Prompt to add a song to a  playlist with a drop down choice box
     *
     * @param playlists list of playlists
     * @return selected playlist the user chooses, null if user cancels
     */
    public static Playlist removePlaylistSelection(List<Playlist> playlists) {
        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(playlists.get(0), playlists);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(REMOVE_PLAYLIST_TITLE);
        setDialogIcon(dialog, "remove-playlist.png");
        dialog.setContentText(SELECT_PLAYLIST_LABEL);

        Optional<Playlist> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * Prompt to add a song to a  playlist
     *
     * @param playlists list of playlists
     * @param songToAdd song that is added to selected playlist
     * @return selected playlist the user chooses, null if user cancels
     */
    public static Playlist addSongToPlaylist(List<Playlist> playlists, Song songToAdd) {
        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(playlists.get(0), playlists);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(ADD_TO_PLAYLIST_TITLE);
        final String ADD_TO_PLAYLIST = "Add \"" + songToAdd.getM_fileName() + "\" to playlist";
        dialog.setHeaderText(ADD_TO_PLAYLIST);
        setDialogIcon(dialog, "add-song-playlist.png");
        dialog.setContentText(SELECT_PLAYLIST_LABEL);

        Optional<Playlist> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * Prompt to remove a song from a  playlist
     *
     * @param playlist song is being removed from
     * @param songName song that is added to selected playlist
     * @return true if user confirms dialog, false otherwise
     */
    public static boolean removeSongFromPlaylist(Playlist playlist, Song songName) {
        final String DIALOG_MESSAGE = "Remove \"" + songName.getM_fileName() + "\" from " + playlist.getM_playlistName();
        Dialog dialog = makeDialog(PROMPT_ICON, REMOVE_FROM_PLAYLIST_TITLE, "remove-song-playlist.png", DIALOG_MESSAGE, REMOVE_SONG_PLAYLIST_CONFIRMATION);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Optional result = dialog.showAndWait();
        return result.isPresent() && result.get() == okButton;
    }

    /**
     * Prompt to create new playlist after previous attempt has blank text box
     *
     * @return playlistName, otherwise null if user clicks cancel
     */
    private static String createNewPlaylistRetry() {
        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(ADD_PLAYLIST_TITLE);

        dialog.setHeaderText(PLAYLIST_EMPTY_HEADER);
        setDialogIcon(dialog, "add-playlist.png");
        dialog.setContentText(NEW_PLAYLIST_LABEL);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * Create basic alert prompt
     *
     * @param alertType   type of alert (error, warning, information, conformation, none)
     * @param title       title of the alert
     * @param headerText  header for the alert (optional)
     * @param bodyMessage message describing the alert
     * @return alert prompt
     */
    private static Alert makeAlertPrompt(AlertType alertType, String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(alertType);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);
        return alert;
    }

    /**
     * Create dialog with the Gamma Music Manager logo
     *
     * @param dialog object passed in to set logo
     */
    private static void setLogoDialog(Dialog dialog) {
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "gamma-logo-welcome.png"), 100, 100, false, false)));
    }

    /**
     * Create basic alert prompt
     *
     * @param dialog   object passed in to set icon
     * @param iconName name of the icon file
     */
    private static void setDialogIcon(Dialog dialog, String iconName) {
        ImageView graphic = new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                iconName)));
        dialog.setGraphic(graphic);
    }

    /**
     * Create plain dialog (without button types)
     *
     * @param title       title of the dialog prompt
     * @param headerText  header for the dialog (optional)
     * @param contentText message describing the dialog
     * @return dialog created
     */
    private static Dialog makePlainDialog(String title, String headerText, String contentText) {
        Dialog dialog = new Dialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(PROMPT_ICON);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        return dialog;
    }

    /**
     * Create basic dialog with buttons. Custom icon is recommended
     *
     * @param promptHeaderIcon prompt's icon
     * @param title            title of the dialog prompt
     * @param iconName         name of the icon file
     * @param headerText       header for the dialog (optional)
     * @param contentText      message describing the dialog
     * @return dialog created
     */
    private static Dialog<ButtonType> makeDialog(Image promptHeaderIcon, String title, String iconName, String headerText, String contentText) {
        Dialog<ButtonType> dialog = new Dialog<>();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(promptHeaderIcon);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);

        setDialogIcon(dialog, iconName);
        return dialog;
    }

    /**
     * Get the name of the music file, without it's extension
     *
     * @param mediaFile music file
     * @return the name of the file
     */
    private static String getFileNameNoExtension(File mediaFile) {
        String fileNameFull = mediaFile.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        return fileNameFull.substring(0, beforeExtension);
    }

    /**
     * Get the total folder size
     *
     * @param library folder
     * @return size of the folder
     */
    private static long folderSize(File library) {
        long length = 0;
        for (File file : library.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    /**
     * Used for rename suggestions if file already exists in the directory. Automatically increments the index until the
     * file does not exist in the directory, starting at 2
     *
     * @param duplicate               media file
     * @param fileNameFullNoExtension name of file without extension
     * @param extension               of the file
     * @return the file with an index
     */
    private static File incrementDuplicateFileIndex(File duplicate, String fileNameFullNoExtension, String extension) {
        // Start at suffix (2) by default to indicate duplicate file. Increment the suffix if the file name with this suffix exists
        int numIndex = 2;
        File duplicateWithIndex = new File(duplicate.getParent() + File.separator + fileNameFullNoExtension + " (" +
                numIndex + ")" + extension);
        while (duplicateWithIndex.exists()) {
            duplicateWithIndex = new File(duplicate.getParent() + File.separator + fileNameFullNoExtension + " (" +
                    numIndex + ")" + extension);
            numIndex++;
        }
        return duplicateWithIndex;
    }

    /**
     * Used for rename suggestions if library already exists in the directory. Automatically increments the index until the folder does not exist in
     * the directory
     *
     * @param duplicate  library
     * @param folderName name of folder
     * @return the file with an index
     */
    private static File incrementDuplicateFolderIndex(File duplicate, String folderName) {
        // Start at suffix (2) by default to indicate duplicate folder. Increment the suffix if the folder name with this suffix exists
        int numIndex = 2;
        File duplicateWithIndex = new File(duplicate.getParent() + File.separator + folderName + " (" +
                numIndex + ")");
        while (duplicateWithIndex.exists()) {
            duplicateWithIndex = new File(duplicate.getParent() + File.separator + folderName + " (" +
                    numIndex + ")");
            numIndex++;
        }
        return duplicateWithIndex;
    }

    /**
     * Renames or a library folder.
     *
     * @param fileToRename file to rename
     * @param extension    file name extension. If a library, extension should be an emptry string
     * @param result       button result
     * @return null if failed to rename
     */
    private static Path rename(File fileToRename, String extension, Optional<String> result) {
        try {
            Path source = Paths.get(fileToRename.getAbsolutePath());
            if (result.isPresent()) {
                String parentDirectory = fileToRename.getParent();
                File newName = new File(parentDirectory + File.separator + result.get() + extension);
                boolean isFileDirectory = fileToRename.isDirectory();
                if (result.get().isEmpty()) {
                    if (isFileDirectory) {
                        return folderRenameRetry(fileToRename);
                    }
                    return fileRenameRetry(fileToRename);
                } else if (newName.exists()) {
                    if (newName.isDirectory()) {
                        return folderRenameDuplicate(newName);
                    }
                    return fileRenameDuplicate(newName);
                } else if (containsIllegalChar(result.get())) {
                    if (isFileDirectory) {
                        return folderRenameInvalidChar(fileToRename);
                    }
                    return fileRenameInvalidChar(fileToRename);
                }
                return Files.move(source, source.resolveSibling(result.get() + extension));
            }
        } catch (IOException e) {
            failedToRename(fileToRename);
        }
        return null;
    }

}