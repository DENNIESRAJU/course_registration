package com.courseregistration.dao;

import java.sql.*;

public class DBConfig {
	private static final String url = "jdbc:mysql://localhost:3306/course_registration";
	private static final String username = "root";
	private static final String password = "admin@123";
	
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
}
