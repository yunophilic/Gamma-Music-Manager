# CMPT 373 Team Gamma - Music Management System

This is our Music Management application written in Java 8. This program will allow you to play your MP3 files, manage where they are located and edit their metadata. 

___

## Folder descriptions
* **src:** Contains source code for this project.
* **Docs:** Contains documentation for this project including archive of our backlog, planning documentation and links to the Google Doc folder containing the most up to date documentation. This folder also contains our citations for the code and resources we used. 
* **db:** This folder will be created after you run our application. This folder is used as storage for our application. If this folder is not present then the application will act if it is the first time running the application.
* **non-projectCode:** Contains code that is not part of the application itself. This could be things like POC or example source code. 
* **library-sample:** Contains some sample music files used for testing. 

---
## Build Instructions 
* This application can be built by using IntelliJ IDE on Windows. Load the project in to IntelliJ and run using Java 8. We currently do not officially support Linux or Mac OSX.
* Clone this repository
* Download all dependencies (below)
* Run application on IntelliJ

### Dependencies 
* [Java SE Development Kit 8u92](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [jaudiotagger-2.2.3](http://www.jthink.net/jaudiotagger/)
* [jpathwatch-0-95](https://jpathwatch.wordpress.com/)
* [jdbc-3.8.11.2](https://bitbucket.org/xerial/sqlite-jdbc/downloads)
* [JLayer 1.0.1](http://www.javazoom.net/javalayer/sources.html)
* This program uses JavaFX Media Player that depends on codecs being installed on your computer. For example, version N of the Windows operating system will require you to install Microsoft Media Feature Pack or any other officially supported codec library for this application to run. [For more information please refer to Oracle](http://www.oracle.com/technetwork/java/javase/certconfig-2095354.html)
---
