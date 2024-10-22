package com.teamgamma.musicmanagementsystem.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Class to manage database
 */
public class DatabaseManager {
    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + "db";
    private static final String DB_FILE_PATH = DB_DIR + File.separator + "persistence.db";

    //persistence.db
    private Connection m_connection;
    private PreparedStatement m_addLibrary;
    private PreparedStatement m_getLibraries;
    private PreparedStatement m_deleteLibrary;
    private PreparedStatement m_addPlaylist;
    private PreparedStatement m_deletePlaylist;
    private PreparedStatement m_getPlaylists;
    private PreparedStatement m_renamePlaylist;
    private PreparedStatement m_addLeftTreeItem;
    private PreparedStatement m_clearLeftTreeView;
    private PreparedStatement m_getExpandedLeftTreeItems;
    private PreparedStatement m_addRightTreeItem;
    private PreparedStatement m_clearRightTreeView;
    private PreparedStatement m_getExpandedRightTreeItems;
    private PreparedStatement m_addRightFolderToTree;
    private PreparedStatement m_addHistory;
    private PreparedStatement m_deleteFromHistory;
    private PreparedStatement m_getHistory;
    private PreparedStatement m_addToPlaybackQueue;
    private PreparedStatement m_incrementQueueOrder;
    private PreparedStatement m_maxOrderNumberInQueue;
    private PreparedStatement m_deleteFromQueue;
    private PreparedStatement m_updateQueueOrderNumber;
    private PreparedStatement m_getPlaybackQueue;
    private PreparedStatement m_clearPlaybackQueue;
    private PreparedStatement m_addToPlaylistSongs;
    private PreparedStatement m_maxOrderNumberPlaylistSongs;
    private PreparedStatement m_updatePlaylistOrder;
    private PreparedStatement m_updatePLaylistLastPlayedSong;
    private PreparedStatement m_getDeleteSongOrderNumber;
    private PreparedStatement m_deleteFromPlaylistSongs;
    private PreparedStatement m_getSongsInPlaylist;
    private PreparedStatement m_orderNumOfPlaylistLastPlayedSong;
    private PreparedStatement m_deleteFromPlaylistSongsByPlaylistName;
    private PreparedStatement m_getFirstPlaylistName;
    private PreparedStatement m_addToResumeTime;
    private PreparedStatement m_updateResumeTime;
    private PreparedStatement m_getResumeTime;
    private PreparedStatement m_clearResumeTime;
    private PreparedStatement m_countResumeTimeEntry;
    private PreparedStatement m_updateHistory;

    public DatabaseManager() {
    }

