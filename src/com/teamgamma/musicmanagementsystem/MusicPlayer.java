package com.teamgamma.musicmanagementsystem;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Class to play a song.
 */
public class MusicPlayer {

    public static final double VOLUME_CHANGE = 0.1;
    public static final double MAX_VOLUME = 1.0;
    public static final int MIN_VOLUME = 0;

    private Song m_currentSong;

    private MediaPlayer m_player;

    private MusicPlayerManager m_manager;

    public MusicPlayer(MusicPlayerManager manager){
        m_manager = manager;

    }

    public void playSong(Song songToPlay){
        // Get the file from the song
        m_player = new MediaPlayer(new Media(songToPlay.getM_file().toURI().toString()));
        m_player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                m_manager.moveToNextSong();
            }
        });
        // Determine what type of audio file.
        m_player.play();

    }

    public void pauseSong(){
        m_player.pause();
    }

    public void resumeSong(){
        m_player.play();
    }

    public void increaseVolume(){
        double currentVolume = m_player.getVolume();
        if (currentVolume < MAX_VOLUME) {
            currentVolume += VOLUME_CHANGE;
        }
        m_player.setVolume(currentVolume);
    }

    public void decreaseVolume(){
        double currentVolume = m_player.getVolume();
        if (currentVolume > MIN_VOLUME) {
            currentVolume -= VOLUME_CHANGE;
        }
        m_player.setVolume(currentVolume);
    }

    public MediaPlayer getMusicPlayer(){
        return m_player;
    }
}
