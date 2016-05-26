package com.teamgamma.musicmanagementsystem;

import java.util.List;

/**
 * Class to maintain a library in a system.
 */
public class Library{

    private List<Song> m_songList;

    private String m_rootDir;

    public Library(String folderPath){
        m_rootDir = folderPath;
        m_songList = new FileManager().generateSongs(folderPath);
    }

    public boolean addSong(Song songToAdd){
        return false;
    }

    public boolean removeSong(Song songToRemove){
        return false;
    }

    public boolean copySong(Song songToCopy, String pathToDest){
        return false;
    }

    public List<Song> getM_songList(){
        return m_songList;
    }

    public String getM_rootDir(){
        return m_rootDir;
    }
}
