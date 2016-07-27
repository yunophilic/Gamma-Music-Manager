package com.teamgamma.musicmanagementsystem.musicplayer;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Playlist;
import com.teamgamma.musicmanagementsystem.model.Song;

import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Class to manage the the MusicPlayer.
 */
public class MusicPlayerManager {

    private IMusicPlayer m_musicPlayer;

    private List<Song> m_playingQueue;

    private List<Song> m_songHistory;

    private List<MusicPlayerObserver> m_newSongObservers;

    private Song m_currentSong = null;

    private boolean m_repeatPlaylist = false;

    private int m_historyIndex = 0;

    private List<MusicPlayerObserver> m_playbackObservers;

    private List<MusicPlayerObserver> m_changeStateObserver;

    private List<MusicPlayerObserver> m_errorObservers;

    private List<MusicPlayerObserver> m_queuingObserver;

    private Exception m_lastException;

    private double m_volumeLevel = MusicPlayerConstants.MAX_VOLUME;

    private boolean m_isPlayingOnHistory = false;

    private Playlist m_currentPlayList;

    private DatabaseManager m_databaseManager;

    /**
     * Constructor
     */
    public MusicPlayerManager(DatabaseManager databaseManager) {
        m_databaseManager = databaseManager;

        m_playingQueue = new ArrayList<>();

        m_songHistory = new ArrayList<>();

        m_newSongObservers = new ArrayList<>();
        m_playbackObservers = new ArrayList<>();
        m_changeStateObserver = new ArrayList<>();
        m_errorObservers = new ArrayList<>();
        m_queuingObserver = new ArrayList<>();
        m_musicPlayer = new JlayerMP3Player(this);

    }

    /**
     * Function to load the next song in the queue and play it.
     */
    public void playNextSong() {
        System.out.println("Play next song");
        if (isThereNextSongOnHistory() && isPlayingSongOnFromHistoryList()) {
            m_historyIndex++;
            m_currentSong = m_songHistory.get(m_historyIndex);
            m_musicPlayer.playSong(m_currentSong);
        } else if (!m_playingQueue.isEmpty()){
            m_historyIndex = m_songHistory.size() - 1;
            m_isPlayingOnHistory = false;
            Song nextSong = m_playingQueue.get(0);
            m_playingQueue.remove(0);
            playSongRightNow(nextSong);

        } else if (isThereNextSongOnPlaylist()) {
            m_historyIndex = m_songHistory.size() - 1;
            m_isPlayingOnHistory = false;
            playNextSongFromPlaylist();
        }
        notifyChangeStateObservers();
        notifyNewSongObservers();
    }

    /**
     * Function to play the next song from the playlist.
     */
    private void playNextSongFromPlaylist() {
        // Get the current song in the playlist
        m_currentSong = m_currentPlayList.moveToNextSong();
        playSongRightNow(m_currentSong);

    }
    /**
     * Function to play song immediately with out going to the queue.
     *
     * @param songToPlay
     */
    public void playSongRightNow(Song songToPlay) {
        if (m_musicPlayer.isPlayingSong()){
            stopSong();
        }
        m_currentSong = songToPlay;
        m_musicPlayer.playSong(songToPlay);
        updateHistory();
        notifyQueingObserver();
    }

    /**
     * Function will place the song passed in the playback queue. This will play the song immediately if there is not thing
     * in the queue.
     *
     * @param nextSong
     */
    public void placeSongOnBackOfPlaybackQueue(Song nextSong) {
        if (isNoSongPlayingOrNext()) {
            m_currentSong = nextSong;
            m_musicPlayer.playSong(nextSong);
        } else {
            m_playingQueue.add(nextSong);
            m_databaseManager.addToPlaybackQueueTail(nextSong.getFile().getAbsolutePath());
        }

        notifyQueingObserver();
    }

    /**
     * Function to determine if there is a song playing and there is no song that is next.
     *
     * @return
     */
    private boolean isNoSongPlayingOrNext() {
        return m_playingQueue.isEmpty() && !isSomethingPlaying();
    }

