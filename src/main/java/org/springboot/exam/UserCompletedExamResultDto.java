// Path: src/main/java/org/springboot/exam/UserCompletedExamResultDto.java
package org.springboot.exam;

import org.springboot.questions.UserExamService.ExamResultDto; // Import ExamResultDto if it's in questions package
import java.time.LocalDateTime;

/**
 * DTO for sending comprehensive completed exam results to the frontend.
 * Combines details from UserExamApplication and ExamResultDto.
 */
public class UserCompletedExamResultDto {
    private Long applicationId;
    private Long examId;
    private String examTitle;
    private LocalDateTime examScheduledDateTime;
    private int examDurationInMinutes;
    private String applicationStatus; // The status of the user's application (e.g., "COMPLETED")

    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;

    // Default constructor
    public UserCompletedExamResultDto() {}

    // Constructor to build from a UserExamApplication and an ExamResultDto
    public UserCompletedExamResultDto(UserExamApplication application, ExamResultDto result) {
        this.applicationId = application.getId();
        this.examId = application.getExam().getId();
        this.examTitle = application.getExam().getTitle();
        this.examScheduledDateTime = application.getExam().getScheduledDateTime();
        this.examDurationInMinutes = application.getExam().getDurationInMinutes();
        this.applicationStatus = application.getStatus().name(); // Convert enum to string

        if (result != null) {
            this.score = result.getScore();
            this.totalQuestions = result.getTotalQuestions();
            this.correctAnswers = result.getCorrect();
            this.wrongAnswers = result.getWrong();
        } else {
            // Handle case where result might be null (e.g., exam was completed but no score saved yet)
            this.score = 0;
            this.totalQuestions = 0;
            this.correctAnswers = 0;
            this.wrongAnswers = 0;
        }
    }

    // Getters and Setters for all fields

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public LocalDateTime getExamScheduledDateTime() {
        return examScheduledDateTime;
    }

    public void setExamScheduledDateTime(LocalDateTime examScheduledDateTime) {
        this.examScheduledDateTime = examScheduledDateTime;
    }

    public int getExamDurationInMinutes() {
        return examDurationInMinutes;
    }

    public void setExamDurationInMinutes(int examDurationInMinutes) {
        this.examDurationInMinutes = examDurationInMinutes;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
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
}