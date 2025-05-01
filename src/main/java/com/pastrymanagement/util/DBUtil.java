package com.pastrymanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/pastry_management";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin37";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("Failed to load PostgreSQL JDBC Driver", e);
        }
    }

    //method to get the database connection
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connection a la base de donnees ");
            e.printStackTrace();
            throw new RuntimeException("failed to connect to the database", e);
        }
    }

    public static void testConnection() {
        System.out.println("testing database connection...");
        try (Connection c = getConnection()) {
            System.out.println(("Connection successful!!!"));
        }catch (SQLException e){
            System.out.println("Connection failed !!!");
            e.printStackTrace();
        }
    }
}
