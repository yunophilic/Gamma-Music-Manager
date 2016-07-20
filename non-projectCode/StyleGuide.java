// Package declarations come at the top.
package com.teamgamma.musicmanagementsystem.model;

// Import statements come after package declaration.
import com.teamgamma.musicmanagementsystem.musicplayer.*;
import javafx.application.Platform;
import java.io.File;

// Each class should be commented with a comment about what this class is
/**
 * This file is an example of the style guide in this project.
 *
 * Each class should be commented with a comment about what this class is.
 */
public class StyleGuide {
    // Constants should be the first thing in the class if there are any. Always try to extract constants especially for
    // values that will be shown to the user.
    public static final String TEAM_NAME = "GAMMA";
    public static final int GROUP_SIZE = 8;
    public static final int OUR_GRADE = 100;

    // Member variables should come after constants and should use the m_ prefix convention.
    // Comments on instance variables are optional.
    private String m_name;

    private MusicPlayerObserver m_observer;

    // Every function should have a JavaDoc function comment.
    /**
     * Constructor
     */
    public StyleGuide() {
        m_name = TEAM_NAME;
    }

    // Functions are separated by a single newline.
    // There should be a newline between the overview function comment and the parameters and the return value.
    /**
     * Function to print out the message to the console based on parameters passed in.
     *
     * @param numberOfTimes             The number of times to print out the message.
     * @param message                   The massage to print out.
     */
    public void printMessage(int numberOfTimes, String message){
        for (int i = 0; i < numberOfTimes; ++i){
            System.out.println(message);
        }
    }

    /**
     * Function to calculate the grade for the project.
     *
     * @return  The grade out of 100
     */
    public int calcuateGrade() {
        return OUR_GRADE;
    }

    /**
     * Function to print Hello World.
     */
    public void printHelloWorld(){
        // Code that is taken from somewhere online should be cited where it is used.
        // The folloing code is from https://docs.oracle.com/javase/tutorial/getStarted/application/
        System.out.println("Hello World!"); // Display the string.
    }

    // Getters and setters can be commented individually or just have a general comment on them.
    /**********
     * Getters and setters
     *************/
    public String getName() {
        return m_name;
    }

    // This also applies for things like observer notifies are registers.
    /**********
     * Observer Notifies and registers
     *************/
    public void notifyObservers() {
        m_observer.updateUI();
    }

    public void registerObserver() {
        // We will be using Lambda expressions rather than anonymous classes.
        m_observer = () -> System.out.println("Update Observer");

        // More complex lambda expressions should be spaced accordingly. Also notice that when you have a function there
        // is a newline that separates the comment from other code.
        boolean isValid = true;
        m_observer = () -> Platform.runLater(
            () -> {
                // If and else statements are to setup like below.
                if (isValid) {
                    System.out.println("More Complex");
                } else {
                    assert(false);
                }
            }
        );
    }
}

/**
 * Interfaces should also have a class comment describing it.
 */
public interface Styleable {
    /**
     * A function to demonstrate interface commenting.
     */
    void styleMe();
}

/**
 * Class to demonstrate an interface implementation.
 */
public class StyleAbleConcret implements Styleable {
    /**
     * Constructor
     */
    public StyleAbleConcret() {
        // If the function or block of code does nothing please add in a comment saying so.
        // Do nothing
    }

    // Overridden functions can be commented optionally.
    @Override
    public void styleMe() {
        System.out.println("You do need to be styled, you look great already!");
    }
}