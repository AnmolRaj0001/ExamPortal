package org.springboot.questions;
import java.util.List;


public class QuestionDto {
    private Long id;
    private Long examId; // To link to the exam
    private String questionText;
    private List<String> options;
    private String correctAnswer; // Only populated for admin purposes, not sent to user directly

    // Constructor for creating/updating (from admin input)
    public QuestionDto(Long id, Long examId, String questionText, List<String> options, String correctAnswer) {
        this.id = id;
        this.examId = examId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    // Constructor for user-facing questions (without correct answer)
    public QuestionDto(Long id, String questionText, List<String> options) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
    }

    // Constructor from Entity (for internal service use)
    public QuestionDto(Question question) {
        this.id = question.getId();
        this.examId = question.getExam().getId();
        this.questionText = question.getQuestionText();
        this.options = question.getOptions();
        this.correctAnswer = question.getCorrectAnswer(); // Include for admin/internal logic
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}