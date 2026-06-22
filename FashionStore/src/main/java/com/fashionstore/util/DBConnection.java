package com.fashionstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/fashion_store";
    private static final String DB_USER  = "root";
    private static final String DB_PASS  = "root";  // ← enter your password here

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, DB_USER, DB_PASS);
            System.out.println("Database Connected Successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver Not Found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database Connection Closed.");
            } catch (SQLException e) {
                System.out.println("Error Closing Connection: " + e.getMessage());
            }
        }
    }
}