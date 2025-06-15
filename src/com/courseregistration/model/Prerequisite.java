package com.courseregistration.model;

public class Prerequisite {
	private String courseCode;
	private String prerequisiteCode;
	
	public Prerequisite(String courseCode) {
		this.courseCode = courseCode;
	}
	
	public Prerequisite(String courseCode, String prerequisiteCode) {
		this.courseCode = courseCode;
		this.prerequisiteCode = prerequisiteCode;
	}
	
	public String getCourseCode() {
		return this.courseCode;
	}
	
	public String getPrerequisiteCode() {
		return this.prerequisiteCode;
	}
}
