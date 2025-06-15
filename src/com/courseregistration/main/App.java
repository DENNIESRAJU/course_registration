package com.courseregistration.main;

import java.sql.Connection;
import java.util.Scanner;
import com.courseregistration.model.*;
import com.courseregistration.dao.*;

public class App {
	public static void main(String[] args) {
		int ch;
		Scanner sc = new Scanner(System.in);
		
		Connection conn = DBConfig.getConnection();
		
		System.out.println("--- COURSE REGISTRATION SYSTEM ---\n");
		
		while (true) {
			System.out.println("1. Add Student");
			System.out.println("2. Add Course");
			System.out.println("3. Add Prerequisite");
			System.out.println("4. View Students");
			System.out.println("5. View Courses");
			System.out.println("6. View Prerequisites");
			System.out.println("7. View Registration History");
			System.out.println("8. Register Student in Course");
			System.out.println("9. Deregister Student from Course");
			System.out.println("10. Exit");
			
			System.out.println("\nEnter the choice:");
			ch = sc.nextInt();
			sc.nextLine();
			
			if (ch == 10) {
				sc.close();
				DBConfig.closeConnection(conn);
				
				System.out.println("\n--- GOODBYE! ---");
				return;
			} else if (ch == 1) {
				System.out.println("Enter the student's name:");
				String name = sc.nextLine();
				
				System.out.println("Enter the student's email:");
				String email = sc.nextLine();
				
				Student student = new Student(name, email);
				StudentDAO.addStudent(conn, student);
				
			} else if (ch == 2) {
				System.out.println("Enter the course code:");
				String courseCode = sc.nextLine();
				
				System.out.println("Enter the course name:");
				String name = sc.nextLine();
				
				System.out.println("Enter the seat limit:");
				int seatLimit = sc.nextInt();
				
				Course course = new Course(courseCode, name, seatLimit);
				CourseDAO.addCourse(conn, course);
			} else if (ch == 3) {
				System.out.println("Enter the prerequisite course code:");
				String courseCode = sc.nextLine();
				
				System.out.println("Enter the course code:");
				String prerequisiteCode = sc.nextLine();
				
				Prerequisite prerequisite = new Prerequisite(courseCode,
						prerequisiteCode);
				PrerequisiteDAO.addPrerequisite(conn, prerequisite);
			} else if (ch == 4) {
				StudentDAO.getAllStudents(conn);
			} else if (ch == 5) {
				CourseDAO.getAllCourses(conn);
			} else if (ch == 6) {
				PrerequisiteDAO.getAllPrerequisites(conn);
			} else if (ch == 7) {
				// registration history retrieval logic
			} else if (ch == 8) {
				// student course registration logic
			} else if (ch == 9) {
				// student course deregistration logic
			} else {
				System.out.println("Invalid choice! Please try again.");
			}
		}
	}
}