    /**
     * Function to add a song to the front of the playback queue.
     *
     * @param songToPlace The song to place in the queue
     */
    public void placeSongAtStartOfQueue(Song songToPlace) {
        m_playingQueue.add(0, songToPlace);
        if (isNoSongPlayingOrNext()){
            playNextSong();
        }
        notifyQueingObserver();
    }
    /**
     * Function to play a playlist.
     *
     * @param playlistToPlay The playlist that we want to play.
     */
    public void playPlaylist(Playlist playlistToPlay) {
        setCurrentPlaylistSongPercentage();

        if (playlistToPlay.getM_songList().isEmpty()){
            m_lastException = new Exception("Cannot play playlist " + playlistToPlay.getM_playlistName() +
                    " because there is no songs in there");
            notifyError();
            return;
        }

        m_currentPlayList = playlistToPlay;
        m_currentSong = m_currentPlayList.isThereASongLoadedInPlaylist() ? m_currentPlayList.getCurrentSong() : m_currentPlayList.moveToNextSong();

        m_isPlayingOnHistory = false;

        playSongRightNow(m_currentSong);
    }

    /**
     * Function to set if the user wanted to repeat the current song playing.
     *
     * @param repeatSong
     */
    public void setRepeat(boolean repeatSong) {
        m_repeatPlaylist = repeatSong;

        notifyQueingObserver();
    }

    /**
     * Function to pause the current song playing.
     */
    public void pause() {
        if (m_musicPlayer.isReadyToUse()) {
            m_musicPlayer.pauseSong();
            notifyChangeStateObservers();
        }

    }

    /**
     * Function to resume the current song that is paused.
     */
    public void resume() {
        if (m_musicPlayer.isReadyToUse()) {
            m_musicPlayer.resumeSong();
            notifyChangeStateObservers();
        }
    }

    /**
     * Function to add a observer to the list of observers that are notified when a new song is being played.
     *
     * @param observer
     */
    public void registerNewSongObserver(MusicPlayerObserver observer) {
        m_newSongObservers.add(observer);
    }

    /**
     * Function to get the current song playing in the player.
     *
     * @return The current song loaded in the player or Null if there is none.
     */
    public Song getCurrentSongPlaying() {
        return m_currentSong;
    }

    /**
     * Function to get the end time of the song from the player.
     *
     * @return The end time of the song. Or 0 if there is no song loaded.
     */
    public Duration getEndTime() {
        if (m_currentSong == null) {
            return new Duration(0);
        }
        return new Duration(m_currentSong.getM_length() * MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);
    }

    /**
     * Function to update the history of the music player.
     */
    private void updateHistory() {
        if (null == m_currentSong) {
            return;
        }
        // Set the index to the last element to the newest element in the list.
        m_historyIndex = m_songHistory.size() - 1;

        if (!m_songHistory.isEmpty()) {
            // Check to see if the current song is also the last played song in history
            if (m_currentSong == m_songHistory.get(m_historyIndex)) {
                // Do not add the same song in the history consecutively
                return;
            }
        }
        m_songHistory.add(m_currentSong);
        m_databaseManager.addToHistory(m_currentSong.getFile().getAbsolutePath());

        // On insertion of new song in history set the last played index to be the latest song in history list.
        m_historyIndex = m_songHistory.size() - 1;
        m_isPlayingOnHistory = false;
        if (m_songHistory.size() > MusicPlayerConstants.MAX_SONG_HISTORY) {
            m_songHistory.remove(0);
        }
    }

    /**
     * Function to notify all observers that a new song has been loaded.
     */
    public void notifyNewSongObservers() {
        notifyAll(m_newSongObservers);
    }

    /**
     * Function to register a playback observer
     *
     * @param observer
     */
    public void registerPlaybackObserver(MusicPlayerObserver observer) {
        m_playbackObservers.add(observer);
    }

    /**
     * Function to notify all observers for playback.
     */
    public void notifyPlaybackObservers() {
        notifyAll(m_playbackObservers);
    }

    /**
     * Function to get the current playback time of the song being played.
     *
     * @return The current playback time.
     */
    public Duration getCurrentPlayTime() {
        if (m_currentSong == null) {
            return new Duration(0);
        }
        return  m_musicPlayer.getCurrentPlayTime();
    }

    /**
     * Function to seek the song to the specified time. Time is represented by the percent of the song. Where 1.0 would
     * be the end of the song and 0 the start.
     *
     * @param percent
     */
    public void seekSongTo(double percent) {
        if (percent > 1 || percent < 0) {
            assert (false);
        }
        if (m_musicPlayer.isReadyToUse()) {
            m_musicPlayer.seekToTime(percent);
        }
    }

