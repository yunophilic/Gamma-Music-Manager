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
                model.setM_fileBuffer( tree.getSelectionModel().getSelectedItem().getValue().getPath() );
            }
        });

        //paste option
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                File dest = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                if (!dest.isDirectory()) {
                    System.out.println("Not a directory!"); //for now
                }
                try {
                    model.copyToDestination(dest);
                    model.notifyFileObservers();
                } catch (IOException ex) {
                    ex.printStackTrace(); //for now
                }
            }
        });

        //delete option
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                File fileToDelete = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                // TODO: show confirmation dialog before deleting
                try {
                    model.deleteFile(fileToDelete);
                } catch (Exception ex) {
                    // TODO: show popup dialog
                    ex.printStackTrace();
                }
                model.notifyFileObservers();
            }
        });

        //remove library
        MenuItem removeLibrary = new MenuItem("Remove this library");
        removeLibrary.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("Remove library"); //TODO: remove library from program view
            }
        });

        //open in right pane option
        MenuItem openInRightPane = new MenuItem("Open in right pane");
        openInRightPane.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                File folderSelected = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                if (!folderSelected.isDirectory()) {
                    System.out.println("Not a directory!"); //for now
                } else {
                    model.setRightFolderSelected(folderSelected);
                    model.notifyRightFolderObservers();
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
