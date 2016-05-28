package com.teamgamma.musicmanagementsystem;

import javafx.scene.media.MediaPlayer;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Class to manage the the MusicPlayer.
 */
public class MusicPlayerManager {

    private IMusicPlayer m_musicPlayer;

    private Queue<Song> m_playingQueue;

    private boolean m_repeatSong = false;

    /**
     * Constructor
     */
    public MusicPlayerManager(){
        m_playingQueue = new PriorityQueue<Song>();
        m_musicPlayer = new MP3Player(this);
    }

    /**
     * Function to load the next song in the queue.
     */
    public void moveToNextSong(){
        Song nextSong = m_playingQueue.poll();
        if (null == nextSong) {
            // No more songs to play.
            if (m_repeatSong) {
                m_musicPlayer.resumeSong();
            }
        }
        else {
            m_musicPlayer.playSong(nextSong);
        }
    }

    /**
     * Function will place the song passed in to the front of the queue.
     * @param nextSong
     */
    public void playSongNext(Song nextSong) {
        if(m_playingQueue.isEmpty()){
            m_musicPlayer.playSong(nextSong);
        }
        else{
            // TODO: Make it move to front by changing prioirty.
            m_playingQueue.add(nextSong);
        }
    }

    /**
     * Function to play a playlist.
     * TODO: Revisit after playlist implementation.
     */
    public void playPlaylist() {
        // Method should add in playlist to queue.
    }


    /**
     * Function to set if the user wanted to repeat the current song playing.
     * @param repeatSong
     */
    public void setRepeat(boolean repeatSong) {
        m_repeatSong = repeatSong;
        m_musicPlayer.repeatSong(repeatSong);
    }

    /**
     * Fucntion to pause the current song playing.
     */
    public void pause() {
        m_musicPlayer.pauseSong();
    }

    /**
     * Function to resume the current song that is paused.
     */
    public void resume() {
        m_musicPlayer.resumeSong();
    }

    /**
     * Function to get the JavaFX Media player for the Media View.
     * @return The FX MediaPlayer if the underlying player uses it or null if it does not.
     */
    public MediaPlayer getMediaPlayer(){
        if (m_musicPlayer instanceof MP3Player) {
            return ((MP3Player) m_musicPlayer).getMusicPlayer();
        } else {
            return null;
        }
    }
}
