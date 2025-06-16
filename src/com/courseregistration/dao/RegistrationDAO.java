package com.courseregistration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.courseregistration.model.Registration;

public class RegistrationDAO {
	public static void registerStudent(Connection conn, Registration registration) {
		String sql = "INSERT INTO registration (student_id, course_code) VALUES"
				+ " (?, ?)";
		String seatLimitSql = "SELECT seat_available FROM course WHERE "
				+ "course_code = ?";
		String seatsChangeSql = "UPDATE course SET seat_available = seat_available - 1"
				+ " WHERE course_code = ?  AND seat_available > 0";
		
		try {
			PreparedStatement seatsAvailableStmt = conn.prepareStatement(seatLimitSql);
			seatsAvailableStmt.setString(1, registration.getCourseCode());
			ResultSet seats = seatsAvailableStmt.executeQuery();
			
			if (seats.getInt("seat_available") > 0) {
				// check if student has prerequisites for the registering course
				ArrayList<String> prerequisites = PrerequisiteDAO
						.getCoursePrerequisites(conn, registration
								.getCourseCode());
				
				if (prerequisites.size() > 0) {
					ResultSet registrationHistory = 
							getRegistrationHistory(conn, 
									registration.getStudentId());
					
					// number of rows in prerequisite ArrayList
					int size = prerequisites.size();
					int prerequisitesCompleted = 0;
					
					while (registrationHistory.next()) {
						// check if the course code in history matches
						// the course codes in the prerequisites
						for (int i = 0; i <= size; i++) {
							if (registrationHistory.getString("course_code")
									== prerequisites.get(i)) {
								prerequisitesCompleted++;
						}
					}
				}
					
				if (prerequisitesCompleted != size)
					return;
			}
			
			// if yes, then registering
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
		} catch (SQLException e) {
			System.err.println("Error registering course: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void deregisterStudent(Connection conn, Registration registration) {
		String deleteRegistrationSql = "DELETE FROM registration WHERE "
				+ "student_id = ? AND course_code = ?";
		String seatChangeSql = "UPDATE course SET seat_available = "
				+ "seat_available + 1 WHERE student_id = ?";
		String deregistrationSql = "INSERT INTO deregistration (student_id, "
				+ "course_code) VALUES (?, ?)";
		
		try {
			PreparedStatement deleteRegistration = conn.
					prepareStatement(deleteRegistrationSql);
			deleteRegistration.setInt(1, registration.getStudentId());
			deleteRegistration.setString(2, registration.getCourseCode());
			
			PreparedStatement seatChange = conn.prepareStatement(seatChangeSql);
			seatChange.setInt(1, registration.getStudentId());
			
			PreparedStatement deregistration = conn.
					prepareStatement(deregistrationSql);
			deregistration.setInt(1, registration.getStudentId());
			deregistration.setString(2, registration.getCourseCode());
			
			int deleted = deleteRegistration.executeUpdate();
			int seatChanged = seatChange.executeUpdate();
			int deregistered = deregistration.executeUpdate();
			
			if (deleted > 0 && seatChanged > 0 && deregistered > 0)
				System.out.println("Course deregistered successfully!\n");
		} catch (SQLException e) {
			System.err.println("Error deregistering course: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static ResultSet getRegistrationHistory(Connection conn, int studentId) {
		String registrationHistorySql = "SELECT * FROM registration WHERE "
				+ "student_id = ?";
		
		try {
			PreparedStatement studentRegistrationHisoryStmt = conn
				.prepareStatement(registrationHistorySql,
						ResultSet.TYPE_SCROLL_INSENSITIVE, // make it scrollable
					    ResultSet.CONCUR_READ_ONLY);
			studentRegistrationHisoryStmt.setInt(1, studentId);
			ResultSet studentRegistrationHistory = 
				studentRegistrationHisoryStmt.executeQuery();
			
			return studentRegistrationHistory;
		} catch (SQLException e) {
			System.out.println("Error fetching registration history: "
					+ e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public static void viewRegistrationHistory(Connection conn, int studentId) {
		String registrationHistorySql = "SELECT * FROM registration WHERE "
				+ "student_id = ?";
		
		try {
			PreparedStatement studentRegistrationHisoryStmt = conn
				.prepareStatement(registrationHistorySql);
			studentRegistrationHisoryStmt.setInt(1, studentId);
			ResultSet studentRegistrationHistory = 
				studentRegistrationHisoryStmt.executeQuery();
			
			System.out.println("\nStudent ID\tCourse Code\n");
			while (studentRegistrationHistory.next()) {
				System.out.println(studentRegistrationHistory.getString("student_id")
						+ "\t\t" + studentRegistrationHistory
						.getString("course_code"));
			}
			System.out.println("\n");
		} catch (SQLException e) {
			System.out.println("Error fetching registration history: "
					+ e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
