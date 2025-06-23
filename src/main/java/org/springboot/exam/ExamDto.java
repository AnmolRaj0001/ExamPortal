package org.springboot.exam;

import java.time.LocalDateTime;

public class ExamDto {
    private Long id;
    private String title;
    private String description;
    private Integer durationInMinutes;
    private LocalDateTime scheduledDateTime;
    private ExamStatus status; // Used for admin to set status, and for users to see
    private String collegeId;   // Add collegeId
    private String collegeName; // Add collegeName

    // NEW: Add fields for exam pricing
    private Boolean isFree; // True if exam is free, false if paid
    private Double amount; // Amount for paid exams (0.0 for free exams)

    // --- Constructors ---
    public ExamDto() {}

    // Updated constructor to include collegeId, collegeName, isFree, and amount
    public ExamDto(Long id, String title, String description, Integer durationInMinutes, LocalDateTime scheduledDateTime,
                   ExamStatus status, String collegeId, String collegeName, Boolean isFree, Double amount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.scheduledDateTime = scheduledDateTime;
        this.status = status;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.isFree = isFree;     // Initialize new fields
        this.amount = amount;   // Initialize new fields
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public ExamStatus getStatus() {
        return status;
    }

    public void setStatus(ExamStatus status) {
        this.status = status;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    // NEW: Getters and Setters for isFree
    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    // NEW: Getters and Setters for amount
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
