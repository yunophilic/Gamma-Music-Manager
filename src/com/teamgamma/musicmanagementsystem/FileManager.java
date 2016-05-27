package com.teamgamma.musicmanagementsystem;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *  Class to manage files in the program.
 */
public class FileManager {

    public FileManager() {
        // Do nothing.
    }

    public List<Song> generateSongs(String pathToDirectory) {
        List<Song> listOfSongs = new ArrayList<>();

        // Get all music files in the path (including subdirectories)
        File path = new File(pathToDirectory);
        List<File> musicFiles = getMusicFiles(path);

        // Create Song object and add to array list
        for (File musicFile: musicFiles) {
            listOfSongs.add(new Song(musicFile.getAbsolutePath()));
        }

        // For debugging
        /*System.out.println("Number of music files found: " + listOfSongs.size());
        for (Song song: listOfSongs) {
            System.out.println("Song: " + song.getM_songName());
            System.out.println("      " + song.getM_file().getAbsolutePath());
        }*/

        return listOfSongs;
    }

    public boolean removeFile(File fileToRemove) {
        return fileToRemove.delete();
    }

    private List<File> getMusicFiles(File path) {
        List<File> musicFiles = new ArrayList<>();
        File[] files = path.listFiles();
        for (File file: files) {
            if (file.isFile()) {
                String[] extensions = new String[] {".mp3"};
                if (isAccept(file, extensions)) {
                    musicFiles.add(file);
                }
            } else {
                musicFiles.addAll(getMusicFiles(file));
            }
        }

        return musicFiles;
    }

    private boolean isAccept(File file, String[] extensions){
        for (String extension: extensions) {
            if (file.getName().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
