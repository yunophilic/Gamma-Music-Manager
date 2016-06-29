package com.teamgamma.musicmanagementsystem.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class to manage database
 */
public class DatabaseManager {
    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + "db";
    private static final String DB_FILE_PATH = DB_DIR + File.separator + "persistence.db";

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
    private PreparedStatement m_setSelectedLeftTreeItem;
    private PreparedStatement m_getSelectedLeftTreeItem;
    private PreparedStatement m_getExpandedLeftTreeItems;
    private PreparedStatement m_addHistory;
    private PreparedStatement m_deleteFromHistory;
    private PreparedStatement m_addPlaybackQueue;
    private PreparedStatement m_incrementQueueOrder;
    private PreparedStatement m_maxOrderNumberInQueue;
    private PreparedStatement m_deleteFromQueue;
    private PreparedStatement m_updateQueueOrderNumber;
    private PreparedStatement m_getPlaybackQueue;
    private PreparedStatement m_addToPlaylistSongs;
    private PreparedStatement m_nextOrderNumber;

    public DatabaseManager() {
    }

    /**
     * prepare all prepared statements
     */
    private void prepareStatements() {
        try {
            m_addLibrary = m_connection.prepareStatement("INSERT INTO Library VALUES (?)");

            m_getLibraries = m_connection.prepareStatement("SELECT * FROM Library");

            m_deleteLibrary = m_connection.prepareStatement("DELETE FROM Library WHERE libraryPath=?");

            m_addPlaylist = m_connection.prepareStatement("INSERT INTO Playlist VALUES (?)");

            m_deletePlaylist = m_connection.prepareStatement("DELETE FROM Playlist WHERE playlistName=?");

            m_getPlaylists = m_connection.prepareStatement("SELECT * FROM Playlist");

            m_renamePlaylist = m_connection.prepareStatement("UPDATE Playlist " +
                                                             "SET playlistName=? " +
                                                             "WHERE playlistName=?");

            m_addLeftTreeItem = m_connection.prepareStatement("INSERT INTO LeftTreeView (path, isExpanded) " +
                                                              "VALUES (?, ?)");

            m_clearLeftTreeView = m_connection.prepareStatement("DELETE FROM LeftTreeView");

            m_setSelectedLeftTreeItem = m_connection.prepareStatement("UPDATE LeftTreeView " +
                                                                      "SET isSelected=1 " +
                                                                      "WHERE path=?");

            m_getSelectedLeftTreeItem = m_connection.prepareStatement("SELECT * " +
                                                                      "FROM LeftTreeView " +
                                                                      "WHERE isSelected=1");

            m_getExpandedLeftTreeItems = m_connection.prepareStatement("SELECT * " +
                                                                       "FROM LeftTreeView " +
                                                                       "WHERE isExpanded=1");

            m_addHistory = m_connection.prepareStatement("INSERT INTO History (songPath) " +
                                                         "VALUES (?)");

            m_deleteFromHistory = m_connection.prepareStatement("DELETE FROM History " +
                                                                "WHERE songPath = ?");

            m_addPlaybackQueue = m_connection.prepareStatement("INSERT INTO PlaybackQueue (songPath, orderNumber) " +
                                                                  "VALUES (?, ?)");

            m_incrementQueueOrder = m_connection.prepareStatement("UPDATE PlaybackQueue " +
                                                                  "SET orderNumber = orderNumber + 1");

            m_maxOrderNumberInQueue = m_connection.prepareStatement("SELECT max(orderNumber) " +
                                                                    "FROM PlaybackQueue");

            m_deleteFromQueue = m_connection.prepareStatement("DELETE FROM PlaybackQueue " +
                                                              "WHERE songPath = ?");

            m_updateQueueOrderNumber =  m_connection.prepareStatement("UPDATE PlaybackQueue " +
                                                                      "SET orderNumber = orderNumber - 1 " +
                                                                      "WHERE orderNumber > (SELECT orderNumber " +
                                                                                           "FROM PlaybackQueue " +
                                                                                           "WHERE songPath = ?) ");

            m_getPlaybackQueue = m_connection.prepareStatement("SELECT * FROM PlaybackQueue " +
                                                               "ORDER BY OrderNumber DESC");

            m_addToPlaylistSongs = m_connection.prepareStatement("INSERT INTO PlaylistSongs (songPath, " +
                                                                                            "playlistName, " +
                                                                                            "orderNumber, " +
                                                                                            "isLastPlayed)" +
                                                                 "VALUES (?, ?, ?, ?)");
            m_nextOrderNumber = m_connection.prepareStatement("SELECT max(orderNumber) " +
                                                              "FROM PlaylistSongs " +
                                                              "WHERE playlistName = ?");
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
     * @return true if something, false otherwise
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
            Files.createDirectories(dbFilePath.getParent());
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

            //Library table, store all the library paths
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Library (" +
                                        "libraryPath TEXT PRIMARY KEY NOT NULL" +
                                    ")");

            //left tree view table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS LeftTreeView (" +
                                        "path       TEXT    PRIMARY KEY NOT NULL," +
                                        "isExpanded INTEGER             NOT NULL," +
                                        "isSelected INTEGER             NOT NULL DEFAULT 0" +
                                    ")");

            //right tree view table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS RightTreeView (" +
                                        "path       TEXT    PRIMARY KEY NOT NULL," +
                                        "isExpanded INTEGER             NOT NULL" +
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
                                        "isLastPlayed   INTEGER   NOT NULL DEFAULT 0," +
                                        "PRIMARY KEY(songPath, playlistName, orderNumber)" +
                                        "FOREIGN KEY(playlistName) REFERENCES Playlist(playlistName) ON DELETE CASCADE" +
                                    ")");

