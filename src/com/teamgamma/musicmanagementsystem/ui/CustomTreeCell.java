package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.SongManager;
import com.teamgamma.musicmanagementsystem.TreeViewItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

public class CustomTreeCell extends TextFieldTreeCell<TreeViewItem> {
    private ContextMenu contextMenu;
    private SongManager model;
    private TreeView<TreeViewItem> tree;

    public CustomTreeCell(SongManager modelRef, TreeView<TreeViewItem> treeRef, boolean isLeftPane) {
        contextMenu = new ContextMenu();
        model = modelRef;
        tree = treeRef;
        contextMenu.getItems().addAll(generateMenuItems(isLeftPane));
    }

    private List<MenuItem> generateMenuItems(boolean isLeftPane) {
        //copy option
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    model.setM_fileBuffer(tree.getSelectionModel().getSelectedItem().getValue().getPath());
                }
            }
        });

        //paste option
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    File dest = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    if (!dest.isDirectory()) {
                        PromptUI.customPromptError("Not a directory", "", "Please select a directory as the paste target.");
                        return;
                    }
                    try {
                        model.copyToDestination(dest);
                        model.notifyFileObservers();
                    } catch (FileAlreadyExistsException ex) {
                        PromptUI.customPromptError("Error", "", "The following file or folder already exist!\n" + ex.getMessage());
                    } catch (IOException ex) {
                        PromptUI.customPromptError("Error", "", "IOException:" + ex.getMessage());
                    }
                }
            }
        });

        //delete option
        MenuItem delete = new MenuItem("Delete");
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
                        PromptUI.customPromptError("Error", "", "Fail to delete!\n" + ex.getMessage());
                    }
                    model.notifyFileObservers();
                }
            }
        });

        //remove library
        MenuItem removeLibrary = new MenuItem("Remove this library");
        removeLibrary.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    System.out.println("Remove library"); //TODO: remove library from program view
                    model.removeLibrary(tree.getSelectionModel().getSelectedItem().getValue().getPath());
                    model.notifyLibraryObservers();
                }
            }
        });

        //open in right pane option
        MenuItem openInRightPane = new MenuItem("Open in right pane");
        openInRightPane.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    File folderSelected = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    if (!folderSelected.isDirectory()) {
                        System.out.println("Not a directory!"); //for now
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
        return menuItems;
    }

    @Override
    public void updateItem(TreeViewItem item, boolean empty) {
        super.updateItem(item, empty);
        setContextMenu(contextMenu);
    }
}