    /**
     * prepare all prepared statements
     */
    private void prepareStatements() {
        try {
            m_addLibrary = m_connection.prepareStatement("INSERT INTO Library VALUES (?)");

            m_getLibraries = m_connection.prepareStatement("SELECT * FROM Library");

            m_deleteLibrary = m_connection.prepareStatement("DELETE FROM Library WHERE libraryPath = ?");

            m_addPlaylist = m_connection.prepareStatement("INSERT INTO Playlist VALUES (?)");

            m_deletePlaylist = m_connection.prepareStatement("DELETE FROM Playlist WHERE playlistName = ?");

            m_getPlaylists = m_connection.prepareStatement("SELECT * FROM Playlist");

            m_renamePlaylist = m_connection.prepareStatement("UPDATE Playlist " +
                                                             "SET playlistName = ? " +
                                                             "WHERE playlistName = ?");

            m_addLeftTreeItem = m_connection.prepareStatement("INSERT INTO LeftTreeView (expandedPath) " +
                                                              "VALUES (?)");

            m_clearLeftTreeView = m_connection.prepareStatement("DELETE FROM LeftTreeView");

            m_getExpandedLeftTreeItems = m_connection.prepareStatement("SELECT * " +
                                                                       "FROM LeftTreeView ");

            m_addRightTreeItem = m_connection.prepareStatement("INSERT INTO RightTreeView (expandedPath) " +
                                                               "VALUES (?)");

            m_clearRightTreeView = m_connection.prepareStatement("DELETE FROM RightTreeView");

            m_getExpandedRightTreeItems = m_connection.prepareStatement("SELECT * " +
                                                                        "FROM RightTreeView ");

            m_addRightFolderToTree = m_connection.prepareStatement("INSERT INTO RightTreeView (expandedPath)" +
                                                                   "VALUES (?)");

            m_addHistory = m_connection.prepareStatement("INSERT INTO History (songPath) " +
                                                         "VALUES (?)");

            m_deleteFromHistory = m_connection.prepareStatement("DELETE FROM History " +
                                                                "WHERE songPath = ?");

            m_getHistory = m_connection.prepareStatement("SELECT * " +
                                                         "FROM History " +
                                                         "ORDER BY time");

            m_addToPlaybackQueue = m_connection.prepareStatement("INSERT INTO PlaybackQueue (songPath, orderNumber) " +
                                                                 "VALUES (?, ?)");

            m_incrementQueueOrder = m_connection.prepareStatement("UPDATE PlaybackQueue " +
                                                                  "SET orderNumber = orderNumber + 1");

            m_maxOrderNumberInQueue = m_connection.prepareStatement("SELECT max(orderNumber) " +
                                                                    "FROM PlaybackQueue");

            m_deleteFromQueue = m_connection.prepareStatement("DELETE FROM PlaybackQueue " +
                                                              "WHERE songPath = ? AND orderNumber = ?");

            m_updateQueueOrderNumber =  m_connection.prepareStatement("UPDATE PlaybackQueue " +
                                                                      "SET orderNumber = orderNumber - 1 " +
                                                                      "WHERE orderNumber > (SELECT orderNumber " +
                                                                                            "FROM PlaybackQueue " +
                                                                                            "WHERE songPath = ?" +
                                                                                            "AND orderNumber = ?) ");

            m_getPlaybackQueue = m_connection.prepareStatement("SELECT * FROM PlaybackQueue " +
                                                               "ORDER BY OrderNumber ASC");

            m_clearPlaybackQueue = m_connection.prepareStatement("DELETE FROM PlaybackQueue");

            m_addToPlaylistSongs = m_connection.prepareStatement("INSERT INTO PlaylistSongs (songPath, " +
                                                                 "playlistName, " +
                                                                 "orderNumber) " +
                                                                 "VALUES (?, ?, ?)");

            m_maxOrderNumberPlaylistSongs = m_connection.prepareStatement("SELECT max(orderNumber) AS 'maxOrderNumber' " +
                                                                          "FROM PlaylistSongs " +
                                                                          "WHERE playlistName = ?");

            m_updatePlaylistOrder = m_connection.prepareStatement("UPDATE PlaylistSongs " +
                                                                  "SET orderNumber = orderNumber - 1 " +
                                                                  "WHERE playlistName = ? AND orderNumber > ?");

            m_updatePLaylistLastPlayedSong = m_connection.prepareStatement("UPDATE PlaylistSongs " +
                                                                           "SET isLastPlayed = 1 " +
                                                                           "WHERE playlistName = ? AND " +
                                                                                 "songPath = ? AND " +
                                                                                 "orderNumber = ?");


            m_getDeleteSongOrderNumber = m_connection.prepareStatement("SELECT orderNumber " +
                                                                       "FROM PlaylistSongs " +
                                                                       "WHERE playlistName = ? AND songPath = ?");

            m_deleteFromPlaylistSongs = m_connection.prepareStatement("DELETE FROM PlaylistSongs " +
                                                                      "WHERE playlistName = ? AND songPath = ? " +
                                                                      "AND orderNumber = ?");

            m_getSongsInPlaylist = m_connection.prepareStatement("SELECT songPath " +
                                                                 "FROM PlaylistSongs " +
                                                                 "WHERE playlistName = ? " +
                                                                 "ORDER BY orderNumber ASC");

            m_orderNumOfPlaylistLastPlayedSong = m_connection.prepareStatement("SELECT orderNumber " +
                                                                               "FROM PlaylistSongs " +
                                                                               "WHERE playlistName = ? AND isLastPlayed = 1");

            m_deleteFromPlaylistSongsByPlaylistName = m_connection.prepareStatement("DELETE " +
                                                                                    "FROM PlaylistSongs " +
                                                                                    "WHERE playlistName = ?");

            m_getFirstPlaylistName = m_connection.prepareStatement("SELECT * FROM Playlist " +
                                                                    "LIMIT 1");

            m_addToResumeTime = m_connection.prepareStatement("INSERT INTO ResumeTime (playlistName, resumeTime) " +
                                                              "VALUES (?, ?) ");

            m_updateResumeTime = m_connection.prepareStatement("UPDATE ResumeTime " +
                                                               "SET resumeTime = ?" +
                                                               "WHERE playlistName = ?");

            m_getResumeTime = m_connection.prepareStatement("SELECT resumeTime " +
                                                            "FROM ResumeTime " +
                                                            "WHERE playlistName = ?");

            m_clearResumeTime = m_connection.prepareStatement("DELETE FROM ResumeTime");

            m_countResumeTimeEntry = m_connection.prepareStatement("SELECT COUNT(*) AS row " +
                                                                   "FROM ResumeTime " +
                                                                   "WHERE playlistName = ?");

            m_updateHistory = m_connection.prepareStatement("UPDATE History SET songPath = ?, time = time WHERE songPath = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize database
     */
    public void setupDatabase() {
        setupConnection();
        createTables();
        prepareStatements();
    }

    /**
     * Function to see if there is something that is saved.
     *
     * @return true if there is data exists in the file, false otherwise
     */
    public boolean isDatabaseFileExist() {
        return new File(DB_FILE_PATH).exists();
    }

    /**
     * Function to create .txt file to save library names
     */
    public void createDatabaseFile() {
        Path dbFilePath = Paths.get(DB_FILE_PATH);
        try {
            Files.createFile(dbFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize database connection
     */
    private void setupConnection(){
        try {
            System.out.println("Connecting to database...");
            m_connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE_PATH);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close connection
     */
    public void closeConnection() {
        try {
            System.out.println("Closing connection...");
            m_connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create tables if not exist
     */
    private void createTables() {
        try {
            Statement statement = m_connection.createStatement();

            //turn on foreign key support
            statement.executeUpdate("PRAGMA foreign_keys = ON");

            //Library table, store all the library paths
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Library (" +
                                        "libraryPath TEXT PRIMARY KEY NOT NULL" +
                                    ")");

            //left tree view table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS LeftTreeView (" +
                                        "expandedPath TEXT NOT NULL, " +
                                        "PRIMARY KEY (expandedPath)" +
                                    ")");

            //right tree view table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS RightTreeView (" +
                                        "expandedPath TEXT NOT NULL, " +
                                        "PRIMARY KEY (expandedPath)" +
                                    ")");

            //Playlist table, store all the playlist names
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Playlist (" +
                                        "playlistName TEXT PRIMARY KEY NOT NULL" +
                                    ")");

            //playlist songs table, store all the song's paths that are in a playlist
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlaylistSongs (" +
                                        "songPath       TEXT      NOT NULL," +
                                        "playlistName   TEXT      NOT NULL," +
                                        "orderNumber    INTEGER   NOT NULL," +
                                        "isLastPlayed   BOOLEAN   NOT NULL DEFAULT 0," +
                                        "PRIMARY KEY(songPath, playlistName, orderNumber)" +
                                        "FOREIGN KEY(playlistName) REFERENCES Playlist(playlistName) ON DELETE CASCADE" +
                                    ")");

            //History table, store songs that are played before
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS History (" +
                                        "songPath TEXT                     NOT NULL," +
                                        "time     DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), " +
                                        "PRIMARY KEY (songPath, time)" +
                                    ")");

            //Playback queue table, store the songs that are in the queue
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlaybackQueue (" +
                                        "songPath    TEXT    NOT NULL," +
                                        "orderNumber INTEGER NOT NULL, " +
                                        "time        DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))," +
                                        "PRIMARY KEY (songPath, orderNumber, time)" +
                                    ")");

            // ResumeTime table, store time for each playlist when the application closes
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ResumeTime (" +
                                        "playlistName   TEXT    NOT NULL, " +
                                        "resumeTime     REAL, " +
                                        "PRIMARY KEY (playlistName)," +
                                        "FOREIGN KEY (playlistName) REFERENCES Playlist(playlistName) ON DELETE CASCADE" +
                                    ")");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update db by adding a new library
     *
     * @param libraryPath, the path of the library to be added
     */
    public void addLibrary(String libraryPath) {
        try {
            m_addLibrary.setString(1, libraryPath);
            m_addLibrary.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update db by removing an existing library
     *
     * @param libraryPath, the path of the library to be removed
     * @return true if the library is successfully removed, false otherwise
     */
    public boolean removeLibrary(String libraryPath) {
        try {
            m_deleteLibrary.setString(1, libraryPath);
            m_deleteLibrary.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update db by adding a new playlist
     *
     * @param playlistName, name of the playlist to be added
     */
    public void addPlaylist(String playlistName) {
        try {
            m_addPlaylist.setString(1, playlistName);
            m_addPlaylist.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update db by removing an existing playlist
     *
     * @param playlistName, name of the playlist to be removed
     */
    public void removePlaylist(String playlistName) {
        try {
            m_deletePlaylist.setString(1, playlistName);
            m_deletePlaylist.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rename a playlist in the Playlist table
     *
     * @param oldPlaylistName, the current name of a playlist
     * @param newPlaylistName, the new name that the playlist to change to
     */
    public void renamePlaylist(String oldPlaylistName, String newPlaylistName) {
        try {
            m_renamePlaylist.setString(1, newPlaylistName);
            m_renamePlaylist.setString(2, oldPlaylistName);
            m_renamePlaylist.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch all playlist names
     *
     * @return  List of playlist names String
     */
    public List<String> getPlaylists() {
        try {
            List<String> playlistNameList = new ArrayList<>();
            ResultSet resultSet = m_getPlaylists.executeQuery();
            while (resultSet.next()) {
                playlistNameList.add(resultSet.getString("playlistName"));
            }
            return playlistNameList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fetch library paths
     *
     * @return List of libraries path
     */
    public List<String> getLibraries() {
        try {
            List<String> libraryPathList = new ArrayList<>();
            ResultSet resultSet = m_getLibraries.executeQuery();
            while(resultSet.next()) {
                libraryPathList.add(resultSet.getString("libraryPath"));
            }
            resultSet.close();
            return libraryPathList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save state of the left tree view
     *
     * @param expandedPaths, the list of paths that are expanded
     */
    public void saveLeftTreeViewState(List<String> expandedPaths) {
        try {
            m_clearLeftTreeView.execute();
            for (int i = 0; i < expandedPaths.size(); i++) {
                m_addLeftTreeItem.setString(1, expandedPaths.get(i));
                m_addLeftTreeItem.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save state of the right tree view
     *
     * @param expandedPaths, the list of paths that are expanded
     */
    public void saveRightTreeViewState(List<String> expandedPaths) {
        try {
            m_clearRightTreeView.execute();
            for (int i = 0; i < expandedPaths.size(); i++) {
                m_addRightTreeItem.setString(1, expandedPaths.get(i));
                m_addRightTreeItem.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the path of the left tree view items that are expanded
     *
     * @return List of paths of expanded tree view items
     */
    public List<String> getExpandedLeftTreeViewItems() {
        try {
            List<String> items = new ArrayList<>();
            ResultSet resultSet = m_getExpandedLeftTreeItems.executeQuery();
            while (resultSet.next()) {
                items.add(resultSet.getString("expandedPath"));
            }
            return items;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the path of the right tree view items that are expanded
     *
     * @return List of paths of expanded tree view items
     */
    public List<String> getExpandedRightTreeViewItems() {
        try {
            List<String> items = new ArrayList<>();
            ResultSet resultSet = m_getExpandedRightTreeItems.executeQuery();
            while (resultSet.next()) {
                items.add(resultSet.getString("expandedPath"));
            }
            return items;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add the path of a song to the history table when a song is played
     *
     * @param songPath a string path of the song
     */
    public void addToHistory(String songPath) {
        try {
            m_addHistory.setString(1, songPath);
            m_addHistory.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete an entry from the history table
     *
     * @param songPath the path of the song to be deleted
     */
    public void deleteFromHistory(String songPath) {
        try {
            m_deleteFromHistory.setString(1, songPath);
            m_deleteFromHistory.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a list of songs in the table History
     *
     * @return a list of song paths in the history table
     */
    public List<String> getHistory() {
        try {
            List<String> songPathList = new ArrayList<>();
            ResultSet resultSet = m_getHistory.executeQuery();
            while (resultSet.next()) {
                songPathList.add(resultSet.getString("songPath"));
            }
            return songPathList;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add the path of a song that is added to the head of the playback queue
     *
     * @param songPath new added song's path
     */
    public void addToPlaybackQueueHead(String songPath) {
        try {
            incrementPlaybackQueueOrder();
            m_addToPlaybackQueue.setString(1, songPath);
            m_addToPlaybackQueue.setInt(2, 1);
            m_addToPlaybackQueue.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper Function for addPlaybackQueueHead()
     * Increment the order number of each song in the playback queue table before a song is inserted at the head
     */
    private void incrementPlaybackQueueOrder() {
        try {
            m_incrementQueueOrder.execute();
            System.out.println("Playback Queue order numbers updated");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add the path of a song that is added to the tail of the playback queue
     *
     * @param songPath new added song's path
     */
    public void addToPlaybackQueueTail(String songPath) {
        try {
            int newSongOrderNumber = getMaxOrderNumberOfPlaybackQueue() + 1;
            m_addToPlaybackQueue.setString(1, songPath);
            m_addToPlaybackQueue.setInt(2, newSongOrderNumber);
            m_addToPlaybackQueue.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper Function for addPlaylistQueueTail()
     * Get the largest value of the order number in PlaybackQueue table
     *
     * @return the largest order number in the PlaybackQueue table
     */
    private int getMaxOrderNumberOfPlaybackQueue() {
        try {
            ResultSet resultSet = m_maxOrderNumberInQueue.executeQuery();
            return resultSet.getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Remove the specified song from the PlaybackQueue table
     *
     * @param songPath the path of the song to be deleted
     * @param orderNumber the index of the song in the playback queue
     */
    public void deleteFromPlaybackQueue(String songPath, int orderNumber) {
        try {
            m_deleteFromQueue.setString(1, songPath);
            m_deleteFromQueue.setInt(2, orderNumber);
            m_deleteFromQueue.executeUpdate();

            updateQueueOrderNumber(songPath, orderNumber);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrement the order numbers of the songs that have greater order number than the song to be deleted
     *
     * @param songPath the path of the song which to be deleted
     * @param orderNumber the index of the song in the playback queue
     */
    public void updateQueueOrderNumber(String songPath, int orderNumber) {
        try {
            m_updateQueueOrderNumber.setString(1, songPath);
            m_updateQueueOrderNumber.setInt(2, orderNumber);
            m_updateQueueOrderNumber.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a list of song paths that are in the Playback Queue
     *
     * @return a list of song paths that are in the playback queue
     */
    public List<String> getPlaybackQueue() {
        try {
            List<String> songPathList = new ArrayList<>();
            ResultSet resultSet = m_getPlaybackQueue.executeQuery();
            while (resultSet.next()) {
                songPathList.add(resultSet.getString("songPath"));
            }
            return songPathList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Clear the PlaybackQueue table
     */
    public void clearPlaybackQueue() {
        try {
            m_clearPlaybackQueue.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new song to the PlaylistSongs table
     *
     * @param songPath, path of a song to be added into the PlaylistSongs table
     * @param playlistName, the name of the playlist which the song is belong to
     */
    private void addToPlaylistSongs(String playlistName, String songPath) {
        try {
            int nextOrderNumber = getNextOrderNumber(playlistName);
            m_addToPlaylistSongs.setString(1, songPath);
            m_addToPlaylistSongs.setString(2, playlistName);
            m_addToPlaylistSongs.setInt(3, nextOrderNumber);
            m_addToPlaylistSongs.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function for addToPlaylistSongs
     * To get the next order number which should be added along the new record
     *
     * @param playlistName, the playlist to check the order numbers
     * @return the order number of the next inserting song
     */
    private int getNextOrderNumber(String playlistName) {
        try {
            m_maxOrderNumberPlaylistSongs.setString(1, playlistName);
            ResultSet resultSet = m_maxOrderNumberPlaylistSongs.executeQuery();
            int maxOrderNumber = resultSet.getInt("maxOrderNumber");
            return maxOrderNumber + 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Delete the specified song in the specified playlist then update the order numbers of that playlist
     *
     * @param playlistName, the playlist which the song belongs to
     * @param songPath, the path of the song that is to be deleted
     */
    public void deleteFromPlaylistSongs(String playlistName, String songPath ) {
        try {
            int orderNumberOfSongToDelete = getOrderNumber(playlistName, songPath);

            m_deleteFromPlaylistSongs.setString(1, playlistName);
            m_deleteFromPlaylistSongs.setString(2, songPath);
            m_deleteFromPlaylistSongs.setInt(3, orderNumberOfSongToDelete);
            m_deleteFromPlaylistSongs.executeUpdate();

            updatePlaylistSongsOrderNumber(playlistName, orderNumberOfSongToDelete);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function for table PlaylistSongs
     * Get the order number of the specified song in the specified playlist
     *
     * @param playlistName, the playlist that contains the song
     * @param songPath, the path of the specified song
     * @return the order number of the specified song
     */
    private int getOrderNumber(String playlistName, String songPath) {
        try {
            m_getDeleteSongOrderNumber.setString(1, playlistName);
            m_getDeleteSongOrderNumber.setString(2, songPath);
            ResultSet resultSet = m_getDeleteSongOrderNumber.executeQuery();
            int orderNumber = resultSet.getInt("orderNumber");
            return orderNumber;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Helper function for deleteFromPlaylistSongs
     * Update the order numbers after a song is deleted from one of the playlist
     *
     * @param playlistName, the playlist which a song is deleted from
     * @param orderNumber, the order number of the song that has been deleted
     */
    private void updatePlaylistSongsOrderNumber(String playlistName, int orderNumber) {
        try {
            m_updatePlaylistOrder.setString(1, playlistName);
            m_updatePlaylistOrder.setInt(2, orderNumber);
            m_updatePlaylistOrder.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a list of song paths of the specified playlist
     *
     * @param playlistName, the name of the targeted playlist
     * @return a list of song paths that are in the specified playlist
     */
    public List<String> getSongsInPlaylist(String playlistName) {
        try {
            List<String> songPaths = new ArrayList<>();
            m_getSongsInPlaylist.setString(1, playlistName);
            ResultSet resultSet = m_getSongsInPlaylist.executeQuery();
            while (resultSet.next()) {
                songPaths.add(resultSet.getString("songPath"));
            }
            return songPaths;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function to get the last played song in the playlist
     *
     * @param playlistName   The name of the playlist.
     * @return  The index of the last song played or -1 if it was unable to get it.
     */
    public int getPlaylistLastPlayedSongIndex(String playlistName) {
        try{
            m_orderNumOfPlaylistLastPlayedSong.setString(1, playlistName);
            ResultSet res = m_orderNumOfPlaylistLastPlayedSong.executeQuery();
            return (res.next()) ? (res.getInt(1) - 1) : -1;
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Save playlist songs and their order
     *
     * @param playlist playlist where the songs are to be saved
     */
    public void savePlaylistSongs(Playlist playlist) {
        try {
            String playlistName = playlist.getM_playlistName();
            List<Song> songs = playlist.getM_songList();
            m_deleteFromPlaylistSongsByPlaylistName.setString(1, playlistName);
            m_deleteFromPlaylistSongsByPlaylistName.executeUpdate();
            for (Song song : songs) {
                addToPlaylistSongs(playlistName, song.getFile().getAbsolutePath());
            }
            savePlaylistLastPlayedSong(playlist);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function for savePlaylistSongs()
     *
     * @param playlist playlist where the songs are to be saved
     */
    private void savePlaylistLastPlayedSong(Playlist playlist) {
        try {
            int orderNumber = playlist.getM_currentSongIndex() + 1;
            if (orderNumber > 0) {
                m_updatePLaylistLastPlayedSong.setString(1, playlist.getM_playlistName());
                m_updatePLaylistLastPlayedSong.setString(2, playlist.getCurrentSong().getFile().getAbsolutePath());
                m_updatePLaylistLastPlayedSong.setInt(3, orderNumber);
                m_updatePLaylistLastPlayedSong.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a record of a playlist and the time of its current playing song when the application closes
     *
     * @param playlistName, the playlist to have a resume time (percentage)
     * @param percentage, the percentage of the song when left off
     */
    public void savePlaylistResumeTime(String playlistName, double percentage) {
        try {
            m_addToResumeTime.setString(1, playlistName);
            m_addToResumeTime.setDouble(2, percentage);
            m_addToResumeTime.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Empty the ResumeTime table
     */
    public void clearResumeTime() {
        try {
            m_clearResumeTime.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * To update the resume time of an record for a playlist
     *
     * @param playlistName, the playlist to be updated
     * @param percentage, the resume time to update
     */
    public void updateResumeTime(String playlistName, double percentage) {
        try {
            m_updateResumeTime.setDouble(1, percentage);
            m_updateResumeTime.setString(2, playlistName);
            m_updateResumeTime.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve the time to resume for each playlist when application starts up
     * 
     * @param playlistName, the playlist to get the resume time from
     * @return the resume time of the specified playlist
     */
    public double getResumeTime(String playlistName) {
        try {
            m_getResumeTime.setString(1, playlistName);
            ResultSet resultSet = m_getResumeTime.executeQuery();
            double percentage = resultSet.getDouble("resumeTime");
            return percentage;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Check if the resume time is already stored in the table ResumeTime
     *
     * @param playlistName, the playlist to be checked
     * @return boolean indicates whether if there is a percentage value corresponds to the specified playlist in the table
     */
    public boolean checkIfResumeTimeExists(String playlistName) {
        try {
            m_countResumeTimeEntry.setString(1, playlistName);
            ResultSet resultSet = m_countResumeTimeEntry.executeQuery();
            int count = resultSet.getInt("row");
            if (count == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the first playlist from the Playlist table
     *
     * @return the name of the first playlist. Null can also be returned when there is no playlists
     */
    public String getFirstPlaylistName() {
        try {
            ResultSet resultSet = m_getFirstPlaylistName.executeQuery();
            String firstPlaylistName = resultSet.getString("playlistName");
            return firstPlaylistName;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function to update the the history when a file has moved in the file system.
     *
     * @param oldPath       The old path of the song.
     * @param newPath       The new path location.
     */
    public void updateHistory(String oldPath, String newPath) {
        try {
            m_updateHistory.setString(1, newPath);
            m_updateHistory.setString(2, oldPath);
            m_updateHistory.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
