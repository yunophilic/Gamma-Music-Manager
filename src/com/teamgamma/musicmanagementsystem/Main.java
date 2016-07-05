package com.teamgamma.musicmanagementsystem;

import com.sun.javafx.application.LauncherImpl;

/**
 * Main function: calls JavaFX application with a start up loader
 * Taken from https://blog.codecentric.de/en/2015/09/javafx-how-to-easily-implement-application-preloader-2/
 */
public class Main {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(ApplicationController.class, StartUpLoader.class, args);
    }
}
