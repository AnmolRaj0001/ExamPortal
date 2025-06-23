package org.springboot.userprofiles;

import org.springboot.user.User;
import jakarta.persistence.*;
import java.time.LocalDate; // For Date of Birth

@Entity
@Table(name = "user_profiles") // As per your mention, use "user_profiles"
public class UserProfiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user; // One-to-one relationship with User

    // Personal Details
    @Column(name = "dob")
    private LocalDate dateOfBirth; // Use LocalDate for dates
    private String gender;
    @Column(name = "father_name")
    private String fatherName; // Correctly added field
    
    @Column(name = "profile_photo_url") // Renamed column for clarity, consistent with URL storage
    private String profilePhotoUrl; // Correctly added field
    private String registrationId; // Unique Registration ID

    // Permanent Address
    @Column(name = "permanent_house_no")
    private String houseNo;
    @Column(name = "permanent_address_line1")
    private String streetLane;
    
    @Column(name = "permanent_landmark") // FIX: Explicitly name this column
    private String landmark;
    @Column(name = "permanent_city")
    private String city;
    @Column(name = "permanent_zip")
    private String pincode;
    @Column(name = "permanent_state")
    private String state;
    @Column(name = "permanent_country")
    private String country;

    // Present Address (if different from permanent)
    private boolean isSameAsPermanent;
    
    // To indicate if present address is same as permanent
    @Column(name = "current_house_no")
    private String presentHouseNo;
    
    @Column(name = "current_address_line1")
    private String presentStreetLane;
    
    @Column(name = "current_landmark") // FIX: Explicitly name this column, different from permanent_landmark
    private String presentLandmark;
    
    @Column(name = "current_city")
    private String presentCity;
    
    @Column(name = "current_zip")
    private String presentPincode;
    @Column(name = "current_state")
    private String presentState;
    @Column(name = "current_country")
    private String presentCountry;

    // College Details (Foreign keys to College, Course, Semester)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // We can store semester name directly or link to Semester entity
    // If you need full Semester entity details, use @ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    // --- Getters and Setters ---
    // Ensure all new fields (fatherName, profilePhotoUrl) and existing fields
    // have their corresponding getters and setters. You can use your IDE
    // to generate these if they are not already present.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getStreetLane() {
        return streetLane;
    }

    public void setStreetLane(String streetLane) {
        this.streetLane = streetLane;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isSameAsPermanent() {
        return isSameAsPermanent;
    }

    public void setSameAsPermanent(boolean sameAsPermanent) {
        isSameAsPermanent = sameAsPermanent;
    }

    public String getPresentHouseNo() {
        return presentHouseNo;
    }

    public void setPresentHouseNo(String presentHouseNo) {
        this.presentHouseNo = presentHouseNo;
    }

    public String getPresentStreetLane() {
        return presentStreetLane;
    }

    public void setPresentStreetLane(String presentStreetLane) {
        this.presentStreetLane = presentStreetLane;
    }

    public String getPresentLandmark() {
        return presentLandmark;
    }

    public void setPresentLandmark(String presentLandmark) {
        this.presentLandmark = presentLandmark;
    }

    public String getPresentCity() {
        return presentCity;
    }

    public void setPresentCity(String presentCity) {
        this.presentCity = presentCity;
    }

    public String getPresentPincode() {
        return presentPincode;
    }

    public void setPresentPincode(String presentPincode) {
        this.presentPincode = presentPincode;
    }

    public String getPresentState() {
        return presentState;
    }

    public void setPresentState(String presentState) {
        this.presentState = presentState;
    }

    public String getPresentCountry() {
        return presentCountry;
    }

    public void setPresentCountry(String presentCountry) {
        this.presentCountry = presentCountry;
    }

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }
}