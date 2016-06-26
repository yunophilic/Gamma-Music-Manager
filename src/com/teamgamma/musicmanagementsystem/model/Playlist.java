package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.model.PlaylistObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that represents a playlist
 */
public class Playlist implements PlaylistObserver {
    private String m_playlistName;
    private List<Song> m_songList;
    public int currentPlaylistSong = 0;

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
    public List<Song> shufflePlaylist() {

        Collections.shuffle(m_songList);

        // Call observer
        playlistsChanged();
        return m_songList;

    }

    public List<Song> shuffleSomePlaylist() {
        List<Song> copyList = new ArrayList<>();
        MusicPlayerManager copier = new MusicPlayerManager();
        boolean currentSong = false;
        int counter = 0;
        int tracker = 0;
        int playlistSize = 0;

        // Find point to shuffle from
        for(Song s: m_songList) {
            if (s != copier.getCurrentSongPlaying()) {
                counter += 1;
            }
            // FOUND POINT TO SHUFFLE
            else {
                currentSong = true;
                tracker = counter;
                break;
            }
        }

        // Add all songs from index point to end of playlist to copyList (Excludes played songs)
        for (counter = counter; counter < m_songList.size(); counter++) {
            copyList.add(m_songList.get(counter));
        }

        // Shuffle
        Collections.shuffle(copyList);
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

    // Return one song at a time
    public Song oneAtATime() {

        // Keep track of this int in the database, call it from the player
        //currentPlaylistSong++;
        return m_songList.get(currentPlaylistSong);
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
