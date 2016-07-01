package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that represents a playlist
 */
public class Playlist implements PlaylistObserver {
    private String m_playlistName;
    private List<Song> m_songList;
    public int m_currentSongIndex = 0;

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
    public List<Song> shuffleWholePlaylist() {
        Collections.shuffle(m_songList);
        m_currentSongIndex = 0;

        // Call observer
        playlistsChanged();

        return m_songList;

    }

    public List<Song> shufflePlaylistFromCurrentSong() {
        List<Song> copyList = new ArrayList<>();

        boolean currentSong = false;
        int counter = m_currentSongIndex;
        int tracker = 0;

        // Add all songs from index point to end of playlist to copyList (Excludes played songs)
        for (; counter < m_songList.size(); counter++) {
            copyList.add(m_songList.get(counter));
        }

        // Shuffle
        Collections.shuffle(copyList);
        int playlistSize = 0;
        playlistSize = m_songList.size();

        // Get rid of the songs from the index point onwards (Because original playlist is not shuffled)
        while (tracker != playlistSize) {
            m_songList.remove(tracker);
            tracker++;
        }

        // Add the shuffled copylist to original playlist
        for (Song s: copyList) {
            m_songList.add(s);
        }

        // Call observer
        playlistsChanged();
        return m_songList;
    }

    /**
     * Function to get the next song in the playlist.
     *
     * @return The next song in the playlist
     */
    public Song getNextSong(){
        if (!m_songList.isEmpty() && (m_songList.size() - 1 == m_currentSongIndex)) {
            return m_songList.get(0);
        } else {
            return  m_songList.get(m_currentSongIndex + 1);
        }
    }

    /**
     * Function to move to the next song in the playlist.
     * @return  The song that is to be played next.
     */
    public Song moveToNextSong(){
        if (!m_songList.isEmpty() && (m_songList.size() - 1 == m_currentSongIndex)) {
            m_currentSongIndex = 0;
        } else {
            m_currentSongIndex++;
        }
        return m_songList.get(m_currentSongIndex);
    }

    /**
     * Function to get the current song in the playlist.

     * @return The current song in the playlist.
     */
    public Song getCurrentSong(){
        return m_songList.get(m_currentSongIndex);
    }

    // Return one song at a time
    public Song oneAtATime() {
        // Keep track of this int in the database, call it from the player
        //m_currentSongIndex++;
        return m_songList.get(m_currentSongIndex);
    }

    public boolean isLastSongInPlaylist(){
        return (m_currentSongIndex == (m_songList.size() - 1));
    }

    // TODO: Do this
    /*public int getCurrentSongPlayingIndex() {
        MusicPlayerManager
    }*/

    public List<Song> getM_songList() {
        return m_songList;
    }

    public String getM_playlistName() {
        return m_playlistName;
    }

    public void setM_playlistName(String m_playlistName) {
        this.m_playlistName = m_playlistName;
    }

    /**
     * For drop down menu
     *
     * @return name of the playlist
     */
    @Override
    public String toString() {
        return m_playlistName;
    }

    @Override
    public void playlistsChanged() {

    }

    @Override
    public void songsChanged() {

    }
}
