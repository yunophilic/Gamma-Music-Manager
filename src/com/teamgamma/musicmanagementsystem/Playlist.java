package com.teamgamma.musicmanagementsystem;

import java.util.*;

/**
 * Class that represents a playlist
 */
public class Playlist {
    private String m_playlistName;
    private List<Song> m_songList;

    public Playlist(String playlistName) {
        m_playlistName = playlistName;
        m_songList = new ArrayList<>();
    }

    public boolean addSong(Song songToAdd) {
        return m_songList.add(songToAdd);
    }

    public boolean removeSong(Song songToRemove) {
        return m_songList.remove(songToRemove);
    }

    public void randomizePlaylist() {
        Collections.shuffle(m_songList);
    }

    public List<Song> getM_songList() {
        return m_songList;
    }
}
