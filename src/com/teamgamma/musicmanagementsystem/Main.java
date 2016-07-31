package com.teamgamma.musicmanagementsystem;

import com.sun.javafx.application.LauncherImpl;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Main function: calls JavaFX application with a start up loader
 * Taken from https://blog.codecentric.de/en/2015/09/javafx-how-to-easily-implement-application-preloader-2/
 */
public class Main {
    public static void main(String[] args) {
        // Launch application if the server socket is not already open (i.e. no other instances running)
        // otherwise, exit the app
        try {
            // According to https://en.wikipedia.org/wiki/Ephemeral_port, ports 49152 to 65535 are suggested
            ServerSocket serverSocket = new ServerSocket(49152);
            LauncherImpl.launchApplication(ApplicationController.class, StartUpLoader.class, args);
        } catch (IOException x) {
            System.out.println("Another instance already running... exit.");
        }
    }
}
