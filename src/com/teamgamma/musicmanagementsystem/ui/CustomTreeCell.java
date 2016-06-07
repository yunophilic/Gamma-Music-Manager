package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.SongManager;
import com.teamgamma.musicmanagementsystem.TreeViewItem;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

public class CustomTreeCell extends TextFieldTreeCell<TreeViewItem> {
    //attributes
    private ContextMenu contextMenu;
    private SongManager model;
    private TreeView<TreeViewItem> tree;

    //constants
    private static final String COPY = "Copy";
    private static final String PASTE = "Paste";
    private static final String DELETE = "Delete";
    private static final String REMOVE_THIS_LIBRARY = "Remove This Library";
    private static final String OPEN_IN_RIGHT_PANE = "Open in Right Pane";


    public CustomTreeCell(SongManager modelRef, TreeView<TreeViewItem> treeRef, boolean isLeftPane) {
        contextMenu = new ContextMenu();
        model = modelRef;
        tree = treeRef;
        contextMenu.getItems().addAll(generateMenuItems(isLeftPane));
    }

    private List<MenuItem> generateMenuItems(boolean isLeftPane) {
        final boolean isLibraryPath;

        //copy option
        MenuItem copy = new MenuItem(COPY);
        copy.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    model.setM_fileBuffer(tree.getSelectionModel().getSelectedItem().getValue().getPath());
                }
            }
        });

        //paste option
        MenuItem paste = new MenuItem(PASTE);
        paste.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    File dest = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    if (!dest.isDirectory()) {
                        PromptUI.customPromptError("Not a directory!", "", "Please select a directory as the paste target.");
                        return;
                    }
                    try {
                        model.copyToDestination(dest);
                        model.notifyFileObservers();
                    } catch (FileAlreadyExistsException ex) {
                        PromptUI.customPromptError("Error", "", "The following file or folder already exist!\n" + ex.getMessage());
                    } catch (IOException ex) {
                        PromptUI.customPromptError("Error", "", "IOException: " + ex.getMessage());
                    }
                }
            }
        });

        //delete option
        MenuItem delete = new MenuItem(DELETE);
        delete.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    File fileToDelete = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    //confirmation dialog
                    if (!PromptUI.customPromptConfirmation(
                            "Deleting " + (fileToDelete.isDirectory() ? "folder" : "file"),
                            "",
                            "Are you sure you want to permanently delete \"" + fileToDelete.getName() + "\"?")) {
                        return;
                    }
                    //try to actually delete
                    try {
                        model.deleteFile(fileToDelete);
                    } catch (Exception ex) {
                        PromptUI.customPromptError("Error", "", "Exception: \n" + ex.getMessage());
                    }
                    model.notifyFileObservers();
                }
            }
        });

        //remove library
        MenuItem removeLibrary = new MenuItem(REMOVE_THIS_LIBRARY);
        removeLibrary.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    System.out.println("Remove library");
                    model.removeLibrary(tree.getSelectionModel().getSelectedItem().getValue().getPath());

                    if (model.getRightFolderSelected() != null) {
                        boolean isLibraryInRight = model.getRightFolderSelected().getAbsolutePath().contains(tree.getSelectionModel().getSelectedItem().getValue().getPath().getAbsolutePath());
                        if (isLibraryInRight) {
                            model.setRightFolderSelected(null);
                        }
                    }

                    if (model.getM_selectedCenterFolder() != null) {
                        boolean isLibraryInCenter = model.getM_selectedCenterFolder().getAbsolutePath().contains(tree.getSelectionModel().getSelectedItem().getValue().getPath().getAbsolutePath());
                        if (isLibraryInCenter) {
                            System.out.println("SELECTED CENTER FOLDER REMOVED!!!");
                            model.setCenterFolder(null);
                        }
                    }
                    model.notifyLibraryObservers();
                }
            }
        });

        //open in right pane option
        MenuItem openInRightPane = new MenuItem(OPEN_IN_RIGHT_PANE);
        openInRightPane.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    File folderSelected = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    if (!folderSelected.isDirectory()) {
                        PromptUI.customPromptError("Not a directory!", "", "Please select a directory.");
                    } else {
                        model.setRightFolderSelected(folderSelected);
                        model.notifyRightFolderObservers();
                    }
                }
            }
        });

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(copy);
        menuItems.add(paste);
        menuItems.add(delete);
        if (isLeftPane) {
            menuItems.add(removeLibrary);
            menuItems.add(openInRightPane);
        }

        contextMenu.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // Disable paste if nothing is chosen to be copied
                if (model.getM_fileBuffer() == null) {
                    paste.setDisable(true);
                    paste.setStyle("-fx-text-fill: gray;");
                }

                // Do not show remove library option if selected item is not a library
                if (tree.getSelectionModel().getSelectedItem() == null || !tree.getSelectionModel().getSelectedItem().getValue().isRootPath()) {
                    removeLibrary.setDisable(true);
                    removeLibrary.setVisible(false);
                }
            }
        });

        return menuItems;
    }

    //THESE TWO FUNCTIONS ARE NOT WORKING PROPERLY
    /*public void disablePasteOption() {
        for(MenuItem menuItem : contextMenu.getItems() ) {
            if (menuItem.getText().equals(PASTE)) {
                menuItem.setDisable(true);
                return;
            }
        }
    }

    public void enablePasteOption() {
        for(MenuItem menuItem : contextMenu.getItems() ) {
            if (menuItem.getText().equals(PASTE)) {
                menuItem.setDisable(false);
                return;
            }
        }
    }*/

    @Override
    public void updateItem(TreeViewItem item, boolean empty) {
        super.updateItem(item, empty);
        setContextMenu(contextMenu);
    }
}
