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
    private int m_currentSongIndex; //-1 means no song is playing!
    private double m_songResumeTime;

    /**
     * Constructor
     *
     * @param playlistName
     */
    public Playlist(String playlistName) {
        m_playlistName = playlistName;
        m_songList = new ArrayList<>();
        m_currentSongIndex = -1;
    }

    /**
     * Constructor to set the current song in the playlist.
     *
     * @param playlistName
     * @param songIndex
     */
    public Playlist(String playlistName, int songIndex) {
        m_playlistName = playlistName;
        m_songList = new ArrayList<>();
        m_currentSongIndex = songIndex;
    }

    /**
     * Add song to playlist
     *
     * @param songToAdd song object to be added
     * @return boolean
     */
    public boolean addSong(Song songToAdd) {
        return m_songList.add(songToAdd);
    }

    /**
     * Add songs to playlist
     *
     * @param songsToAdd list of songs to be added
     * @return boolean
     */
    public boolean addSongs(List<Song> songsToAdd) {
        return m_songList.addAll(songsToAdd);
    }

    /**
     * Remove song from playlist
     *
     * @param songToRemoveIndex index of song to be removed
     */
    public void removeSong(int songToRemoveIndex) {
        m_songList.remove(songToRemoveIndex);

        //refresh current song index
        if(m_currentSongIndex > songToRemoveIndex) {
            m_currentSongIndex--;
        }

        //restart playlist if current index exceeds list range
        if(m_currentSongIndex > m_songList.size()-1) {
            m_currentSongIndex = m_songList.isEmpty() ? -1 : 0;
        }
    }

    /**
     * Shuffle order of songs in playlist
     */
    public List<Song> shuffleAllSongs() {
        Collections.shuffle(m_songList);
        m_currentSongIndex = -1;

        return m_songList;
    }

    /**
     * Shuffle unplayed songs in playlist
     */
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

        return m_songList;
    }

    /**
     * Function to get the next song in the playlist.
     *
     * @return The next song in the playlist
     */
    public Song getNextSong(){
        if (isLastSong()) {
            return m_songList.get(0);
        } else {
            return  m_songList.get(m_currentSongIndex + 1);
        }
    }

    /**
     * Function to move to the next song in the playlist.
     *
     * @return  The song that is to be played next.
     */
    public Song moveToNextSong(){
        if (isLastSong()) {
            m_currentSongIndex = 0;
        } else {
            m_currentSongIndex++;
        }
        return m_songList.get(m_currentSongIndex);
    }

    /**
     * Function to tell you if it is the last song.
     *
     * @return True if it is the last song in the playlist or false if it is not.
     */
    private boolean isLastSong() {
        return !m_songList.isEmpty() && (m_songList.size() - 1 <= m_currentSongIndex);
    }

    /**
     * Function to get the current song in the playlist.

     * @return The current song in the playlist.
     */
    public Song getCurrentSong() {
        return m_songList.get(m_currentSongIndex);
    }

    /**
     * Function to get the song based on the index location it is in the playlist.
     *
     * @param index The index of the song
     *
     * @return The song at that index.
     */
    public Song getSongByIndex(int index) {
        return m_songList.get(index);
    }

    /**
     * Function to validate index
     *
     * @param index The index to be checked
     *
     * @return true if the index is in bounds, false otherwise
     */
    public boolean isValid(int index) {
        return (index >= 0) && (index < m_songList.size());
    }

    /**
     * Function to tell you if there is a song loaded in the playlist.
     *
     * @return True if there is, false other wise.
     */
    public boolean isThereASongLoadedInPlaylist() {
        return m_currentSongIndex > -1;
	}

    /**
     * Function to get you the previous song.
     *
     * @return The previous song in the playlist.
     */
    public Song getPreviousSong() {
        if (isThereAPreviousSong()){
            return m_songList.get(m_currentSongIndex - 1);
        } else {
            return m_songList.get(m_songList.size() - 1);
        }
    }

    /**
     * Function to tell you if there is a previous song.
     *
     * @return True if there is false other wise.
     */
    private boolean isThereAPreviousSong() {
        return !m_songList.isEmpty() && m_currentSongIndex != 0;
    }

    /**
     * Function to move to the previous song and update position in the playlist.
     *
     * @return The song that should be the current song playing.
     */
    public Song moveToPreviousSong() {
        if (isThereAPreviousSong()){
            m_currentSongIndex--;
        } else {
            m_currentSongIndex = m_songList.size() - 1;
        }
        return m_songList.get(m_currentSongIndex);
    }

    /**
     * Function to check if the current song is the last on in the playlist
     *
     * @return True if the current song is the last on in the playlist false otherwise.
     */
    public boolean isLastSongInPlaylist(){
        return (m_currentSongIndex == (m_songList.size() - 1));
    }

    /**
     * Function to check if the playlist is empty
     *
     * @return True if it is false other wise.
     */
    public boolean isEmpty() {
        return m_songList.isEmpty();
    }

    /**
     * Function to get all the songs in the playlist.
     *
     * return The list of songs in the playlist
     */
    public List<Song> getM_songList() {
        return m_songList;
    }

    /**
     * Function to get the playlist name .
     *
     * @return The name of the playlist
     */
    public String getM_playlistName() {
        return m_playlistName;
    }

    /**
     * Function to get the index of the current song in the playlist.
     *
     * @return The current song index
     */
    public int getM_currentSongIndex() {
        return m_currentSongIndex;
    }

    /**
     * Function to set the name of the playlist.
     *
     * @param m_playlistName The name of the playlist.
     */
    public void setM_playlistName(String m_playlistName) {
        this.m_playlistName = m_playlistName;
    }

    /**
     * Function to set the current song index of the playlist.
     *
     * @param index The index to set.
     */
    public void setM_currentSongIndex(int index) {
        m_currentSongIndex = index;
    }

    /**
     * Function to set the resume time of the song from this playlist when the application closes
     *
     * @param resumeTime
     */
    public void setM_songResumeTime(double resumeTime) {
        m_songResumeTime = resumeTime;
    }

    /**
     * Function to get the resume time of the song from this playlist when the application opens
     *
     * @return
     */
    public double getM_songResumeTime() {
        return m_songResumeTime;
    }

    @Override
    public String toString() {
        return m_playlistName;
    }
}
