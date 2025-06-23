package org.springboot.questions;


public class UserAnswerDto {
    private Long userId;
    private Long examId;
    private Long questionId;
    private String selectedOption;

    // Constructors, Getters, and Setters
    public UserAnswerDto() {}

    public UserAnswerDto(Long userId, Long examId, Long questionId, String selectedOption) {
        this.userId = userId;
        this.examId = examId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}