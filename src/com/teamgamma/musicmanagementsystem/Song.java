package com.teamgamma.musicmanagementsystem;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

/**
 * Underlying data structure for a Song. A File plus data additional data.
 */
public class Song {
    private File m_file;
    private String m_songName;
    private String m_title;
    private String m_artist;
    private String m_album;
    private String m_genre;
    private int m_rating;

    public Song(String pathToFile) {
        m_file = new File(pathToFile);
        m_songName = m_file.getName();
        try {
            //credits to http://www.jthink.net/jaudiotagger/
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            m_title = tag.getFirst(FieldKey.TITLE);
            m_artist = tag.getFirst(FieldKey.ARTIST);
            m_album = tag.getFirst(FieldKey.ALBUM);
            m_genre = tag.getFirst(FieldKey.GENRE);
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }

        m_rating = 0;
    }

    public File getM_file() {
        return m_file;
    }

    public String getM_songName() {
        return m_songName;
    }

    public String getM_title() {
        return m_title;
    }

    public String getM_artist() {
        return m_artist;
    }

    public String getM_album() {
        return m_album;
    }

    public String getM_genre() {
        return m_genre;
    }

    public int getM_rating() {
        return m_rating;
    }

    public void setM_title(String title) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.TITLE, title);
            file.commit();
            //update object attr
            m_title = title;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    public void setM_artist(String artist) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.ARTIST, artist);
            file.commit();
            //update object attr
            m_artist = artist;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    public void setM_album(String album) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.ALBUM, album);
            file.commit();
            //update object attr
            m_album = album;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    public void setM_genre(String genre) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.GENRE, genre);
            file.commit();
            //update object attr
            m_genre = genre;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    public void setM_rating(int rating) {
        m_rating = rating;
    }
}