    /**
     * Function to check if the player has a song that is loaded in.
     *
     * @return true if there is a song, false other wise.
     */
    public boolean isSomethingPlaying() {
        return ((null != m_currentSong) && m_musicPlayer.isPlayingSong());
    }

    /**
     * Function to play the previous song in the history. Does not affect history by adding currently playing song in history.
     */
    public void playPreviousSong() {
        assert (m_songHistory.size() < m_historyIndex);
        if (getCurrentPlayTime().greaterThan(new Duration(MusicPlayerConstants.FIVE_SECONDS_IN_MILLISECONDS))) {
            m_musicPlayer.playSong(m_currentSong);
        } else {
            if (!m_songHistory.isEmpty()) {
                m_isPlayingOnHistory = true;
                if (m_historyIndex != 0 && (m_currentSong != null)) {
                    m_historyIndex--;
                }
                m_currentSong = m_songHistory.get(m_historyIndex);
                m_musicPlayer.playSong(m_currentSong);
            } else {
                // Set to inital state of the player
                m_currentSong = null;
                notifyNewSongObservers();
                notifyPlaybackObservers();
            }
        }
    }

    /**
     * Function to modify song rating.
     */
    public void setRating(int rating) {
        m_currentSong.setRating(rating);
        notifyChangeStateObservers();
    }

    /**
     * Function to register observer for notifying when player changes state from play/pause and vice versa.
     *
     * @param observer
     */
    public void registerChangeStateObservers(MusicPlayerObserver observer) {
        m_changeStateObserver.add(observer);
    }

    /**
     * Function to notify all the observers for the change state observers.
     */
    public void notifyChangeStateObservers() {
        notifyAll(m_changeStateObserver);
    }

    /**
     * Function to register observers that need to be notified when an error occurs.
     *
     * @param observer The observer to register.
     */
    public void registerErrorObservers(MusicPlayerObserver observer) {
        m_errorObservers.add(observer);
    }

    /**
     * Function to notify all observers watching for errors.
     */
    public void notifyError() {
        // Check first to see if we can recover.
        if (m_lastException instanceof FileNotFoundException){
            removeSongFromHistory(m_currentSong);
            playPreviousSong();
        }
        notifyAll(m_errorObservers);
    }

    /**
     * Function to get the exception that was thrown.
     *
     * @return The exception that was thrown.
     */
    public Exception getError() {
        return m_lastException;
    }

    /**
     * Function to set the last exception that was thrown by either MP3Player or MusicPlayerManager.
     *
     * @param e The exception to save.
     */
    public void setError(Exception e) {
        m_lastException = e;
    }

    /**
     * Helper function to notify all the observers in a list.
     *
     * @param observers List of observers to iterate through.
     */
    private void notifyAll(List<MusicPlayerObserver> observers) {
        for (MusicPlayerObserver observer : observers) {
            observer.updateUI();
        }
    }

    /**
     * Function to determine if the player is playing of the history list.
     *
     * @return True if the player is playing off the history list, False other wise.
     */
    public boolean isPlayingSongOnFromHistoryList() {
        return (m_isPlayingOnHistory);
    }

    /**
     * Function to stop song from playing.
     */
    public void stopSong(){
        m_musicPlayer.stopSong();
    }

    /**
     * Function to remove the current song from history
     */
    private void removeSongFromHistory() {
        assert (m_currentSong == null);

        int songHistorySize = m_songHistory.size();
        for (int songIndex = 0; songIndex < songHistorySize; ++songIndex) {
            if (m_songHistory.get(songIndex).getFile().getAbsolutePath().equals(
                    m_currentSong.getFile().getAbsolutePath())) {

                m_songHistory.remove(songIndex);
                if (songIndex == 0 || m_songHistory.isEmpty()){
                    m_historyIndex = 0;
                } else if (songIndex < m_songHistory.size() - 1) {
                    // Move to next song oldest song if allowed
                    m_historyIndex++;
                } else {
                    m_historyIndex--;
                }

                if (!m_songHistory.isEmpty()){
                    // If possible load the previous song from the history.
                    m_currentSong = m_songHistory.get(m_historyIndex);
                    playPreviousSong();
                }
                return;
            }
        }
    }

