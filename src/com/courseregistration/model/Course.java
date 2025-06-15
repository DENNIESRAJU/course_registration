package com.courseregistration.model;

import java.util.ArrayList;

public class Course {
	private int id;
	private String courseCode;
	private String name;
	private int seatLimit;
	private ArrayList<String> prerequisites;
	
	public Course(String courseCode, String name, int seatLimit) {
		this.courseCode = courseCode;
		this.name = name;
		this.seatLimit = seatLimit;
	}
	
	public Course(int id, String courseCode, String name, int seatLimit,
			ArrayList<String> prerequisites) {
		this.id = id;
		this.courseCode = courseCode;
		this.name = name;
		this.seatLimit = seatLimit;
		this.prerequisites = prerequisites;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getCourseCode() {
		return this.courseCode;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getSeatLimit() {
		return this.seatLimit;
	}
}
