package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

/**
 * Underlying data structure for a Song. A File plus data additional data.
 * Credits to http://www.jthink.net/jaudiotagger/ for reading writing the song metadata
 * Credits to https://github.com/soc/jaudiotagger/blob/master/src/main/java/org/jaudiotagger/tag/id3/reference/MediaMonkeyPlayerRating.java for rating conversion
 */
public class Song implements Item {
    private File m_file;
    private String m_title;
    private String m_artist;
    private String m_album;
    private String m_genre;
    private int m_rating;
    private double m_length;
    private long m_frames;

    public Song(File file) {
        m_file = file;

        try {
            AudioFile audioFile = AudioFileIO.read(m_file);
            Tag tag = audioFile.getTag();
            if (tag == null) {
                tag = fillEmptyTag(audioFile);
            }
            parseTags(tag);

            MP3File mp3File = new MP3File(m_file);
            m_length =  mp3File.getMP3AudioHeader().getPreciseTrackLength();
            m_frames = mp3File.getMP3AudioHeader().getNumberOfFrames();
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    /**
     * Read tags and put them in attributes
     *
     * @param tag the tag object to be read
     */
    private void parseTags(Tag tag) {
        m_title = tag.getFirst(FieldKey.TITLE);
        m_artist = tag.getFirst(FieldKey.ARTIST);
        m_album = tag.getFirst(FieldKey.ALBUM);
        m_genre = tag.getFirst(FieldKey.GENRE);
        String ratingInMetadata = tag.getFirst(FieldKey.RATING);
        m_rating = convertRatingToFiveStarScale(ratingInMetadata.equals("") ? 0 : Integer.parseInt(ratingInMetadata));
    }

    /**
     * Fill empty tag to the file (used when no tag exist in the file)
     *
     * @param file the specified file
     */
    private Tag fillEmptyTag(AudioFile file) throws FieldDataInvalidException, CannotWriteException {
        Tag tag;
        tag = new ID3v24Tag();
        tag.setField(FieldKey.TITLE, "");
        tag.setField(FieldKey.ARTIST, "");
        tag.setField(FieldKey.ALBUM, "");
        tag.setField(FieldKey.GENRE, "");
        tag.setField(FieldKey.RATING, "");
        file.setTag(tag);
        AudioFileIO.write(file);
        return tag;
    }

    /**
     * Convert rating from five star scale
     * (this might not be needed when switching to other API)
     *
     * @param value the value to be converted
     */
    private int convertRatingFromFiveStarScale(int value) {
        if (value < 0 || value > 5)
            throw new IllegalArgumentException("convertRatingFromFiveStarScale() accepts values from 0 to 5 not: " + value);

        int newValue = 0;
        switch (value) {
            case 0:
                break;

            case 1:
                newValue = 1;
                break;

            case 2:
                newValue = 64;
                break;

            case 3:
                newValue = 128;
                break;

            case 4:
                newValue = 196;
                break;

            case 5:
                newValue = 255;
                break;

        }

        return newValue;
    }

    /**
     * Convert rating to five star scale
     * (this might not be needed when switching to other API)
     *
     * @param value the value to be converted
     */
    private int convertRatingToFiveStarScale(int value) {
        int newValue = 0;
        if (value <= 0)
            newValue = 0;
        else if (value <= 1)
            newValue = 1;
        else if (value <= 8)
            newValue = 0;
        else if (value <= 18)
            newValue = 1;
        else if (value <= 28)
            newValue = 1;
        else if (value <= 28)
            newValue = 1;
        else if (value <= 28)
            newValue = 1;
        else if (value <= 28)
            newValue = 1;
        else if (value <= 29)
            newValue = 2;
        else if (value <= 39)
            newValue = 1;
        else if (value <= 49)
            newValue = 1;
        else if (value <= 113)
            newValue = 2;
        else if (value <= 167)
            newValue = 3;
        else if (value <= 218)
            newValue = 4;
        else
            newValue = 5;

        return newValue;
    }

    @Override
    public File getFile() {
        return m_file;
    }

    @Override
    public void changeFile(String path) {
        m_file = new File(path);
    }

    @Override
    public boolean isRootItem() {
        return false;
    }

    @Override
    public String toString() {
        return m_file.getName();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof Song)) {
            return false;
        }

        Song otherSong = (Song)object;
        String thisFilePath = m_file.getAbsolutePath();
        String otherFilePath = otherSong.getFile().getAbsolutePath();

        return thisFilePath.equals(otherFilePath);
    }

    /**
     * Set the title attribute of this song object and also the title tag in the metadata
     *
     * @param title the specified title
     */
    public void setTitle(String title) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.TITLE, title);
            AudioFileIO.write(file);
            //update object attr
            m_title = title;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    /**
     * Set the artist attribute of this song object and also the artist tag in the metadata
     *
     * @param artist the specified artist
     */
    public void setArtist(String artist) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.ARTIST, artist);
            AudioFileIO.write(file);
            //update object attr
            m_artist = artist;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    /**
     * Set the album attribute of this song object and also the album tag in the metadata
     *
     * @param album the specified album
     */
    public void setAlbum(String album) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.ALBUM, album);
            AudioFileIO.write(file);
            //update object attr
            m_album = album;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    /**
     * Set the genre attribute of this song object and also the genre tag in the metadata
     *
     * @param genre the specified album
     */
    public void setGenre(String genre) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.GENRE, genre);
            AudioFileIO.write(file);
            //update object attr
            m_genre = genre;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    /**
     * Set the rating attribute of this song object and also the rating tag in the metadata
     *
     * @param rating the specified album
     */
    public void setRating(int rating) throws IllegalArgumentException {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.RATING, Integer.toString(convertRatingFromFiveStarScale(rating)));
            AudioFileIO.write(file);
            //update object attr
            m_rating = rating;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    /**
     * Retrieve the file name of the song without the extension
     *
     * @return the file name without extension
     */
    public String getFileName() {
        String fileNameFull = m_file.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        return fileNameFull.substring(0, beforeExtension);
    }

    /**********
     * Getters
     *************/

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

    public double getM_length() {
        return m_length;
    }

    public long getM_frames() {
        return m_frames;
    }
}
