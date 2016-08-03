# Gamma Music Manager

The Gamma Music Manager allows you to manage your Mp3 files, maintain your playlists, and play music -- all in one app! 
You will be able to copy, paste, delete and move songs using the built-in file manager. In addition, choose any Mp3 file to play in our music player.
You will also be able to create and delete playlists, and add and remove songs to a playlist.

> Note: we currently do not support Linux or Mac OSX.

___

## Folder Structure
* **src:** 
    * **com.teamgamma.musicmanagementsystem:** Contains source code for this project
        * **misc:** Miscellaneous classes including utility classes and constant variables
        * **model:** Classes for the model including DatabaseManager and FilePersistentStorage classes
        * **musicplayer:** Contains classes for the model and UI of our music player
        * **ui:** UI classes for our application (excluding the music player)
        * **watchservice:** Watcher class that watches for file changes made through the file system
        * **Main.java:** Contains our main() function
        * **StartUpLoader.java:** UI for the start up loader
    * **res:** Images used in the application
* **Docs:** Contains documentation for this project 
    * Includes archives of our backlog and other planning documentation
    * **citations.txt:** 
        * This folder also contains citations for the code and resources used
        * A link to the Google Doc folder containing the most up to date documentation
* **db:** This folder is used as storage for our application
    * This folder will be created after you run our application for the first time 
    * If this folder is not present then the application will assume it is the first time running the application.
    * **config.json:** Saves information on app configurations such as volume and selected folders
    * **persistence.db:** Saves the playlists, music player history, playback queue, library locations, and other file manager states
* **non-projectCode:** Contains code that is not part of the application itself. This could be things like POC or example source code. 
* **library-sample:** Contains some sample music files used for testing 
* **ThirdParty:** Contains third-party libraries used by this application

---
## Build Instructions
* Clone this repository
* Download Java SE Development Kit 8u92 (refer to Dependencies section)
* Load project into IntelliJ IDE
* Create a new run configuration under the Application section
* Set the Main class to be com.teamgamma.musicmanagementsystem.Main
* Set the JRE to be 1.8 with a version greater than or equal to 8.4
* Run Main.main()

> Note: JDK 8u40 or newer is required to use the JavaFX Dialogs in this application.

### Dependencies 
* [Java SE Development Kit 8u92](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [jaudiotagger-2.2.3](http://www.jthink.net/jaudiotagger/)
* [jpathwatch-0-95](https://jpathwatch.wordpress.com/)
* [jdbc-3.8.11.2](https://bitbucket.org/xerial/sqlite-jdbc/downloads)
* [JLayer 1.0.1](http://www.javazoom.net/javalayer/sources.html)
* [Java Native Access](https://github.com/java-native-access/jna)
* [Simple Json](https://github.com/fangyidong/json-simple)

---
