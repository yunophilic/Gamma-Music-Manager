package com.teamgamma.musicmanagementsystem.watchservice;

import com.teamgamma.musicmanagementsystem.model.*;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Watcher {
    private WatchService m_watcher;
    private WatchKey m_watchKey;
    private Thread m_watcherThread;
    private SongManager m_model;
    private DatabaseManager m_databaseManager;

    public Watcher(SongManager model, DatabaseManager databaseManager) {
        m_model = model;
        m_databaseManager = databaseManager;
        registerAsObserver();
        openWatcher();
        updateWatcher();
    }

    public void startWatcher() {
        m_watcherThread = new Thread(() -> {
            System.out.println("**** Watching...");
            boolean isFirst = true;

            while (true) {
                try {
                    if (isFirst) {
                        m_watchKey = m_watcher.take();

                        isFirst = false;
                        System.out.println("**** File system changed...");
                    } else { //Try for more events with timeout
                        m_watchKey = m_watcher.poll(5, TimeUnit.SECONDS);

                        if (m_watchKey == null) { //WatchKey failed to grab more events
                            Platform.runLater(() -> m_model.notifyFileObservers());
                            break;
                        }
                    }

                    printWatchEvent();
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

    private void printWatchEvent() {
        for (WatchEvent<?> event : m_watchKey.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            Path eventPath = (Path) event.context();
            System.out.println("**** " + kind + ": " + eventPath
                    + " [ " + eventPath.toAbsolutePath().toString() + " ]");
        }
    }

    public void stopWatcher() {
        m_watcherThread.interrupt();
        try {
            m_watcher.close();
            System.out.println("**** Watcher closed...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openWatcher() {
        try {
            m_watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                addWatchDir(lib.getM_rootDirPath());
            } catch (IOException e) {
                System.out.println("**** Directory does not exist: " + lib.getM_rootDirPath());
                deletedDirs.add(lib.getM_rootDir());
            }
        }
        deleteWatchDir(deletedDirs);
    }

    private void addWatchDir(String rootDir) throws IOException {
        Path path = Paths.get(rootDir);

        //Register root + all sub directories in root directory to watcher
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                try {
                    dir.register(m_watcher,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                } catch (IOException e) {
                    return FileVisitResult.TERMINATE;
                }
            }
        });
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
                //Do nothing
            }

            @Override
            public void rightFolderChanged() {
                //Do nothing
            }

            @Override
            public void songChanged() {
                //Do nothing
            }

            @Override
            public void fileChanged() {
                restartWatcher();
            }

            @Override
            public void leftPanelOptionsChanged() {
                //Do nothing
            }
        });
    }

}
