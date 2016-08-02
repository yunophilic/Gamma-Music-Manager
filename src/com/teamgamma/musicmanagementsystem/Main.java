package com.teamgamma.musicmanagementsystem;

import com.sun.javafx.application.LauncherImpl;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Main function: calls JavaFX application with a start up loader
 * Taken from https://blog.codecentric.de/en/2015/09/javafx-how-to-easily-implement-application-preloader-2/
 */
public class Main {

    // According to https://en.wikipedia.org/wiki/Ephemeral_port, ports 49152 to 65535 are suggested
    private static final int PORT_NUMBER = 49553;

    public static void main(String[] args) {
        // Launch application if the server socket is not already open (i.e. no other instances running)
        // otherwise, exit the app
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            LauncherImpl.launchApplication(ApplicationController.class, StartUpLoader.class, args);
        } catch (IOException x) {
            System.out.println("Another instance already running... exit.");
        }
    }
}
