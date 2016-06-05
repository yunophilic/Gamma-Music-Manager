package com.teamgamma.musicmanagementsystem.watchservice;

import com.teamgamma.musicmanagementsystem.Library;
import com.teamgamma.musicmanagementsystem.SongManager;
import javafx.application.Platform;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Watcher {
    private WatchService m_watcher;
    private WatchKey m_watchKey;
    private Thread m_watcherThread;
    private SongManager model;

    public Watcher(SongManager model) {
        try {
            this.model = model;
            m_watcher = FileSystems.getDefault().newWatchService();

            for (Library lib : model.getM_libraries()) {
                addWatchDir(lib.getM_rootDirPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startWatcher() {
        m_watcherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("**** Watching...");

                try {
                    while(true) {
                        m_watchKey = m_watcher.take();

                        for (WatchEvent<?> event : m_watchKey.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            Path eventPath = (Path) event.context();
                            System.out.println("**** " + kind + ": " + eventPath);
                            m_watchKey.reset();
                            Platform.runLater(() -> {
                                model.notifyFileObservers();
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        m_watcherThread.start();
    }

    public void addWatchDir(String rootDir) {
        Path path = Paths.get(rootDir);

        //Register root + all sub directories in root directory to watcher
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    dir.register(m_watcher,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
