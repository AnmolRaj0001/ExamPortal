package org.springboot.userprofiles.dto;

import java.time.LocalDate; // For Date of Birth

public class UserProfileDto {
    private Long id; // UserProfiles ID
    private Long userId; // Corresponding User ID
    private String name; // User's name from User entity
    private String email; // User's email from User entity
    private String mobile; // User's mobile from User entity
    private String registrationId;
    private boolean profileCompleted;
    private String profilePhotoBase64;

    // Personal Details
    private LocalDate dateOfBirth;
    private String gender;
    private String fatherName;
    private String profilePhotoUrl;

    // Permanent Address
    private String houseNo;
    private String streetLane;
    private String landmark;
    private String city;
    private String pincode;
    private String state;
    private String country;

    // Present Address
    private boolean isSameAsPermanent;
    private String presentHouseNo;
    private String presentStreetLane;
    private String presentLandmark;
    private String presentCity;
    private String presentPincode;
    private String presentState;
    private String presentCountry;

    // College Details - Direct fields (for when frontend sends directly)
    private Long collegeId;
    private String collegeName;

    // College Details - Nested DTO (for when backend sends detailed object)
    // This is useful for fetching/displaying a richer college object
    private CollegeDto college; // Use the CollegeDto for nested college info

    private Long courseId;
    private String courseName;
    private Long semesterId;
    private String semesterName;

    public UserProfileDto() {}

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }
    
    

    public String getProfilePhotoBase64() {
		return profilePhotoBase64;
	}

	public void setProfilePhotoBase64(String profilePhotoBase64) {
		this.profilePhotoBase64 = profilePhotoBase64;
	}

	public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }

    // Permanent Address
    public String getHouseNo() { return houseNo; }
    public void setHouseNo(String houseNo) { this.houseNo = houseNo; }

    public String getStreetLane() { return streetLane; }
    public void setStreetLane(String streetLane) { this.streetLane = streetLane; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    // Present Address
    public boolean isSameAsPermanent() { return isSameAsPermanent; }
    public void setSameAsPermanent(boolean sameAsPermanent) { isSameAsPermanent = sameAsPermanent; }

    public String getPresentHouseNo() { return presentHouseNo; }
    public void setPresentHouseNo(String presentHouseNo) { this.presentHouseNo = presentHouseNo; }

    public String getPresentStreetLane() { return presentStreetLane; }
    public void setPresentStreetLane(String presentStreetLane) { this.presentStreetLane = presentStreetLane; }

    public String getPresentLandmark() { return presentLandmark; }
    public void setPresentLandmark(String presentLandmark) { this.presentLandmark = presentLandmark; }

    public String getPresentCity() { return presentCity; }
    public void setPresentCity(String presentCity) { this.presentCity = presentCity; }

    public String getPresentPincode() { return presentPincode; }
    public void setPresentPincode(String presentPincode) { this.presentPincode = presentPincode; }

    public String getPresentState() { return presentState; }
    public void setPresentState(String presentState) { this.presentState = presentState; }

    public String getPresentCountry() { return presentCountry; }
    public void setPresentCountry(String presentCountry) { this.presentCountry = presentCountry; }

    // College Details - Getters/Setters for direct fields
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    // College Details - Getters/Setters for nested CollegeDto
    public CollegeDto getCollege() { return college; }
    public void setCollege(CollegeDto college) { this.college = college; }


    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    // Corrected setCourseName method to return void
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }


    public Long getSemesterId() { return semesterId; }
    public void setSemesterId(Long semesterId) { this.semesterId = semesterId; }

    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }
}
