package com.teamgamma.musicmanagementsystem.musicplayer;

import com.teamgamma.musicmanagementsystem.model.Song;
import javafx.util.Duration;

/**
 * Interface that defines actions a music player can have. Any player wishing to work in the application will have to
 * implement this interface to work with the MusicPlayerManager.
 */
public interface IMusicPlayer {
    /**
     * The method used to play the song on the music player.
     *
     * @param songToPlay
     */
    void playSong(Song songToPlay);

    /**
     * This function should pause the current song that is being played.
     */
    void pauseSong();

    /**
     * The function should resume the current song that is paused.
     */
    void resumeSong();

    /**
     * This function should increase the volume of the song being played.
     */
    void increaseVolume();

    /**
     * This function should decrease the volume of the song being played.
     */
    void decreaseVolume();

    /**
     * This function should set the player to repeat the current song that is being played.
     *
     * @param repeatSong The flag that will set if the player is to repeat the song.
     */
    void repeatSong(boolean repeatSong);

    /**
     * This function should set the action that the player will take when the song is finished running.
     *
     * @param action
     */
    void setOnSongFinishAction(Runnable action);

    /**
     * This function should set the action that the player will take when an error occurs.
     *
     * @param action
     */
    void setOnErrorAction(Runnable action);

    /**
     * Function to see if something is being played in the song manager.
     *
     * @return True if something is being played, false otherwise.
     */
    boolean isPlayingSong();

    /**
     * Function to see if the player is ready to be used
     */
    boolean isReadyToUse();

    /**
     * Function to stop the song that is playing.
     */
    void stopSong();

    /**
     * Function to get the current playback time for the duration
     * @return A duration object containing the currnet time.
     */
    Duration getCurrentPlayTime();

    /**
     * Function to seek to the given percent of the song
     * @param The percent of the song you want to seek to
     */
    void seekToTime(double percent);
}
