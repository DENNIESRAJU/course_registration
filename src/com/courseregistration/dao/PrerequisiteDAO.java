package com.courseregistration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.courseregistration.model.Prerequisite;

public class PrerequisiteDAO {
	public static void addPrerequisite(Connection conn, Prerequisite prerequisite) {
		String sql = "INSERT INTO prerequisite (course_code, prerequisite_code)"
				+ " VALUES (?, ?)";
		
		try {
			PreparedStatement insert = conn.prepareStatement(sql);
			insert.setString(1, prerequisite.getCourseCode());
			insert.setString(2, prerequisite.getPrerequisiteCode());
			
			int rowsInserted = insert.executeUpdate();
			if (rowsInserted > 0)
				System.out.println("Prerequisite added successfully!\n");
		} catch (SQLException e) {
			System.err.println("Error adding prerequisite: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static ArrayList<String> getCoursePrerequisites(Connection conn,
			String courseCode) {
		String sql = "SELECT * FROM prerequisite WHERE course_code = ?";
		ArrayList<String> prerequisites = new ArrayList<String>();
		
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, courseCode);

			ResultSet results = statement.executeQuery();
			
			while (results.next())
				prerequisites.add(results.getString("prerequisite_code"));
		} catch (SQLException e) {
			System.err.println("Error fetching course prerequisites: "
					+ e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		return prerequisites;
	}
	
	public static void getAllPrerequisites(Connection conn) {
		String sql = "SELECT * FROM prerequisite";
		
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet results = statement.executeQuery();
			
			System.out.println("\nCourse Code\tPrerequisite Code\n");
			while (results.next())
				System.out.println(results.getString("course_code") + "\t" +
					results.getString("prerequisite_code"));
			System.out.println("\n");
		} catch (SQLException e) {
			System.err.println("Error fetching prerequisites: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