    /**
     * Function to get the current volume level.
     * @return  The current volume level
     */
    public double getCurrentVolumeLevel() {
        return m_volumeLevel;
    }

    /**
     * Function to set the volume using Java Sound API.
     * Based on http://stackoverflow.com/questions/648107/changing-volume-in-java-when-using-jlayer
     */
    private void setVolumeControl() {
        try {
            Port speakerPort = (Port) AudioSystem.getLine(Port.Info.SPEAKER);
            speakerPort.open();

            FloatControl volCtrl = (FloatControl) speakerPort.getControl(
                    FloatControl.Type.VOLUME);

            volCtrl.setValue((float) m_volumeLevel);

        } catch (Exception e) {
            m_lastException = e;
            notifyError();
        }
    }

    /**
     * Function to get the history of songs that have been played in the music player
     *
     * @return  The list of songs representing the history.
     */
    public List<Song> getHistory() {
        return m_songHistory;
    }

    /**
     * Function to get all songs in the playing Queue.
     *
     * @return The songs that are in the playing queue.
     */
    public Collection<Song> getPlayingQueue() {
        return m_playingQueue;
    }

    /**
     * Helper fucntion to reterieve what ever is in the playback queue in the DB and set it to be what is in the playback queue.
     */
    public void loadPlaybackQueue(List<Song> songs) {
        m_playingQueue.addAll(songs);
    }

    /**
     * Function to load songs that are from the history that is retrieved from the DB.
     */
    public void loadHistory(List<Song> songs) {
        m_songHistory.addAll(songs);

        // TODO: Will have to set the history index to actual location that was left off
        if (!m_songHistory.isEmpty()) {
            m_historyIndex = m_songHistory.size() - 1;
        }
    }

    /**
     * Function to register a observer for when there is an update to the playing queue.
     * @param observer
     */
    public void registerQueingObserver(MusicPlayerObserver observer) {
        m_queuingObserver.add(observer);
    }

    /**
     * Function to notify the observers watching for updates to the playback queeu.
     */
    public void notifyQueingObserver() {
        notifyAll(m_queuingObserver);
    }

    /**
     * Function to check if there is something if previous song can be done.
     *
     * @return True if there is a song that can be previously played, False other wise.
     */
    public boolean isNothingPrevious(){
        return m_songHistory.isEmpty();
    }

    /**
     * Function to check if there is a next song to play.
     *
     * @return True if there is, False other wise.
     */
    public boolean isThereANextSong(){
        return (isThereNextSongOnPlaylist() ||
                !m_playingQueue.isEmpty() ||
                (isThereNextSongOnHistory()));
    }

    /**
     * Function to get the history index in the manager.
     *
     * @return The index for the history data structure.
     */
    public int getM_historyIndex() {
        return m_historyIndex;
    }

    /**
     * Function to set the Volume of the music player.
     * 
     * @param volumeLevel The volume to set it at.
     */
    public void setVolumeLevel(double volumeLevel) {
        m_volumeLevel = volumeLevel;
        setVolumeControl();
    }

    /**
     * Function to remove a song given as the parameter from all instances in the history
     *
     * @param songToDelete The Song to delete.
     */
    public void removeSongFromHistory(Song songToDelete){
        while (m_songHistory.contains(songToDelete)){
            if (m_songHistory.indexOf(songToDelete) < m_historyIndex) {
                // Decrement the index if we are removing something ahead of the current index.
                m_historyIndex--;
            }
            m_songHistory.remove(songToDelete);
        }
        m_databaseManager.deleteFromHistory(songToDelete.getFile().getAbsolutePath());
    }

    /**
     * Function to play a song from the history based on the index position it is in.
     *
     * @param index The index of the song to play.
     */
    public void playSongFromHistory(int index){
        assert(index > 0 && index < m_songHistory.size());

        m_musicPlayer.stopSong();

        m_isPlayingOnHistory = true;
        m_historyIndex = index;
        m_currentSong = m_songHistory.get(m_historyIndex);
        m_musicPlayer.playSong(m_currentSong);
    }

