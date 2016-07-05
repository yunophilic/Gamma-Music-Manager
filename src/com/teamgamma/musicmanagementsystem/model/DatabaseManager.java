package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private PreparedStatement m_addToPlaylistSongs;
    private PreparedStatement m_maxOrderNumberPlaylistSongs;
    private PreparedStatement m_updatePlaylistOrder;
    private PreparedStatement m_getDeleteSongOrderNumber;
    private PreparedStatement m_deleteFromPlaylistSongs;
    private PreparedStatement m_getSongsInPlaylist;
    private PreparedStatement m_orderNumOfPlaylistLastPlayedSong;
    private PreparedStatement m_deleteFromPlaylistSongsByPlaylistName;

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
                                                              "WHERE songPath = ?");

            m_updateQueueOrderNumber =  m_connection.prepareStatement("UPDATE PlaybackQueue " +
                                                                      "SET orderNumber = orderNumber - 1 " +
                                                                      "WHERE orderNumber > (SELECT orderNumber " +
                                                                      "FROM PlaybackQueue " +
                                                                      "WHERE songPath = ?) ");

            m_getPlaybackQueue = m_connection.prepareStatement("SELECT * FROM PlaybackQueue " +
                                                               "ORDER BY OrderNumber ASC");

            m_addToPlaylistSongs = m_connection.prepareStatement("INSERT INTO PlaylistSongs (songPath, " +
                                                                 "playlistName, " +
                                                                 "orderNumber, " +
                                                                 "isLastPlayed)" +
                                                                 "VALUES (?, ?, ?, ?)");

            m_maxOrderNumberPlaylistSongs = m_connection.prepareStatement("SELECT max(orderNumber) AS 'maxOrderNumber' " +
                                                                          "FROM PlaylistSongs " +
                                                                          "WHERE playlistName = ?");

            m_updatePlaylistOrder = m_connection.prepareStatement("UPDATE PlaylistSongs " +
                                                                  "SET orderNumber = orderNumber - 1 " +
                                                                  "WHERE playlistName = ? AND orderNumber > ?");


            m_getDeleteSongOrderNumber = m_connection.prepareStatement("SELECT orderNumber " +
                                                                       "FROM PlaylistSongs " +
                                                                       "WHERE playlistName = ? AND songPath = ?");

            m_deleteFromPlaylistSongs = m_connection.prepareStatement("DELETE FROM PlaylistSongs " +
                                                                      "WHERE playlistName = ? AND songPath = ? " +
                                                                      "AND orderNumber = ?");

            m_getSongsInPlaylist = m_connection.prepareStatement("SELECT songPath " +
                                                                 "FROM PlaylistSongs " +
                                                                 "WHERE PlaylistName = ? " +
                                                                 "ORDER BY orderNumber ASC");

            m_orderNumOfPlaylistLastPlayedSong = m_connection.prepareStatement("SELECT orderNumber " +
                                                                               "FROM PlaylistSongs " +
                                                                               "WHERE PlaylistName = ? AND isLastPlayed = 1");

            m_deleteFromPlaylistSongsByPlaylistName = m_connection.prepareStatement("DELETE " +
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
                    "isLastPlayed   INTEGER   NOT NULL DEFAULT 0," +
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
     * Save the path for the folder on the right panel
     * @param folderPath
     */
    public void addRightFolder(String folderPath) {
        try {
            m_addRightFolderToTree.setString(1, folderPath);
            m_addRightFolderToTree.executeUpdate();
        }
        catch (SQLException e) {
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
     * Get a list of songs in the table History
     * @return
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
            m_addToPlaybackQueue.setString(1, songPath);
            m_addToPlaybackQueue.setInt(2, newSongOrderNumber);
            m_addToPlaybackQueue.executeUpdate();
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
            return resultSet.getInt(1);
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
        }
        return null;
    }

    /**
     * Add a new song to the PlaylistSongs table
     * @param songPath
     * @param playlistName
     */
    public void addToPlaylistSongs(String playlistName, String songPath, boolean isLastedPlayed) {
        try {
            int nextOrderNumber = getNextOrderNumber(playlistName);
            m_addToPlaylistSongs.setString(1, songPath);
            m_addToPlaylistSongs.setString(2, playlistName);
            m_addToPlaylistSongs.setInt(3, nextOrderNumber);
            if (isLastedPlayed){
                m_addToPlaylistSongs.setInt(4, 1);
            } else {
                m_addToPlaylistSongs.setInt(4, 0);
            }

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
     * @param playlistName
     * @param songPath
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
     * @param playlistName
     * @param songPath
     * @return
     */
    public int getOrderNumber(String playlistName, String songPath) {
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
     * @param playlistName
     * @param orderNumber
     */
    public void updatePlaylistSongsOrderNumber(String playlistName, int orderNumber) {
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
     * @param playlistName
     * @return
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
     *
     * @return  The index of the last song played or -1 if it was unable to get it.
     */
    public int getPlaylistLastPlayedSongIndex(String playlistName) {
        try{
            m_orderNumOfPlaylistLastPlayedSong.setString(1, playlistName);
            m_orderNumOfPlaylistLastPlayedSong.execute();
            ResultSet res = m_orderNumOfPlaylistLastPlayedSong.getResultSet();
            return res.getInt(1) - 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Save playlist songs and their order
     * @param playlist playlist where the songs are to be saved
     */
    public void savePlaylistSongs(Playlist playlist) {
        try {
            String playlistName = playlist.getM_playlistName();
            List<Song> songs = playlist.getM_songList();
            m_deleteFromPlaylistSongsByPlaylistName.setString(1, playlistName);
            m_deleteFromPlaylistSongsByPlaylistName.executeUpdate();
            for (Song song : songs) {
                if (song.equals(playlist.getCurrentSong())){
                    addToPlaylistSongs(playlistName, song.getM_file().getAbsolutePath(), true);
                } else {
                    addToPlaylistSongs(playlistName, song.getM_file().getAbsolutePath(), false);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
