package com.teamgamma.musicmanagementsystem;

import java.io.File;
import java.io.FileFilter;
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

        File path = new File(pathToDirectory);
        File[] musicFiles = getMusicFiles(path);

        for (int i = 0; i < musicFiles.length; i++) {
            listOfSongs.add(new Song(musicFiles[i].getAbsolutePath()));
        }

        // For debugging
        /*for (int i = 0; i < listOfSongs.size(); i++) {
            System.out.println("Song: " + listOfSongs.get(i).getM_songName());
            System.out.println("      " + listOfSongs.get(i).getM_file().getAbsolutePath());
        }*/

        return listOfSongs;
    }

    private File[] getMusicFiles(File path) {
        return path.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                List<String> formats = new ArrayList<>();
                formats.add(".mp3");

                boolean isAccepted = false;
                for (int i = 0; i < formats.size(); i++) {
                    if (pathname.getName().endsWith(formats.get(i).toLowerCase())) {
                        isAccepted = true;
                        break;
                    }
                }
                return isAccepted;
            }
        });
    }

}
