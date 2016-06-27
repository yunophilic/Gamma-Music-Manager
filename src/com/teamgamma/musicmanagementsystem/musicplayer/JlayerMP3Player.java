package com.teamgamma.musicmanagementsystem.musicplayer;

import com.teamgamma.musicmanagementsystem.model.Song;

import javafx.concurrent.Task;
import javafx.util.Duration;

import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private boolean m_isPlaying = false;

    private Lock m_lock = new ReentrantLock();

    private Thread m_currentUIThread;

    private Thread m_currentPlaybackThread;

    private BufferedInputStream m_bufferedStream;

    private FileInputStream m_fs;

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
        try {
            setUpMusicPlayer(songToPlay);
        } catch (Exception e) {
            return;
        }
        m_currentPlaybackThread = createPlayBackThread();
        m_currentPlaybackThread.start();

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
    private void setUpMusicPlayer(Song songToPlay) throws Exception{
        try {
            m_fs = new FileInputStream(songToPlay.getM_file());
            m_bufferedStream = new BufferedInputStream(m_fs);
            m_audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();

            m_player = new AdvancedPlayer(m_bufferedStream, m_audioDevice);
            m_player.setPlayBackListener(createPlaybackListeners());

            m_isReady = true;
        } catch (Exception e) {
            e.printStackTrace();
            m_manager.setError(e);
            m_manager.notifyError();
            throw e;
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
                // Wait for the current UI thread to stop if it is still running.
                try {
                    m_lock.lock();
                    m_isPlaying = false;
                    if (m_currentUIThread != null) {
                        // Spawn a new thread to join it so we do not get any UI hangs.
                        createTerminationThread(m_currentUIThread).start();
                    }
                    m_isPlaying = true;

                    // Initial notify
                    m_manager.notifyPlaybackObservers();
                    m_currentUIThread = new Thread(createUpdateUIThread());
                    if (m_currentUIThread.getState() == Thread.State.NEW) {
                        m_currentUIThread.start();
                    }

                    m_manager.notifyChangeStateObservers();
                } finally {
                    m_lock.unlock();
                }
            }

            @Override
            public void playbackFinished(PlaybackEvent playbackEvent) {
                super.playbackFinished(playbackEvent);

                // Frame is the Millisecond precision of the current playback time and not the MP3 frame.
                m_lastFramePlayed = playbackEvent.getFrame();
                m_CurrentPlaybackTimeInMiliseconds += m_lastFramePlayed;

                // Stop playback UI thread
                m_isPlaying = false;

                if (m_onFinishAction != null){
                    m_onFinishAction.run();
                }

                m_manager.notifyChangeStateObservers();
            }
        };
    }

    /**
     * Helper function to create a thread with the logic to interrupt and join the thread given.
     *
     * @param threadToTerminate The thread to terminate
     *
     * @return  A Thread that can be used to terminate the thread in the parameter.
     */
    private Thread createTerminationThread(Thread threadToTerminate) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    threadToTerminate.interrupt();
                    threadToTerminate.join();
                } catch (InterruptedException e) {
                    m_manager.setError(e);
                    m_manager.notifyError();
                }
            }
        });
    }

    /**
     * Function to create a playback thread that will play the song from the beginning.
     * @return  A thread that will play the song from the beginning.
     */
    private Thread createPlayBackThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_isPlaying = true;
                    m_player.play();
                } catch (BitstreamException bistreamError) {
                    // Ignore since this exception would be for when we want to interrupt the music player so we can
                    // the thread.
                } catch (ArrayIndexOutOfBoundsException arrayOutOfBounds) {
                    // Need to reset the player as we hit the 0.01% case that this exception happen as said in their
                    // documentation
                    arrayOutOfBounds.printStackTrace();
                    stopSong();
                    m_manager.resume();
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
        try {
            setUpMusicPlayer(m_currentSong);
        } catch (Exception e) {
            return;
        }
        m_currentPlaybackThread = createResumePlaybackThread();
        m_currentPlaybackThread.start();
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
                    // Using integer max as specified in source of the AdvancePlayer play()
                    m_player.play((int) convertMilisecondsToFrame(m_CurrentPlaybackTimeInMiliseconds), Integer.MAX_VALUE);
                } catch (BitstreamException bistreamError) {
                    // Ignore since this exception would be for when we want to interrupt the music player so we can
                    // the thread.
                } catch (ArrayIndexOutOfBoundsException arrayOutOfBounds) {
                    // Need to reset the player as we hit the 0.01% case that this exception happen as said in their
                    // documentation
                    arrayOutOfBounds.printStackTrace();
                    stopSong();
                    m_manager.resume();
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
            m_isReady = false;

            // This should kill the UI threads if they are not waiting.
            m_isPlaying = false;

            // This should kill the current playback thread.
            if (m_currentPlaybackThread != null && m_currentPlaybackThread.isAlive()){

                m_player.stop();
                m_player.close();
                try {
                    m_fs.close();
                    m_bufferedStream.close();
                } catch (Exception e) {
                    m_manager.setError(e);
                    m_manager.notifyError();
                }

                createTerminationThread(m_currentPlaybackThread).start();
            }
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
                                // It is alright if we get interrupted while we sleep.
                                // Make sure to return and stop the thread.
                                return null;
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
