package com.teamgamma.musicmanagementsystem.musicplayer;

/**
 * Example interface for observer pattern.
 * TODO: Determine if we should use custom interfaces or just use Java Observer and Observable
 */
public interface MusicPlayerObserver {

    /**
     * Function to update the user interface via the observer pattern.
     */
    void updateUI();
}
