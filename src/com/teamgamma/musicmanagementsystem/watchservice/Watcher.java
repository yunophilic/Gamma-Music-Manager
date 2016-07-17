package com.teamgamma.musicmanagementsystem.watchservice;

import com.teamgamma.musicmanagementsystem.util.Action;
import com.teamgamma.musicmanagementsystem.model.*;
import javafx.application.Platform;

import javafx.util.Pair;
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

    /**
     * Creates a new Thread for the Watcher class to monitor the File System.
     */
    public void startWatcher() {
        m_watcherThread = new Thread(() -> {
            System.out.println("**** Watching...");
            boolean isFirst = true;
            List<Pair<Action, File>> fileActions = new ArrayList<>();

            watcherRoutine(isFirst, fileActions);
            System.out.println("**** Watcher thread terminated...");
        });

        m_watcherThread.start();
    }

    /**
     * The main routine of the Watcher class.
     *
     * @param isFirst Boolean value indicating if the Watcher is notified for the first time.
     * @param fileActions
     */
    private void watcherRoutine(boolean isFirst, List<Pair<Action, File>> fileActions) {
        while (true) {
            try {
                if (isFirst) {
                    System.out.println("**** File system changed...");
                    m_watchKey = m_watcher.take();
                    isFirst = false;
                } else {
                    // Try for more events with timeout
                    m_watchKey = m_watcher.poll(m_timeout, TimeUnit.MILLISECONDS);
                }

                // WatchKey failed to grab more events
                if (m_watchKey == null) {
                    Platform.runLater(() -> m_model.fileSysChanged(fileActions));
                    break;
                }

                handleEvents(fileActions);
                m_watchKey.reset();
            } catch (InterruptedException e) {
                System.out.println("**** Watcher thread interrupted...");
                break;
            }
        }
    }

    /**
     * Handle the WatchKey events.
     * @param fileActions List of action and file pairs.
     */
    private void handleEvents(List<Pair<Action, File>> fileActions) {
        Path dir = m_keyMaps.get(m_watchKey);
        for (WatchEvent<?> event : m_watchKey.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            Path eventPath = (Path) event.context();
            System.out.println("**** " + kind + ": " + eventPath
                    + " [ " + dir + File.separator + eventPath + " ]");

            File file = new File(dir + File.separator + eventPath);
            fileActions.add(new Pair<>(handleAction(kind), file));
        }
    }

    /**
     * Set the Action of the event.
     * @param eventKind The type of the event.
     * @return The resulting Action.
     */
    private Action handleAction(WatchEvent.Kind<?> eventKind) {
        Action action;
        if(eventKind == StandardWatchEventKind.ENTRY_CREATE) {
            action = Action.ADD;
        } else if(eventKind == StandardWatchEventKind.ENTRY_DELETE) {
            action = Action.DELETE;
        } else if(eventKind == ExtendedWatchEventKind.ENTRY_RENAME_FROM) {
            action = Action.RENAME;
        } else if(eventKind == ExtendedWatchEventKind.ENTRY_RENAME_TO) {
            action = Action.RENAME;
        } else {
            action = Action.NONE;
        }
        return action;
    }

    /**
     * Stop the Watcher class.
     */
    public void stopWatcher() {
        m_watcherThread.interrupt();
        try {
            m_watcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the WatchService to File System default.
     */
    private void openWatcher() {
        m_watcher = FileSystems.getDefault().newWatchService();
    }

    /**
     * Restart Watcher class.
     */
    private void restartWatcher() {
        stopWatcher();
        openWatcher();
        updateWatcher();
        startWatcher();
    }

    /**
     * Update the watched root directories.
     */
    private void updateWatcher() {
        for (Library lib : m_model.getM_libraries()) {
            try {
                addWatchDir(lib.getRootDirPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add a directory to be watched.
     *
     * @param rootDirPath The path to the directory.
     * @throws IOException
     */
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

    /**
     * Register model observers.
     */
    private void registerAsObserver() {
        m_model.addLibraryObserver((action, file) -> restartWatcher());
        m_model.addFileObserver((action, file) -> restartWatcher());
    }

}
