package org.springboot.userprofiles.dto;

public class CollegeDto {
    private Long id;
    private String collegeName; // <-- Ensure this field and its getter/setter exist
    private String collegeLocation; // <-- Add this field if you want to expose location in DTO

    public CollegeDto() {
    }

    public CollegeDto(Long id, String collegeName) {
        this.id = id;
        this.collegeName = collegeName;
    }

    // Constructor with location if needed
    public CollegeDto(Long id, String collegeName, String collegeLocation) {
        this.id = id;
        this.collegeName = collegeName;
        this.collegeLocation = collegeLocation;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeLocation() { // Add getter for collegeLocation
        return collegeLocation;
    }

    public void setCollegeLocation(String collegeLocation) { // Add setter for collegeLocation
        this.collegeLocation = collegeLocation;
    }
}