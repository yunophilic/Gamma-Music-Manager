package com.teamgamma.musicmanagementsystem;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Class to play a MP3 using JavaFX MediaPlayer class.
 */
public class MP3Player implements IMusicPlayer{

    // Constants for volumn control
    public static final double VOLUME_CHANGE = 0.1;
    public static final double MAX_VOLUME = 1.0;
    public static final int MIN_VOLUME = 0;

    private Song m_currentSong;

    private MediaPlayer m_player;

    private MusicPlayerManager m_manager;

    public MP3Player(MusicPlayerManager manager){
        m_manager = manager;
    }

    @Override
    public void playSong(Song songToPlay) {
        m_currentSong = songToPlay;
        m_player = new MediaPlayer(new Media(songToPlay.getM_file().toURI().toString()));
        m_player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                m_manager.moveToNextSong();
            }
        });

        m_player.play();

    }

    @Override
    public void pauseSong() {
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
    public void decreaseVolume(){
        double currentVolume = m_player.getVolume();
        if (currentVolume > MIN_VOLUME) {
            currentVolume -= VOLUME_CHANGE;
        }
        m_player.setVolume(currentVolume);
    }

    @Override
    public void repeatSong(boolean repeatSong){
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
        m_player.setOnError(action);
    }

    public MediaPlayer getMusicPlayer(){
        return m_player;
    }


}
