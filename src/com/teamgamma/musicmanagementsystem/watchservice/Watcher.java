package com.teamgamma.musicmanagementsystem.watchservice;

import com.teamgamma.musicmanagementsystem.model.Library;
import com.teamgamma.musicmanagementsystem.model.PersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.model.SongManagerObserver;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class Watcher {
    private WatchService m_watcher;
    private WatchKey m_watchKey;
    private Thread m_watcherThread;
    private SongManager m_model;

    public Watcher(SongManager model) {
        this.m_model = model;
        registerAsObserver();
        openWatcher();
        updateWatcher();
    }

    public void startWatcher() {
        m_watcherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("**** Watching...");

                while (true) {
                    try {
                        m_watchKey = m_watcher.take();

                        for (WatchEvent<?> event : m_watchKey.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            Path eventPath = (Path) event.context();
                            System.out.println("**** "+ eventPath.toAbsolutePath().toString() + ": " + kind + ": " + eventPath);
                        }
                        m_watchKey.reset();
                        Platform.runLater(() -> m_model.notifyFileObservers());
                    } catch (Exception e) {
                        System.out.println("**** Watcher thread interrupted...");
                        break;
                    }
                }
                System.out.println("**** Watcher thread terminated...");
            }
        });

        m_watcherThread.start();
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
                            ENTRY_CREATE,
                            ENTRY_MODIFY,
                            ENTRY_DELETE);
                    return FileVisitResult.CONTINUE;
                } catch (IOException e) {
                    return FileVisitResult.TERMINATE;
                }
            }
        });
    }

    private void deleteWatchDir(List<File> deletedDirs) {
        PersistentStorage persistentStorage = new PersistentStorage();
        for (File file : deletedDirs) {
            persistentStorage.removePersistentStorageLibrary(file.getAbsolutePath());
            m_model.removeLibrary(file);
        }
    }

    private void registerAsObserver() {
        m_model.addObserver(new SongManagerObserver() {
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
                /* Do nothing */
            }
        });
    }

}
