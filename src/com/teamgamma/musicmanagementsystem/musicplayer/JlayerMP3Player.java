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
 * Created by Eric on 2016-06-12.
 */
public class JlayerMP3Player implements IMusicPlayer{

    private AdvancedPlayer m_player = null;

    private AudioDevice m_audioDevice = null;

    private boolean m_isReady = false;

    private MusicPlayerManager m_manager;

    private Song m_currentSong;

    private int m_currentPlaybackTimeInMiliseconds = 0;

    private int m_lastFramePlayed = 0;

    private Runnable m_onFinishAction;

    private Thread m_UIUpdateThread;

    // Might requrire mutex for this
    private boolean m_isPlaying = false;

    public JlayerMP3Player(MusicPlayerManager manager){
        m_manager = manager;
    }

    @Override
    public void playSong(Song songToPlay) {
        stopSong();
        setUpMusicPlayer(songToPlay);
        createPlayBackThread().start();
        // Only upon sucess save the song
        m_currentSong = songToPlay;
        m_currentPlaybackTimeInMiliseconds = 0;
        m_manager.notifyNewSongObservers();
    }

    private void setUpMusicPlayer(Song songToPlay) {
        try {
            System.out.println("Trying to play" + songToPlay.getM_fileName());
            FileInputStream fileInputStream = new FileInputStream(songToPlay.getM_file());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            m_audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
            System.out.println("Audio Device is " + m_audioDevice);
            m_player = new AdvancedPlayer(bufferedInputStream, m_audioDevice);
            m_player.setPlayBackListener(createPlaybackListeners());

            m_isReady = true;
        } catch (Exception e) {
            e.printStackTrace();
            m_manager.setError(e);
            m_manager.notifyError();
        }
    }

    private PlaybackListener createPlaybackListeners() {
        return new PlaybackListener() {
            @Override
            public void playbackStarted(PlaybackEvent playbackEvent) {
                super.playbackStarted(playbackEvent);
                m_lastFramePlayed = playbackEvent.getFrame();
                System.out.println("Started playback for " + playbackEvent.getSource().toString());
                System.out.println("the source is " + playbackEvent.getSource());

                m_isPlaying = true;
                System.out.println("Is Playback is " + m_isPlaying);
                m_manager.notifyPlaybackObservers();
                new Thread(createUpdateUIThread()).start();
                m_manager.notifyChangeStateObservers();
                System.out.println("\n\nSong Playback Listner Done \n\n");
            }

            @Override
            public void playbackFinished(PlaybackEvent playbackEvent) {
                // Literally the song is finished playing not when close it called.
                super.playbackFinished(playbackEvent);
                System.out.println("\n\n PLAYBACK FINISHED NOTIFY \n\n");
                System.out.println("Finished Playback for " + playbackEvent.getSource().toString());
                System.out.println("the source is " + playbackEvent.getSource());

                // Frame is the Milisecond precision of the current playback time.
                m_lastFramePlayed = playbackEvent.getFrame();
                m_currentPlaybackTimeInMiliseconds += m_lastFramePlayed;
                System.out.println("Playback Finsish Last played frame is " + m_lastFramePlayed);
                // stop playback UI thread?
                m_isPlaying = false;
                if (m_onFinishAction != null){
                    m_onFinishAction.run();
                }

                m_manager.notifyChangeStateObservers();
            }
        };
    }

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
        System.out.println("Resuming song Request: Starting song at (current Frame)" + m_currentPlaybackTimeInMiliseconds);

        setUpMusicPlayer(m_currentSong);
        createResumePlaybackThread().start();

    }

    private Thread createResumePlaybackThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_player.play((int) convertMilisecondsToFrame(m_currentPlaybackTimeInMiliseconds), Integer.MAX_VALUE);
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
    public Duration getCurrentPlayTime() {
        // Get position returns current time in miliseconds
        return new Duration(m_currentPlaybackTimeInMiliseconds + m_audioDevice.getPosition());
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
                                Thread.sleep(1000);
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


    private long convertMilisecondsToFrame(int miliseconds) {
        double percentOfSongFromMili = (double) miliseconds / (m_currentSong.getM_length() * 1000);
        return Math.round(percentOfSongFromMili * m_currentSong.getM_frames());
    }
}
