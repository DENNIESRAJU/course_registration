package com.courseregistration.model;

public class Registration {
	// implement set to avoid duplicate student registrations
	private int studentId;
	private String courseCode;
	
	public Registration(int studentId, String courseCode) {
		this.studentId = studentId;
		this.courseCode = courseCode;
	}
	
	public int getStudentId() {
		return this.studentId;
	}
	
	public String getCourseCode() {
		return this.courseCode;
	}
}
