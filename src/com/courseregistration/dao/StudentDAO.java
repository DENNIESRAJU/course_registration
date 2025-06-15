package com.courseregistration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.courseregistration.model.Student;

public class StudentDAO {
	public static void addStudent(Connection conn, Student student) {
		String sql = "INSERT INTO student (name, email) VALUES (?, ?)";
		
		try {
			PreparedStatement insert = conn.prepareStatement(sql);
			insert.setString(1, student.getName());
			insert.setString(2, student.getEmail());
			
			int rowsInserted = insert.executeUpdate();
			if (rowsInserted > 0)
				System.out.println("Student added successfully!\n");
		} catch (SQLException e) {
			System.err.println("Error adding student: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
//	public static Student getStudent(Connection conn, String email) {
//		String sql = "SELECT * FROM student WHERE email = ?";
//		
//		try {
//			PreparedStatement select = conn.prepareStatement(sql);
//			select.setString(1, email);
//			
//			ResultSet result = select.executeQuery();
//			if (result.next()) {
//				System.out.println("Fetched student successfully!\n");
//				
//				int id = result.getInt("id");
//				String name = result.getString("name");
//				int marks = result.getInt("marks");
//				// Set<String> enrolledCourses = 
//				// get courses enrolled dynamically from db
//				// make a diff fn with course fetching for each student
//				// take elements from ResultSet and add to HashSet
//				
//				// TODO add course registration first
//
//				// return new Student(id, name, email, marks, enrolledCourses);
//			}
//		} catch (SQLException e) {
//			System.err.println("Error closing connection: " + e.getMessage());
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}
//	}
	
	public static void getAllStudents(Connection conn) {
		String sql = "SELECT * FROM student";
		
		try {
			PreparedStatement select = conn.prepareStatement(sql);
			ResultSet results = select.executeQuery();
			
			System.out.println("\nID\tName\t\tEmail\t");
			while (results.next())
				System.out.println(results.getString("id") + "\t" +
				results.getString("name") + "\t" + results.getString("email"));
			System.out.println("\n");
		} catch (SQLException e) {
			System.err.println("Error adding student: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
