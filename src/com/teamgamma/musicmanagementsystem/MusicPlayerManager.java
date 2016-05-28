package com.teamgamma.musicmanagementsystem;

import javafx.scene.media.MediaPlayer;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Class to manage the MusicPlayer.
 */
public class MusicPlayerManager {

    private MusicPlayer m_musicPlayer;

    private Queue<Song> m_playingQueue;

    private boolean m_repeatSong = false;

    public MusicPlayerManager(){
        m_playingQueue = new PriorityQueue<Song>();
        m_musicPlayer = new MusicPlayer(this);
    }

    public void moveToNextSong(){
        Song nextSong = m_playingQueue.poll();
        if (null == nextSong) {
            // No more songs to play.
            if (m_repeatSong) {
                m_musicPlayer.resumeSong();
            }
        }

        m_musicPlayer.playSong(nextSong);
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

    public void playPlaylist() {
        // Method should add in playlist to queue.
    }

    public void setRepeat(boolean repeatSong) {
        m_repeatSong = repeatSong;
    }

    public void pause() {
        m_musicPlayer.pauseSong();
    }

    public void resume() {
        m_musicPlayer.resumeSong();
    }

    public MediaPlayer getMediaPlayer(){
        return m_musicPlayer.getMusicPlayer();
    }
}
