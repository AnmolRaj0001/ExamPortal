// Path: src/main/java/org/springboot/exam/ExamResult.java
package org.springboot.exam; // Or org.springboot.results;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_results")
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-one relationship with UserExamApplication
    // This ensures each application has one unique result record
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_exam_application_id", unique = true, nullable = false)
    private UserExamApplication userExamApplication;

    // Direct link to User and Exam for easier querying, though accessible via userExamApplication
    // Consider if these are redundant if userExamApplication link is sufficient for your queries.
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", nullable = false)
    // private User user;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "exam_id", nullable = false)
    // private Exam exam;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int correctAnswers;

    @Column(nullable = false)
    private int wrongAnswers;

    @Column(nullable = false)
    private LocalDateTime submissionTime; // When the result was recorded

    // Constructors
    public ExamResult() {
        this.submissionTime = LocalDateTime.now();
    }

    public ExamResult(UserExamApplication userExamApplication, int score, int totalQuestions, int correctAnswers, int wrongAnswers) {
        this.userExamApplication = userExamApplication;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.submissionTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserExamApplication getUserExamApplication() {
        return userExamApplication;
    }

    public void setUserExamApplication(UserExamApplication userExamApplication) {
        this.userExamApplication = userExamApplication;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }
}