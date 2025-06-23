package com.courseregistration.dao;

import java.sql.*;

public class DBConfig {
    private static final String url = "jdbc:mysql://localhost:3306/course_registration";
    private static final String username = "root";
    private static final String password = "Denniesraju@123";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Unable to connect to database!", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void resetDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Disable foreign key checks temporarily
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            
            // Clear all tables
            stmt.execute("TRUNCATE TABLE registration;");
            stmt.execute("TRUNCATE TABLE deregistration;");
            stmt.execute("TRUNCATE TABLE prerequisite;");
            stmt.execute("TRUNCATE TABLE course;");
            stmt.execute("TRUNCATE TABLE student;");
            
            // Re-enable foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            
            System.out.println("Database reset successfully!");
        } catch (SQLException e) {
            System.err.println("Error resetting database: " + e.getMessage());
            throw new RuntimeException("Failed to reset database", e);
        }
    }
}