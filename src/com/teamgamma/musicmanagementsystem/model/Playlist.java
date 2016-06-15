package com.teamgamma.musicmanagementsystem.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * Add song to playlist
     * @param songToAdd
     * @return boolean
     */
    public boolean addSong(Song songToAdd) {
        return m_songList.add(songToAdd);
    }

    /**
     * Remove song from playlist
     * @param songToRemove
     * @return boolean
     */
    public boolean removeSong(Song songToRemove) {
        return m_songList.remove(songToRemove);
    }

    /**
     * Shuffle order of songs in playlist
     */
    public void shufflePlaylist() {
        Collections.shuffle(m_songList);
    }

    /**
     * Get playlist
     * @return playlist
     */
    public List<Song> getM_songList() {
        return m_songList;
    }
}
