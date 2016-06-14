package com.teamgamma.musicmanagementsystem.musicplayer;

import com.teamgamma.musicmanagementsystem.model.Song;
import javafx.concurrent.Task;
import javafx.util.Duration;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * Class to implement a music player that uses the JLayer library
 */
public class JlayerMP3Player implements IMusicPlayer{

    private AdvancedPlayer m_player = null;

    private AudioDevice m_audioDevice = null;

    private boolean m_isReady = false;

    private MusicPlayerManager m_manager;

    private Song m_currentSong;

    private int m_CurrentPlaybackTimeInMiliseconds = 0;

    private int m_lastFramePlayed = 0;

    private Runnable m_onFinishAction;

    // Might require mutex for this
    private boolean m_isPlaying = false;

    /**
     * Constructor
     *
     * @param manager
     */
    public JlayerMP3Player(MusicPlayerManager manager){
        m_manager = manager;
    }

    @Override
    public void playSong(Song songToPlay) {
        stopSong();
        setUpMusicPlayer(songToPlay);
        createPlayBackThread().start();

        // Only upon success save the song
        m_currentSong = songToPlay;
        m_CurrentPlaybackTimeInMiliseconds = 0;
        m_manager.notifyNewSongObservers();
    }

    /**
     * Helper function to setup a new instance of a music player based on the song that is passed in.
     *
     * @param songToPlay The song tha you want to set up the music player for.
     */
    private void setUpMusicPlayer(Song songToPlay) {
        try {
            FileInputStream fileInputStream = new FileInputStream(songToPlay.getM_file());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            m_audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();

            m_player = new AdvancedPlayer(bufferedInputStream, m_audioDevice);
            m_player.setPlayBackListener(createPlaybackListeners());

            m_isReady = true;
        } catch (Exception e) {
            e.printStackTrace();
            m_manager.setError(e);
            m_manager.notifyError();
        }
    }

    /**
     * Helper function to setup the playback lister actions for the JLayer Advance Music Player.
     *
     * @return A class implementing the functionality of the playback listeners.
     */
    private PlaybackListener createPlaybackListeners() {
        return new PlaybackListener() {
            @Override
            public void playbackStarted(PlaybackEvent playbackEvent) {
                super.playbackStarted(playbackEvent);
                m_lastFramePlayed = playbackEvent.getFrame();
                m_isPlaying = true;

                m_manager.notifyPlaybackObservers();
                new Thread(createUpdateUIThread()).start();
                m_manager.notifyChangeStateObservers();

            }

            @Override
            public void playbackFinished(PlaybackEvent playbackEvent) {
                // Literally the song is finished playing not when close it called.
                super.playbackFinished(playbackEvent);

                // Frame is the Milisecond precision of the current playback time.
                m_lastFramePlayed = playbackEvent.getFrame();
                m_CurrentPlaybackTimeInMiliseconds += m_lastFramePlayed;

                // stop playback UI thread
                m_isPlaying = false;

                if (m_onFinishAction != null){
                    m_onFinishAction.run();
                }

                m_manager.notifyChangeStateObservers();
            }
        };
    }

    /**
     * Function to create a playback thread that will play the song from the begginning.
     * @return  A thread that will play the song from the begginning.
     */
    private Thread createPlayBackThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_isPlaying = true;
                    m_player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                    m_manager.setError(e);
                    m_manager.notifyError();
                }
            }
        });
    }

    @Override
    public void pauseSong() {
        m_player.stop();
        m_isPlaying = false;
    }

    @Override
    public void resumeSong() {
        // Play song where it was left off.
        setUpMusicPlayer(m_currentSong);
        createResumePlaybackThread().start();

    }

    /**
     * Function to create a thread and play the song from where it was left off.
     * @return
     */
    private Thread createResumePlaybackThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // using integer max as specified in source of the AdvancePlayer play()
                    m_player.play((int) convertMilisecondsToFrame(m_CurrentPlaybackTimeInMiliseconds), Integer.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                    m_manager.setError(e);
                    m_manager.notifyError();
                }
            }
        });
    }

    @Override
    public void increaseVolume() {

    }

    @Override
    public void decreaseVolume() {

    }

    @Override
    public void repeatSong(boolean repeatSong) {

    }

    @Override
    public void setOnSongFinishAction(Runnable action) {
        m_onFinishAction = action;
    }

    @Override
    public void setOnErrorAction(Runnable action) {

    }

    @Override
    public boolean isPlayingSong() {
        return m_isPlaying;
    }

    @Override
    public boolean isReadyToUse() {
        return m_isReady;
    }

    @Override
    public void seekToTime(double percent) {
        stopSong();
        m_CurrentPlaybackTimeInMiliseconds = (int) Math.round(percent * m_currentSong.getM_length() *
                MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);

        resumeSong();
    }

    @Override
    public Duration getCurrentPlayTime() {
        // Get position returns current time in milliseconds
        return new Duration(m_CurrentPlaybackTimeInMiliseconds + m_audioDevice.getPosition());
    }

    @Override
    public void stopSong() {
        if (isReadyToUse()) {
            m_isPlaying = false;
            m_isReady = false;
            if (m_isPlaying) {
                m_player.stop();
            }

            m_player.close();
        }
    }

    /**
     * Function to create the thread that will notify the playback observer to update the UI.
     *
     * @return  A thread containing the logic to notify the updates.
     */
    private Runnable createUpdateUIThread() {
        return (new Runnable() {
            @Override
            public void run() {
                Task task = new Task<Void>() {
                    @Override
                    public Void call() {
                        while (m_isPlaying) {
                            m_manager.notifyPlaybackObservers();
                            try{
                                Thread.sleep(MusicPlayerConstants.UPDATE_INTERVAL_IN_MILLISECONDS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                };
                task.run();
            }
        });
    }

    /**
     * Helper function to convert from milliseconds to the number of frames based on the current song playing.
     *
     * @param milliseconds   The number of milliseconds to convert.
     *
     * @return The number of frames that is equal to the given milliseconds.
     */
    private long convertMilisecondsToFrame(int milliseconds) {
        double percentOfSongFromMili = (double) milliseconds / (m_currentSong.getM_length() *
                MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);
        return Math.round(percentOfSongFromMili * m_currentSong.getM_frames());
    }
}
