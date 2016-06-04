package com.teamgamma.musicmanagementsystem.watchservice;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

public class Watcher {
    private static WatchService m_watcher;

    public static void main(String[] args) {
        try {
            m_watcher = FileSystems.getDefault().newWatchService();

            Scanner reader = new Scanner(System.in);
            System.out.println("Enter a root dir: ");
            String rootDir = reader.next();

            addWatchDir(rootDir);
            WatchKey watchKey;
            System.out.format("%nWatching root dir: %s%n", rootDir);

            //Continuously check File System for changes
            while(true) {
                watchKey = m_watcher.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path) event.context();
                    System.out.println(kind + ": " + eventPath);
                    watchKey.reset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addWatchDir(String rootDir) {
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
