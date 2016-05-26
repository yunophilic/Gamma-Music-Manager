package com.teamgamma.musicmanagementsystem;

import java.io.File;

/**
 * Underlying data structure for a Song. A File plus data additional data.
 */
public class Song {

    private File m_file;

    private String m_songName;

    private int m_rating;

    public Song(String pathToFile){
        m_file = new File(pathToFile);
        m_songName = m_file.getName();
        m_rating = 0;
    }

    public File getM_file(){
        return m_file;
    }

    public String getM_songName(){
        return m_songName;
    }

    public int getM_rating(){
        return m_rating;
    }

}
