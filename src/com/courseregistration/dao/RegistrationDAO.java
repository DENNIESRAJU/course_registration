package com.courseregistration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.courseregistration.model.Registration;

public class RegistrationDAO {
	public static void registerStudent(Connection conn, Registration registration) {
		String sql = "INSERT INTO registration (student_id, course_code) VALUES"
				+ " (?, ?)";
		String seatLimitSql = "SELECT seat_available FROM course WHERE course_code = ?";
		String seatsChangeSql = "UPDATE course SET seat_available = seat_available - 1"
				+ " WHERE course_code = ?  AND seat_available > 0";
		
		// check if the student has prerequisites for the registering course
		
		try {
			PreparedStatement seatsAvailableStmt = conn.prepareStatement(seatLimitSql);
			seatsAvailableStmt.setString(1, registration.getCourseCode());
			ResultSet seats = seatsAvailableStmt.executeQuery();
			
			if (seats.next()) {
				int seatsAvailable = seats.getInt("seat_available");
				if (seatsAvailable > 0) {
					PreparedStatement insert = conn.prepareStatement(sql);
					insert.setInt(1, registration.getStudentId());
					insert.setString(2, registration.getCourseCode());
				
					PreparedStatement seatsChangeStmt = 
						conn.prepareStatement(seatsChangeSql);
					seatsChangeStmt.setString(1, registration.getCourseCode());
				
					int rowsInserted = insert.executeUpdate();
					int seatsChanged = seatsChangeStmt.executeUpdate();
					
					if (rowsInserted > 0 && seatsChanged > 0)
						System.out.println("Course registered successfully!\n");
				} else {
					System.out.println("This course is currently fully registered!");
				} 
			}
		} catch (SQLException e) {
			System.err.println("Error registering course: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void deregisterStudent(Connection conn, Registration registration) {
		String sql = "DELETE FROM registration WHERE student_id = ? AND "
				+ "course_code = ?";
		
		try {
			PreparedStatement delete = conn.prepareStatement(sql);
			delete.setInt(1, registration.getStudentId());
			delete.setString(2, registration.getCourseCode());
			
			int rowsInserted = delete.executeUpdate();
			if (rowsInserted > 0)
				System.out.println("Course deregistered successfully!\n");
			else
				System.out.println("No such registration found!");
		} catch (SQLException e) {
			System.err.println("Error deregistering course: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	// viewRegistrationHistory() 
}
