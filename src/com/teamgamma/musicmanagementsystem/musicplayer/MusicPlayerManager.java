package com.teamgamma.musicmanagementsystem.musicplayer;

import com.teamgamma.musicmanagementsystem.Song;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class to manage the the MusicPlayer.
 */
public class MusicPlayerManager {

    public static final int MAX_SONG_HISTORY = 10;

    private IMusicPlayer m_musicPlayer;

    private Queue<Song> m_playingQueue;

    private boolean m_repeatSong = false;

    private Queue<Song> m_songHistory;

    private Song m_currentSong = null;

    private List<MusicPlayerObserver> m_newSongObservers;

    private MusicPlayerObserver m_seekObserver;
    /**
     * Constructor
     */
    public MusicPlayerManager(){
        m_playingQueue = new PriorityQueue<Song>();
        m_musicPlayer = new MP3Player(this);
        m_songHistory = new ConcurrentLinkedQueue<Song>();

        m_newSongObservers = new ArrayList<MusicPlayerObserver>();
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
            updateHistory();
        }
    }

    /**
     * Function will place the song passed in to the front of the queue.
     * @param nextSong
     */
    public void playSongNext(Song nextSong) {
        if(m_playingQueue.isEmpty()){
            m_musicPlayer.playSong(nextSong);
            m_currentSong = nextSong;
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
     * Function to increase the volume.
     */
    public void increaseVolume() {
        m_musicPlayer.increaseVolume();
    }

    /**
     * Function to decrease the volume.
     */
    public void decreaseVolume() {
        m_musicPlayer.decreaseVolume();
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

    /**
     * Function to add a observer to the list of observers that are notified when a new song is being played.
     * @param observer
     */
    public void registerNewSongObserver(MusicPlayerObserver observer) {
        m_newSongObservers.add(observer);
    }

    /**
     * Function to get the current song playing in the player.
     * @return The current song loaded in the player or Null if there is none.
     */
    public Song getCurrentSongPlaying() {
        return m_currentSong;
    }

    /**
     * Function to get the end time of the song from the player.
     * @return  The end time of the song.
     */
    public Duration getEndTime(){
        return ((MP3Player) m_musicPlayer).getEndTime();
    }

    /**
     * Function to update the history of the music player.
     */
    private void updateHistory() {
        if (null == m_currentSong) {
            return;
        }

        m_songHistory.add(m_currentSong);

        if (m_songHistory.size() > MAX_SONG_HISTORY){
            m_songHistory.poll();
        }
    }

    /**
     * Function to notify all observers that a new song has been loaded.
     */
    public void notifyNewSongObservers(){
        for (MusicPlayerObserver observer : m_newSongObservers) {
            observer.updateUI();
        }
    }

    public void registerSeekObserver(MusicPlayerObserver observer){
        m_seekObserver = observer;
    }

    public void notifySeekObserver(){
        System.out.println("Notifying Seek observer");
        m_seekObserver.updateUI();
    }

    public Duration getCurrentPlayTime(){
        return ((MP3Player) m_musicPlayer).getCurrentPlayTime();
    }
}
