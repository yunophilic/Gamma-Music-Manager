package com.teamgamma.musicmanagementsystem.watchservice;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Watcher {
    private WatchService m_watcher;
    private WatchKey m_watchKey;

    public Watcher() {
        try {
            m_watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startWatcher() {
        try {
            while(true) {
                m_watchKey = m_watcher.take();
                for (WatchEvent<?> event : m_watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path) event.context();
                    System.out.println(kind + ": " + eventPath);
                    m_watchKey.reset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public WatchService getWatcher() {
        return m_watcher;
    }

    public WatchKey getWatchKey() {
        return m_watchKey;
    }

}
