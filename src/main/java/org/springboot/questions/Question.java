package org.springboot.questions;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList; // Needed for returning an empty list on error
import java.io.IOException; // Needed for handling JsonProcessingException

import org.springboot.exam.Exam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Many questions can belong to one exam
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam; // Link to the Exam entity

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    // This field will now store the List<String> options as a JSON string
    // in a single column within the 'questions' table.
    @Column(name = "options_json", columnDefinition = "TEXT", nullable = false)
    private String optionsJson; // Renamed to optionsJson to clarify its purpose

    @Column(nullable = false)
    private String correctAnswer; // The correct option string, e.g., "Option B"

    // ObjectMapper is used for converting List<String> to/from JSON string.
    // It's static and final for efficiency.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Constructors
    public Question() {}

    public Question(Exam exam, String questionText, List<String> options, String correctAnswer) {
        this.exam = exam;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        // Convert the List<String> options to a JSON string for persistence
        setOptions(options); // Use the setter which handles JSON conversion
    }

    // Getters and Setters for standard fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    // Getter for options: Deserializes the JSON string from the database into a List<String>
    public List<String> getOptions() {
        try {
            // If optionsJson is null or empty, return an empty list to avoid errors.
            if (optionsJson == null || optionsJson.trim().isEmpty()) {
                return new ArrayList<>();
            }
            // Deserialize JSON string back to List<String>
            return objectMapper.readValue(optionsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (IOException e) {
            // Log the error and return an empty list to prevent application crashes
            System.err.println("Error converting options JSON to list: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Setter for options: Serializes a List<String> into a JSON string for database storage
    public void setOptions(List<String> options) {
        try {
            // Convert List<String> options to a JSON string for storage
            this.optionsJson = objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            // Log the error and store an empty JSON array on error
            System.err.println("Error converting options list to JSON: " + e.getMessage());
            this.optionsJson = "[]"; // Store empty JSON array on error
        }
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
