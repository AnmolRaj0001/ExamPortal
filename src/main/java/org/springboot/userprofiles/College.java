package org.springboot.userprofiles;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "college")
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "college_code")
    private String collegeCode;

    @Column(name = "college_location")
    private String collegeLocation;

    @Column(name = "name")
    private String collegeName;

    @Column(name = "college_short_name")
    private String collegeShortName;

    @Column(name = "college_website")
    private String collegeWebsite;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Constructors ---
    public College() {
    }

    public College(String collegeCode, String collegeLocation, String collegeName, String collegeShortName, String collegeWebsite) {
        this.collegeCode = collegeCode;
        this.collegeLocation = collegeLocation;
        this.collegeName = collegeName;
        this.collegeShortName = collegeShortName;
        this.collegeWebsite = collegeWebsite;
    }

    // --- Lifecycle Callbacks for audit fields ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public String getCollegeLocation() {
        return collegeLocation;
    }

    public void setCollegeLocation(String collegeLocation) {
        this.collegeLocation = collegeLocation;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeShortName() {
        return collegeShortName;
    }

    public void setCollegeShortName(String collegeShortName) {
        this.collegeShortName = collegeShortName;
    }

    public String getCollegeWebsite() {
        return collegeWebsite;
    }

    public void setCollegeWebsite(String collegeWebsite) {
        this.collegeWebsite = collegeWebsite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}