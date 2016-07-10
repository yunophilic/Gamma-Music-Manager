package com.teamgamma.musicmanagementsystem.watchservice;

import com.teamgamma.musicmanagementsystem.misc.Actions;
import com.teamgamma.musicmanagementsystem.model.*;
import javafx.application.Platform;

import name.pachler.nio.file.*;
import name.pachler.nio.file.ext.ExtendedWatchEventKind;
import name.pachler.nio.file.ext.ExtendedWatchEventModifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class to monitor the file system changes
 */
public class Watcher {
    private int m_timeout = 2000;
    private WatchService m_watcher;
    private WatchKey m_watchKey;
    private Thread m_watcherThread;
    private Map<WatchKey, Path> m_keyMaps;
    private SongManager m_model;
    private DatabaseManager m_databaseManager;

    /**
     * Constructor for Watcher class
     * @param model: SongManager model
     * @param databaseManager: DatabaseManager manager
     */
    public Watcher(SongManager model, DatabaseManager databaseManager) {
        m_model = model;
        m_databaseManager = databaseManager;
        m_keyMaps = new HashMap<>();

        registerAsObserver();
        openWatcher();
        updateWatcher();
    }

    public void startWatcher() {
        m_watcherThread = new Thread(() -> {
            System.out.println("**** Watching...");
            boolean isFirst = true;
            File tempFile = null;
            Actions action = Actions.NONE;

            while (true) {
                try {
                    if (isFirst) {
                        m_watchKey = m_watcher.take();

                        System.out.println("**** File system changed...");
                    } else {
                        // Try for more events with timeout
                        m_watchKey = m_watcher.poll(m_timeout, TimeUnit.MILLISECONDS);
                    }

                    // WatchKey failed to grab more events
                    if (m_watchKey == null) {
                        Actions finalAction = action;
                        File finalTempFile = tempFile;
                        Platform.runLater(() ->
                                m_model.fileSysChanged(finalAction, finalTempFile));
                        break;
                    }

                    Path dir = m_keyMaps.get(m_watchKey);
                    for (WatchEvent<?> event : m_watchKey.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path eventPath = (Path) event.context();
                        System.out.println("**** " + kind + ": " + eventPath
                                + " [ " + dir + File.separator + eventPath + " ]");

                        // TODO: add more action cases (rename, modify)
                        if(isFirst) {
                            isFirst = false;
                            if(kind == StandardWatchEventKind.ENTRY_CREATE) {
                                action = Actions.ADD;
                            } else if(kind == StandardWatchEventKind.ENTRY_DELETE) {
                                action = Actions.DELETE;
                            } else {
                                action = Actions.NONE;
                            }
                            tempFile = new File(dir + File.separator + eventPath);
                        }
                    }
                    m_watchKey.reset();
                } catch (InterruptedException e) {
                    System.out.println("**** Watcher thread interrupted...");
                    break;
                }
            }
            System.out.println("**** Watcher thread terminated...");
        });

        m_watcherThread.start();
    }

    public void stopWatcher() {
        m_watcherThread.interrupt();
        try {
            m_watcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openWatcher() {
        m_watcher = FileSystems.getDefault().newWatchService();
    }

    private void restartWatcher() {
        stopWatcher();
        openWatcher();
        updateWatcher();
        startWatcher();
    }

    private void updateWatcher() {
        List<File> deletedDirs = new ArrayList<>();
        for (Library lib : m_model.getM_libraries()) {
            try {
                if(lib.getRootDir().exists()) {
                    addWatchDir(lib.getRootDirPath());
                } else {
                    deletedDirs.add(lib.getRootDir());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        deleteWatchDir(deletedDirs);
    }

    private void addWatchDir(String rootDirPath) throws IOException {
        Path path = Paths.get(rootDirPath);

        WatchEvent.Kind[] eventKinds = {
                StandardWatchEventKind.ENTRY_CREATE,
                StandardWatchEventKind.ENTRY_DELETE,
                StandardWatchEventKind.ENTRY_MODIFY,
                ExtendedWatchEventKind.ENTRY_RENAME_FROM,
                ExtendedWatchEventKind.ENTRY_RENAME_TO
        };
        WatchKey key = path.register(m_watcher, eventKinds, ExtendedWatchEventModifier.FILE_TREE);
        m_keyMaps.put(key, path);
    }

    private void deleteWatchDir(List<File> deletedDirs) {
        for (File file : deletedDirs) {
            m_databaseManager.removeLibrary(file.getAbsolutePath());
            m_model.removeLibrary(file);
        }
    }

    private void registerAsObserver() {
        m_model.addSongManagerObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                restartWatcher();
            }

            @Override
            public void centerFolderChanged() {
                // Do nothing
            }

            @Override
            public void rightFolderChanged() {
                // Do nothing
            }

            @Override
            public void songChanged() {
                // Do nothing
            }

            @Override
            public void fileChanged(Actions action, File file) {
                restartWatcher();
            }

            @Override
            public void leftPanelOptionsChanged() {
                // Do nothing
            }
        });
    }

}
