package com.teamgamma.musicmanagementsystem.musicplayer;

import com.teamgamma.musicmanagementsystem.model.Playlist;
import com.teamgamma.musicmanagementsystem.model.Song;

import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class to manage the the MusicPlayer.
 */
public class MusicPlayerManager {

    private IMusicPlayer m_musicPlayer;

    private Queue<Song> m_playingQueue;

    private List<Song> m_songHistory;

    private List<MusicPlayerObserver> m_newSongObservers;

    private Song m_currentSong = null;

    private boolean m_repeatSong = false;

    private int m_historyIndex = 0;

    private List<MusicPlayerObserver> m_playbackObservers;

    private List<MusicPlayerObserver> m_changeStateObserver;

    private List<MusicPlayerObserver> m_errorObservers;

    private List<MusicPlayerObserver> m_queuingObserver;

    private Exception m_lastException;

    // Default value is 1 in JavaFX Media Player
    private double m_volumeLevel = 1.0;

    /**
     * Constructor
     */
    public MusicPlayerManager() {
        m_playingQueue = new ConcurrentLinkedQueue<Song>();
        m_songHistory = new ArrayList<Song>();

        m_newSongObservers = new ArrayList<MusicPlayerObserver>();
        m_playbackObservers = new ArrayList<MusicPlayerObserver>();
        m_changeStateObserver = new ArrayList<MusicPlayerObserver>();
        m_errorObservers = new ArrayList<MusicPlayerObserver>();
        m_queuingObserver = new ArrayList<MusicPlayerObserver>();
        m_musicPlayer = new JlayerMP3Player(this);
    }

    /**
     * Function to load the next song in the queue and play it.
     */
    public void playNextSong() {
        System.out.println("Play next song");
        if (isPlayingSongOnFromHistoryList() && m_historyIndex < m_songHistory.size() - 1) {
            if (m_repeatSong) {
                m_musicPlayer.playSong(m_currentSong);
            } else {
                m_historyIndex++;
                m_currentSong = m_songHistory.get(m_historyIndex);
                m_musicPlayer.playSong(m_currentSong);
            }
        } else {
            Song nextSong = m_playingQueue.poll();
            System.out.println("The next song is " + nextSong);
            if (null == nextSong) {
                // No more songs to play.
                if (m_repeatSong) {
                    m_musicPlayer.playSong(m_currentSong);
                }
            } else {
                playSongRightNow(nextSong);
            }
        }
    }

    /**
     * Function to play song immidiatly with out going to the queue.
     *
     * @param songToPlay
     */
    public void playSongRightNow(Song songToPlay) {
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
    public void placeSongOnPlaybackQueue(Song nextSong) {
        if (m_playingQueue.isEmpty() && !isSomethingPlaying()) {
            m_musicPlayer.playSong(nextSong);
            m_currentSong = nextSong;
        } else {
            // TODO: Make it move to front by changing prioirty.
            m_playingQueue.add(nextSong);
        }
        notifyQueingObserver();

    }

    /**
     * Function to play a playlist.
     * TODO: Revisit after playlist implementation.
     */
    public void playPlaylist() {
        // Method should add in playlist to queue.
        Playlist player = new Playlist("Playlist 1");
        player.shufflePlaylist();
        for (Song s : player.getM_songList()) {
            // Add every song in playlist to queue (Is this what you mean?)
            m_playingQueue.add(s);
            // Function to play song s
        }
    }

    /**
     * Function to set if the user wanted to repeat the current song playing.
     *
     * @param repeatSong
     */
    public void setRepeat(boolean repeatSong) {
        m_repeatSong = repeatSong;
        if (m_musicPlayer.isReadyToUse()) {
            m_musicPlayer.repeatSong(repeatSong);
        }
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
     * Function to increase the volume.
     */
    public void increaseVolume() {
        if (m_volumeLevel < MusicPlayerConstants.MAX_VOLUME){
            m_volumeLevel += MusicPlayerConstants.VOLUME_CHANGE;
        }
        if (m_musicPlayer.isReadyToUse()) {
            m_musicPlayer.increaseVolume();
        }
        setVolumeControl();
    }

    /**
     * Function to decrease the volume.
     */
    public void decreaseVolume() {
        if (m_volumeLevel > MusicPlayerConstants.MIN_VOLUME) {
            m_volumeLevel -= MusicPlayerConstants.VOLUME_CHANGE;
        }
        if (m_musicPlayer.isReadyToUse()) {
            m_musicPlayer.decreaseVolume();
        }
        setVolumeControl();
    }

    /**
     * Function to get the JavaFX Media player for the Media View.
     *
     * @return The FX MediaPlayer if the underlying player uses it or null if it does not.
     */
    public MediaPlayer getMediaPlayer() {
        if (m_musicPlayer instanceof MP3Player) {
            return ((MP3Player) m_musicPlayer).getMusicPlayer();
        } else {
            return null;
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
     * @return The end time of the song.
     */
    public Duration getEndTime() {
        return new Duration(m_currentSong.getM_length() * MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);
    }

    /**
     * Function to update the history of the music player.
     */
    private void updateHistory() {
        if (null == m_currentSong) {
            return;
        }
        if (isPlayingSongOnFromHistoryList()) {
            // Reset to be the last index as someone as we need to add something to the back of the list
            m_historyIndex = m_songHistory.size() - 1;
        }
        if (!m_songHistory.isEmpty()) {
            // Check to see if the current song is also the last played song in history
            if (m_currentSong == m_songHistory.get(m_historyIndex)) {
                // Do not add the same song in the history consecutively
                return;
            }
        }
        m_songHistory.add(m_currentSong);

        // On insertion of new song in history set the last played index to be the latest song in history list.
        m_historyIndex = m_songHistory.size() - 1;
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
     * Function to play the previous song in the history. Does not affect history.
     */
    public void playPreviousSong() {
        assert (m_songHistory.size() < m_historyIndex);
        if (m_repeatSong) {
            // Just restart current song.
            m_musicPlayer.playSong(m_currentSong);
            return;
        }

        if (m_historyIndex == 0) {
            // Nothing in history restart current song
            m_musicPlayer.playSong(m_currentSong);
            return;
        }
        if (!m_songHistory.isEmpty()) {
            m_historyIndex--;
            m_currentSong = m_songHistory.get(m_historyIndex);
            m_musicPlayer.playSong(m_currentSong);
        }
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
        if (m_lastException instanceof MediaException){
            MediaException exception = (MediaException) m_lastException;
            if (exception.getType() == MediaException.Type.MEDIA_UNAVAILABLE){
                removeSongFromHistory();
            }
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
    private boolean isPlayingSongOnFromHistoryList() {
        return ((m_historyIndex != (m_songHistory.size() - 1)) && !m_songHistory.isEmpty());
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
            if (m_songHistory.get(songIndex).getM_file().getAbsolutePath().equals(
                    m_currentSong.getM_file().getAbsolutePath())) {

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
     * WIP
     * Function to set the volume using Java Sound API.
     */
    private void setVolumeControl() {
        Port lineIn;
        FloatControl volCtrl;
        try {
            Mixer mixer = AudioSystem.getMixer(null);
            lineIn = (Port)mixer.getLine(Port.Info.SPEAKER);
            lineIn.open();
            volCtrl = (FloatControl) lineIn.getControl(
                    FloatControl.Type.VOLUME);

            volCtrl.setValue((float) m_volumeLevel);
            // Assuming getControl call succeeds,
            // we now have our LINE_IN VOLUME control.
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed trying to find LINE_IN"
                    + " VOLUME control: exception = " + e);
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
}
