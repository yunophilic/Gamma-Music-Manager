package com.teamgamma.musicmanagementsystem.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that represents a playlist
 */
public class Playlist implements PlaylistObserver {
    private String m_playlistName;
    private List<Song> m_songList;
    private int m_currentSongIndex; //-1 means no song is playing!

    public Playlist(String playlistName) {
        m_playlistName = playlistName;
        m_songList = new ArrayList<>();
        m_currentSongIndex = -1;
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
    public List<Song> shuffleAllSongs() {
        Collections.shuffle(m_songList);
        m_currentSongIndex = -1;

        // Call observer
        playlistsChanged();

        return m_songList;

    }

    public List<Song> shuffleUnplayedSongs() {
        // copy played songs...
        List<Song> playedSongs = new ArrayList<>();
        for (int i = 0; i < m_currentSongIndex+1; i++) {
            playedSongs.add(m_songList.get(i));
        }

        // copy unplayed songs...
        List<Song> unplayedSongs = new ArrayList<>();
        for (int i = m_currentSongIndex+1; i < m_songList.size(); i++) {
            unplayedSongs.add(m_songList.get(i));
        }

        // Shuffle copy of unplayed songs...
        Collections.shuffle(unplayedSongs);

        // Clear and re-add both played and unplayed songs
        m_songList.clear();
        m_songList.addAll(playedSongs);
        m_songList.addAll(unplayedSongs);

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
    public Song getCurrentSong() {
        return m_songList.get(m_currentSongIndex);
    }

    public boolean isSongPlaying() {
        return m_currentSongIndex>-1;
	}

    public Song getPreviousSong() {
        if (!m_songList.isEmpty() && m_currentSongIndex != 0){
            return m_songList.get(m_currentSongIndex - 1);
        } else {
            return m_songList.get(m_songList.size() - 1);
        }
    }

    public Song moveToPreviousSong() {
        if (!m_songList.isEmpty() && m_currentSongIndex != 0){
            m_currentSongIndex--;
        } else {
            m_currentSongIndex = m_songList.size() - 1;
        }
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
