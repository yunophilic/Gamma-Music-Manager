package com.teamgamma.musicmanagementsystem.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Created by kylewang on 2016-06-14.
 */
public class DatabaseManager {

    private Connection connection = null;

    public DatabaseManager() {

        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Connecting to database...");
            // establish connection
            connection = DriverManager.getConnection("jdbc:sqlite:dbFile.db");

        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

    }
}
