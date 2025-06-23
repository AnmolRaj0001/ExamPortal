// src/main/java/org/springboot/exam/UserExamApplication.java
package org.springboot.exam; // Or a new package like org.springboot.userexam

import jakarta.persistence.*;
import org.springboot.user.User; // Assuming your User entity is here
import java.time.LocalDateTime;

@Entity
@Table(name = "user_exam_applications")
public class UserExamApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Link to the User entity

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam; // Link to the Exam entity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status; // e.g., APPLIED, COMPLETED, FAILED

    @Column(nullable = false)
    private LocalDateTime applicationDate;

    // Constructors
    public UserExamApplication() {
        this.applicationDate = LocalDateTime.now(); // Set application date automatically
        this.status = ApplicationStatus.APPLIED; // Default status
    }

    public UserExamApplication(User user, Exam exam) {
        this.user = user;
        this.exam = exam;
        this.applicationDate = LocalDateTime.now();
        this.status = ApplicationStatus.APPLIED;
    }

    // Getters and Setters
    // ... (standard getters/setters for all fields)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }
}