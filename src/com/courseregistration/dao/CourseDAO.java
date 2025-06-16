package com.courseregistration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.courseregistration.model.Course;

public class CourseDAO {
	public static void addCourse(Connection conn, Course course) {
		String sql = "INSERT INTO course (course_code, name, seat_available) "
				+ "VALUES (?, ?, ?)";
		
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, course.getCourseCode());
			statement.setString(2, course.getName());
			statement.setInt(3, course.getSeatLimit());
			
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0)
				System.out.println("Course added successfully!\n");
		} catch (SQLException e) {
			System.err.println("Error adding course: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void getAllCourses(Connection conn) {
		String sql = "SELECT * FROM course";
		
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet results = statement.executeQuery();
			
			System.out.println("\nID\tCourse Code\tCourse Name"
					+ "\t\t\t\tSeat Limit\tPrerequisites\n");
			while (results.next()) {
				ArrayList<String> prerequisites = new ArrayList<String>();
				prerequisites = PrerequisiteDAO.getCoursePrerequisites(conn,
						results.getString("course_code"));
			
				System.out.println(results.getString("id") + "\t" +
					results.getString("course_code") + "\t" +
					results.getString("name") + "\t" +
					results.getInt("seat_available") + "\t\t" + prerequisites);
			}
			System.out.println("\n");
		} catch (SQLException e) {
			System.err.println("Error fetching courses: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
