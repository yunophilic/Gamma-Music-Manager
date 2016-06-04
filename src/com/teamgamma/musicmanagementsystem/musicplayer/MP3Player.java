package com.teamgamma.musicmanagementsystem.musicplayer;

import com.teamgamma.musicmanagementsystem.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Class to play a MP3 using JavaFX MediaPlayer class.
 */
public class MP3Player implements IMusicPlayer {

    // Constants for volume control
    public static final double VOLUME_CHANGE = 0.1;
    public static final double MAX_VOLUME = 1.0;
    public static final int MIN_VOLUME = 0;
    public static final int UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    private Song m_currentSong;

    private MediaPlayer m_player;

    private MusicPlayerManager m_manager;

    private boolean m_repeatFlag = false;

    private Thread m_updateWorker;

    private Runnable m_onErrorAction;

    // For testing/experimenting.
    public double m_counter = 0;

    public MP3Player(MusicPlayerManager manager){
        m_manager = manager;
    }

    @Override
    public void playSong(Song songToPlay) {
        m_currentSong = songToPlay;

        // Stop any player if there is one going.
        if (null != m_player) {
            m_player.dispose();
        }

        try {
            setupMusicPlayer(songToPlay);
        } catch (Exception e) {
            m_manager.setError(e);
            m_manager.notifyError();
            return;
        }

        m_player.play();
        m_manager.notifyChangeStateObservers();
    }

    private void setupMusicPlayer(Song songToPlay) {
        m_player = new MediaPlayer(new Media(songToPlay.getM_file().toURI().toString()));
        repeatSong(m_repeatFlag);
        m_player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                m_manager.playNextSong();
            }
        });

        m_player.setOnReady(new Runnable() {
            @Override
            public void run() {
                // Need to notify observers when the player is ready so the data given will be correct.
                m_manager.notifyNewSongObservers();
            }
        });

        m_player.setOnPlaying(createUpdateUIThread());

        // Put on repeat as well as the thread terminates upon a single play through.
        m_player.setOnRepeat(createUpdateUIThread());

        m_player.setOnError(m_onErrorAction);
    }

    @Override
    public void pauseSong() {
        System.out.println("Song is paused at " + m_player.getCurrentTime().toMillis());
        m_player.pause();
    }

    @Override
    public void resumeSong() {
        m_player.play();
    }

    @Override
    public void increaseVolume() {
        double currentVolume = m_player.getVolume();
        if (currentVolume < MAX_VOLUME) {
            currentVolume += VOLUME_CHANGE;
        }
        m_player.setVolume(currentVolume);
    }

    @Override
    public void decreaseVolume() {
        double currentVolume = m_player.getVolume();
        if (currentVolume > MIN_VOLUME) {
            currentVolume -= VOLUME_CHANGE;
        }
        m_player.setVolume(currentVolume);
    }

    @Override
    public void repeatSong(boolean repeatSong) {
        m_repeatFlag = repeatSong;
        if (repeatSong) {
            m_player.setCycleCount(MediaPlayer.INDEFINITE);
        }
        else {
            m_player.setCycleCount(1);

        }
    }

    @Override
    public void setOnSongFinishAction(Runnable action) {
        m_player.setOnEndOfMedia(action);
    }

    @Override
    public void setOnErrorAction(Runnable action) {
        m_onErrorAction = action;
        if (m_player != null){
            m_player.setOnError(action);
        }

    }

    @Override
    public boolean isPlayingSong() {
        return (m_player.getStatus() == MediaPlayer.Status.PLAYING);
    }

    public MediaPlayer getMusicPlayer() {
        return m_player;
    }

    public Duration getEndTime() {
        return m_player.getMedia().getDuration();
    }

    public Duration getCurrentPlayTime() {
        return m_player.getCurrentTime();
    }

    private Runnable createUpdateUIThread() {
        return (new Runnable() {
            @Override
            public void run() {
                // Create a new thread for updating slider/progress bar.
                Thread updateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(m_player.getStatus() == MediaPlayer.Status.PLAYING
                                && m_player.getCurrentTime().toMillis() != m_player.getMedia().getDuration().toMillis()){

                            m_manager.notifyPlaybackObservers();
                            /*System.out.println( "The End of the song is at " +
                                    m_player.getMedia().getDuration().toMillis() + " Current time before player is " +
                                    m_player.getCurrentTime().toMillis() + " Internal counter test "+ m_counter);
                            */
                            try{
                                // 1s per update
                                Thread.sleep(UPDATE_INTERVAL_IN_MILLISECONDS);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            m_counter += 1000;
                        }
                    }
                });

                if (m_updateWorker == null || !m_updateWorker.isAlive()){
                    m_updateWorker = updateThread;
                    m_updateWorker.start();
                }
            }
        });
    }

    /**
     * Function to seek to the given percentage given in parameter.
     *
     * @param percent
     */
    public void seekToTime(double percent) {
        // JavaFx seek might not be good enough
        // http://stackoverflow.com/questions/32411181/javafx-mediaplayer-highly-inaccurate-seeking

        double seekTime = m_player.getMedia().getDuration().toMillis() * percent;
        System.out.println("Precent given is " + percent + " Seektime is " + seekTime + " The End of the song is at " +
        m_player.getMedia().getDuration().toMillis() + " Current time before player is " + m_player.getCurrentTime().toMillis());
        Duration newTime = new Duration(seekTime);

        //boolean createNewUpdateThread = false;
//        if (m_player.getCurrentTime().toMillis() ==  m_player.getStopTime().toMillis()) {
//            // Need to start an update thread becuase the thread has already stopped.
//            createNewUpdateThread = true;
//        }
        m_player.stop();

        m_player.dispose();
        setupMusicPlayer(m_currentSong);
        m_counter = seekTime;
        m_player.setStartTime(newTime);
        m_player.play();
//        if (createNewUpdateThread){
//            new Thread(createUpdateUIThread()).start();
//        }
    }

}
