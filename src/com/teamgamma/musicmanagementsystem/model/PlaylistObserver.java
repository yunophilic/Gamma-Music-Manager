package com.teamgamma.musicmanagementsystem.model;

/**
 * Observer class for Playlist
 */
public interface PlaylistObserver {
    // Update UI if a song is added to playlist
    void songsChanged();

    void playlistsChanged();
}
