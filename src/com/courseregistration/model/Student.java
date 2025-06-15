package com.courseregistration.model;

import java.util.Set;
import java.util.HashSet;

public class Student {
	private int id;
	private String name;
	private String email;
	private Set<String> enrolledCourses = new HashSet<>();
	
	// for adding data to backend
	public Student(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	// for data retrieval from backend
	public Student(int id, String name, String email, Set<String> enrolledCourses) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.enrolledCourses = enrolledCourses;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEnrolledCourses() {
		// dao logic to assign data to the hashset
	}
	
	public Set<String> getEnrolledCourses() {
		return this.enrolledCourses;
	}
}
