package org.springboot.exam;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams") // Explicitly map to 'exams' table
public class Exam {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false)
 private String title;

 @Column(columnDefinition = "TEXT") // For longer descriptions
 private String description;

 @Column(nullable = false)
 private Integer durationInMinutes; // Duration of the exam

 @Column(nullable = false)
 private LocalDateTime scheduledDateTime; // When the exam is scheduled

 @Enumerated(EnumType.STRING) // Store enum as String in DB
 @Column(nullable = false)
 private ExamStatus status; // E.g., DRAFT, PUBLISHED, ARCHIVED

 // Add collegeId and collegeName to the Exam entity
 @Column(nullable = true) // Allow null if a college is not always required initially
 private String collegeId;

 @Column(nullable = true) // Allow null if a college is not always required initially
 private String collegeName;

 // NEW: Add fields for exam pricing
 @Column(nullable = false) // isFree cannot be null
 private Boolean isFree; // True if exam is free, false if paid

 @Column(nullable = false) // Amount cannot be null
 private Double amount; // Amount for paid exams (0.0 for free exams)

 // --- Constructors ---
 public Exam() {}

 // Updated constructor to include collegeId, collegeName, isFree, and amount
 public Exam(String title, String description, Integer durationInMinutes, LocalDateTime scheduledDateTime,
             ExamStatus status, String collegeId, String collegeName, Boolean isFree, Double amount) {
     this.title = title;
     this.description = description;
     this.durationInMinutes = durationInMinutes;
     this.scheduledDateTime = scheduledDateTime;
     this.status = status;
     this.collegeId = collegeId;
     this.collegeName = collegeName;
     this.isFree = isFree; // Initialize new fields
     this.amount = amount; // Initialize new fields
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