            //History table, store songs that are played before
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS History (" +
                                        "songPath TEXT      PRIMARY KEY               NOT NULL," +
                                        "time     DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                                    ")");

            //Playback queue table, store the songs that are in the queue
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlaybackQueue (" +
                                        "songPath    TEXT    NOT NULL," +
                                        "orderNumber INTEGER NOT NULL, " +
                                        "time        DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))," +
                                        "PRIMARY KEY (songPath)" +
                                    ")");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update db by adding a new library
     *
     * @param libraryPath String
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
     * @param libraryPath String
     * @return success or fail boolean
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
     * @param playlistName String
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
     * @param playlistName String
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
     * @param oldPlaylistName
     * @param newPlaylistName
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
     * Fetch playlist names
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
     */
    public void saveLeftTreeViewState(Map<String, Boolean> pathExpandedMap) {
        try {
            m_clearLeftTreeView.execute();
            for (Map.Entry<String, Boolean> entry : pathExpandedMap.entrySet()) {
                m_addLeftTreeItem.setString(1, entry.getKey());
                m_addLeftTreeItem.setBoolean(2, entry.getValue());
                m_addLeftTreeItem.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save selected left tree item
     */
    public void saveSelectedLeftTreeItem(String centerFolderPath) {
        try {
            m_setSelectedLeftTreeItem.setString(1, centerFolderPath);
            m_setSelectedLeftTreeItem.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the path of the tree view item in the center table
     *
     * @return List of paths of expanded tree view items
     */
    public String getSelectedLeftTreeItem() {
        try {
            ResultSet resultSet = m_getSelectedLeftTreeItem.executeQuery();
            return resultSet.getString("path");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the path of the tree view items that are expanded
     *
     * @return List of paths of expanded tree view items
     */
    public List<String> getExpandedLeftTreeViewItems() {
        try {
            List<String> items = new ArrayList<>();
            ResultSet resultSet = m_getExpandedLeftTreeItems.executeQuery();
            while (resultSet.next()) {
                items.add(resultSet.getString("path"));
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
    public void addToHisotry(String songPath) {
        try {
            m_addHistory.setString(1, songPath);
            m_addHistory.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete an entry from the history table
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
     * Add the path of a song that is added to the head of the playback queue
     * @param songPath new added song's path
     */
    public void addToPlaybackQueueHead(String songPath) {
        try {
            incrementPlaybackQueueOrder();
            m_addPlaybackQueue.setString(1, songPath);
            m_addPlaybackQueue.setInt(2, 1);
            m_addPlaybackQueue.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper Function for addPlaybackQueueHead()
     * Increment the order number of each song in the playback queue table before a song is inserted at the head
     */
    public void incrementPlaybackQueueOrder() {
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
     * @param songPath new added song's path
     */
    public void addToPlaybackQueueTail(String songPath) {
        try {
            int newSongOrderNumber = getMaxOrderNumberOfPlaybackQueue() + 1;
            m_addPlaybackQueue.setString(1, songPath);
            m_addPlaybackQueue.setInt(2, newSongOrderNumber);
            m_addPlaybackQueue.executeUpdate();
        }
        catch (SQLException e) {

        }
    }

    /**
     * Helper Function for addPlaylistQueueTail()
     * Get the largest value of the order number in PlaybackQueue table
     * @return int
     */
    public int getMaxOrderNumberOfPlaybackQueue() {
        try {
            ResultSet resultSet = m_maxOrderNumberInQueue.executeQuery();
            int maxOrderNumber = resultSet.getInt(1);
            return maxOrderNumber;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Remove the specified song from the PlaybackQueue table
     * @param songPath the path of the song to be deleted
     */
    public void deleteFromPlaybackQueue(String songPath) {
        try {
            updateQueueOrderNumber(songPath);
            m_deleteFromQueue.setString(1, songPath);
            m_deleteFromQueue.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrement the order numbers of the songs that have greater order number than the song to be deleted
     * @param songPath the path of the song which to be deleted
     */
    public void updateQueueOrderNumber(String songPath) {
        try {
            m_updateQueueOrderNumber.setString(1, songPath);
            m_updateQueueOrderNumber.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a list of song paths that are in the Playback Queue
     * @return List
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
            return null;
        }
    }

    /**
     * Add a new song to the PlaylistSongs table
     * @param songPath
     * @param playlistName
     */
    public void addToPlaylistSongs(String songPath, String playlistName) {
        try {
            int nextOrderNumber = getNextOrderNumber(playlistName);
            m_addToPlaylistSongs.setString(1, songPath);
            m_addToPlaylistSongs.setString(2, playlistName);
            m_addToPlaylistSongs.setInt(3, nextOrderNumber);
            m_addToPlaylistSongs.setInt(4, 0);
            m_addToPlaylistSongs.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function for addToPlaylistSongs
     * To get the next order number which should be added along the new record
     * @param playlistName
     * @return
     */
    public int getNextOrderNumber(String playlistName) {
        try {
            m_nextOrderNumber.setString(1, playlistName);
            ResultSet resultSet = m_nextOrderNumber.executeQuery();
            int nextOrderNumber = resultSet.getInt("orderNumber");
            return nextOrderNumber + 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
