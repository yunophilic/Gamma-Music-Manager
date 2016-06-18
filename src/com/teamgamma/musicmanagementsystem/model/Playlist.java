package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

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

        return m_songList;
    }


    /**
     * Get playlist
     * @return playlist
     */
    public List<Song> getM_songList() {
        return m_songList;
    }

    public String getM_playlistName() {
        return m_playlistName;
    }
}