    /**
     * Function to get the next song that will be played if you hit play next song.
     *
     * @return  The next song to be played.
     */
    public Song getNextSong() {
        if (isThereNextSongOnHistory()) {
            return m_songHistory.get(m_historyIndex + 1);
        }
        if (!m_playingQueue.isEmpty()) {
            return m_playingQueue.get(0);
        } else if (isThereNextSongOnPlaylist()){
            return m_currentPlayList.getNextSong();
        }

        return null;
    }

    /**
     * Function to tell you if there is a song to be played next on the current playlist.
     *
     * @return If there is a song next on the playlist.
     */
    private boolean isThereNextSongOnPlaylist() {
        if (m_currentPlayList != null) {
            if (!m_repeatPlaylist && m_currentPlayList.isLastSongInPlaylist()){
                return false;
            } else {
                return true;
            }
        }
        return false;

    }

    /**
     * Function to tell you if there is a song next on the song history list.
     *
     * @return If there is a song that can be played next from the history list.
     */
    private boolean isThereNextSongOnHistory() {
        return m_isPlayingOnHistory && m_historyIndex < m_songHistory.size() - 1;
    }

    /**
     * Function to get the previous song in the player.
     *
     * @return The previous song in the player or the current song that is playing if there is no other song.
     */
    public Song getPreviousSong() {
        if (isPlayingSongOnFromHistoryList()) {
            if (m_historyIndex == 0) {
                return m_currentSong;
            } else {
                return m_songHistory.get(m_historyIndex - 1);
            }
        }
        if (!m_songHistory.isEmpty()){
            if (m_songHistory.size() == 1){
                return m_songHistory.get(0);
            } else if (m_currentSong == null) {
                return m_songHistory.get(m_historyIndex);
            } else {
                return m_songHistory.get(m_historyIndex - 1);
            }
        }

        return m_currentSong;
    }

    /**
     * Function to unload a playlist.
     */
    public void resetCurrentPlaylist() {
        m_currentPlayList = null;
    }

    /**
     * Function to unload a song from the music player manager. Should set the player back to an inital state.
     */
    public void unloadSong() {
        m_currentSong = null;
    }

    /**
     * Function to get the current song in the playlist that is loaded.
     *
     * @return The song that is in the playlist or null if there is no playlist loaded.
     */
    public Song getCurrentPlaylistSong(){
        if (m_currentPlayList != null) {
            return m_currentPlayList.getCurrentSong();
        }
        return null;
    }

    /**
     * Function to get the index of the current song in the playlist that is loaded
     *
     * @return  The index of the current song or -1 if there is no song loaded.
     */
    public int getCurrentIndexOfPlaylistSong(){
        if (m_currentPlayList != null) {
            return m_currentPlayList.getM_currentSongIndex();
        }
        return -1;
    }

    /**
     * Function to remove a song from the playback queue based on the index of its location.
     * @param index
     */
    public void removeSongFromPlaybackQueue(int index){
        if (m_playingQueue.size() > index) {
            m_playingQueue.remove(index);
        }
        notifyQueingObserver();
    }

    /**
     * Set the resume time for current playlist
     */
    public void setCurrentPlaylistSongPercentage() {
        if (m_currentPlayList != null) {
            //pause();
            double percentage = getCurrentPlayTime().toMillis() / getEndTime().toMillis();
            m_currentPlayList.setM_songResumeTime(percentage);
        }
    }

    /**
     * Get the resume time for this playlist
     *
     * @return the percentage (resume time) of the current playlist
     */
    public double getCurrentPlaylistResumeTime() {
        double percentage = m_databaseManager.getResumeTime(m_currentPlayList.getM_playlistName());
        return percentage;
    }

    /**
     * Check if there is a resume time stored for this playlist already
     *
     * @return boolean indicates whether if there's a time stored for this playlist
     */
    public boolean isThereResumeTime() {
        double percentage = getCurrentPlaylistResumeTime();
        if (percentage == 0.0) {
            return false;
        }
        return true;
    }

    /**
     * Get the name of the first playlist in the database Playlist table
     *
     * @return the name of the first playlist
     */
    public String getFirstPlaylistName() {
        String firstPlaylistName = m_databaseManager.getFirstPlaylistName();
        return firstPlaylistName;
    }

    /**
     * Set the first playlist from the database to be the current playlist
     *
     * @param firstPlaylist, the first playlist
     */
    public void loadFirstPlaylist(Playlist firstPlaylist) {
        m_currentPlayList = firstPlaylist;
    }
}
