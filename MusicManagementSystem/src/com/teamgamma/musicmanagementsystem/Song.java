package com.teamgamma.musicmanagementsystem;

import java.io.File;

/**
 * Underlying data structure for a Song. A File plus data additional data.
 */
public class Song {

    private File m_file;

    private String m_songName;

    public Song(String pathToFile){
        m_file = new File(pathToFile);
    }

}
