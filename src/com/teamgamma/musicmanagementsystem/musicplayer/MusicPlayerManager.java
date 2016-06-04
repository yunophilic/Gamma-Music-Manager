package com.teamgamma.musicmanagementsystem.musicplayer;

import com.teamgamma.musicmanagementsystem.Playlist;
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

    private List<Song> m_songHistory;

    private List<MusicPlayerObserver> m_newSongObservers;

    private Song m_currentSong = null;

    private boolean m_repeatSong = false;

    private int m_historyIndex = 0;

    private List<MusicPlayerObserver> m_playbackObservers;

    private List<MusicPlayerObserver> m_changeStateObserver;
    /**
     * Constructor
     */
    public MusicPlayerManager(){
        m_playingQueue = new ConcurrentLinkedQueue<Song>();
        m_musicPlayer = new MP3Player(this);
        m_songHistory = new ArrayList<Song>();

        m_newSongObservers = new ArrayList<MusicPlayerObserver>();
        m_playbackObservers = new ArrayList<MusicPlayerObserver>();
        m_changeStateObserver = new ArrayList<MusicPlayerObserver>();
    }

    /**
     * Function to load the next song in the queue.
     */
    public void playNextSong(){
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
            notifyChangeStateObservers();
        }
    }

    /**
     * Function will place the song passed in the playback queue. This will play the song immediately if there is not thing
     * in the queue.
     * @param nextSong
     */
    public void placeSongOnPlaybackQueue(Song nextSong) {
        if(m_playingQueue.isEmpty() && !isSomethingPlaying()){
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
        Playlist player = new Playlist("Playlist 1");
        player.randomizePlaylist();
        for (Song s : player.getM_songList()) {
            // Add every song in playlist to queue (Is this what you mean?)
            m_playingQueue.add(s);
            // Function to play song s
        }
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
        notifyChangeStateObservers();
    }

    /**
     * Function to resume the current song that is paused.
     */
    public void resume() {
        m_musicPlayer.resumeSong();
        notifyChangeStateObservers();
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

        // On insertion of new song in history set the last played index to be the latest song in history list.
        m_historyIndex = m_songHistory.size();
        if (m_songHistory.size() > MAX_SONG_HISTORY){
            m_songHistory.remove(0);
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

    /**
     * Function to register a playback observer
     * @param observer
     */
    public void registerPlaybackObserver(MusicPlayerObserver observer) {
        m_playbackObservers.add(observer);
    }

    /**
     * Function to notify all observers for playback.
     */
    public void notifyPlaybackObservers(){
        for (MusicPlayerObserver observer : m_playbackObservers){
            observer.updateUI();
        }
    }

    /**
     * Function to get the current playback time of the song being played.
     * @return
     */
    public Duration getCurrentPlayTime(){
        return ((MP3Player) m_musicPlayer).getCurrentPlayTime();
    }

    /**
     * Function to seek the song to the specified time. Time is represented by the percent of the song. Where 1.0 would
     * be the
     * @param percent
     */
    public void seekSongTo(double percent){
        if (percent > 1 || percent < 0){
            assert(false);
        }
        ((MP3Player) m_musicPlayer).seekToTime(percent);

    }

    /**
     * Function to check if the player has a song that is loaded in.
     * @return true if there is a song, false other wise.
     */
    public boolean isSomethingPlaying(){
        return ((null != m_currentSong) && m_musicPlayer.isPlayingSong());
    }

    /**
     * Function to play the previous song in the history.
     */
    public void playPreviousSong(){
        assert(m_songHistory.size() < m_historyIndex);

        if (m_historyIndex == 0){
            // Nothing in history.
            System.out.println("History index is 0");
            return;
        }
        if (!m_songHistory.isEmpty()){
            m_historyIndex--;
            m_musicPlayer.playSong(m_songHistory.get(m_historyIndex));
        }
    }

    /**
     * Function to register observer for notifying when player changes state from play/pause and vice versa.
     * @param observer
     */
    public void registerChangeStateObservers(MusicPlayerObserver observer){
        m_changeStateObserver.add(observer);
    }

    /**
     * Function to notify all the observers for the change state observers.
     */
    public void notifyChangeStateObservers(){
        for (MusicPlayerObserver observer : m_changeStateObserver) {
            observer.updateUI();
        }
    }
}
