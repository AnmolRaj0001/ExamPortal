package org.springboot.questions;

import org.springboot.exam.ExamDto;
import org.springboot.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api") // Base path for all APIs
@CrossOrigin(origins = "http://localhost:3000") // Allow your React app to connect
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QuestionService questionService;
    @Autowired
    private ExamService examService; // To get list of exams for admin UI dropdown

    // DTO for admin question upload request (manual)
    public static class AdminQuestionUploadRequest {
        private Long examId;
        private String questionText;
        private List<String> options;
        private String correctAnswer;

        // Getters and Setters
        public Long getExamId() { return examId; }
        public void setExamId(Long examId) { this.examId = examId; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    }


    // --- Admin Endpoints for Questions ---

    /**
     * Admin: Creates a new question for a specific exam manually.
     * Endpoint: POST http://localhost:8080/api/admin/questions
     * @param request DTO containing question details and examId.
     * @return ResponseEntity with the created QuestionDto.
     */
    @PostMapping("/admin/questions")
    public ResponseEntity<QuestionDto> createQuestion(@RequestBody AdminQuestionUploadRequest request) {
        try {
            QuestionDto questionDto = new QuestionDto(
                null, // ID will be generated
                request.getExamId(),
                request.getQuestionText(),
                request.getOptions(),
                request.getCorrectAnswer()
            );
            QuestionDto createdQuestion = questionService.createQuestion(questionDto);
            return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Error creating question manually: {}", e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Admin: Retrieves all questions in the system (for "All Questions" page).
     * Endpoint: GET http://localhost:8080/api/admin/questions/all
     * @return ResponseEntity with a list of all QuestionDto.
     */
    @GetMapping("/admin/questions/all")
    public ResponseEntity<List<QuestionDto>> getAllAdminQuestions() {
        try {
            List<QuestionDto> questions = questionService.getAllQuestionsForAdmin();
            if (questions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 if no content
            }
            return new ResponseEntity<>(questions, HttpStatus.OK); // Return 200 with data
        } catch (Exception e) {
            logger.error("Error fetching all questions for admin: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Or more specific error handling
        }
    }

    /**
     * Admin: Retrieves a single question by its ID.
     * Endpoint: GET http://localhost:8080/api/admin/questions/{questionId}
     * @param questionId The ID of the question.
     * @return ResponseEntity with the QuestionDto.
     */
    @GetMapping("/admin/questions/{questionId}")
    public ResponseEntity<QuestionDto> getQuestionByIdForAdmin(@PathVariable Long questionId) {
        return questionService.getQuestionByIdForAdmin(questionId)
                .map(questionDto -> new ResponseEntity<>(questionDto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Admin: Updates an existing question.
     * Endpoint: PUT http://localhost:8080/api/admin/questions/{questionId}
     * @param questionId The ID of the question to update.
     * @param questionDto DTO containing updated question details.
     * @return ResponseEntity with the updated QuestionDto.
     */
    @PutMapping("/admin/questions/{questionId}")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionDto questionDto) {
        // Ensure the ID in the path matches the ID in the DTO for consistency
        if (!questionId.equals(questionDto.getId())) {
            return new ResponseEntity("Question ID in path does not match ID in request body.", HttpStatus.BAD_REQUEST);
        }
        try {
            QuestionDto updatedQuestion = questionService.updateQuestion(questionDto);
            return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error updating question {}: {}", questionId, e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND); // Or BAD_REQUEST depending on specific error
        }
    }

    /**
     * Admin: Deletes a question by its ID.
     * Endpoint: DELETE http://localhost:8080/api/admin/questions/{questionId}
     * @param questionId The ID of the question to delete.
     * @return ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/admin/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        try {
            questionService.deleteQuestion(questionId);
            return new ResponseEntity<>("Question deleted successfully.", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            logger.error("Error deleting question {}: {}", questionId, e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Admin: Retrieves all questions for a specific exam (including correct answers).
     * Endpoint: GET http://localhost:8080/api/admin/exams/{examId}/questions
     * @param examId The ID of the exam.
     * @return ResponseEntity with a list of QuestionDto.
     */
    @GetMapping("/admin/exams/{examId}/questions")
    public ResponseEntity<List<QuestionDto>> getQuestionsForAdminExam(@PathVariable Long examId) {
        List<QuestionDto> questions = questionService.getQuestionsForAdminExam(examId);
        if (questions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    // Endpoint for admin to get all exams for dropdown (already exists)
    @GetMapping("/admin/exams/all")
    public ResponseEntity<List<ExamDto>> getAllExamsForAdmin() {
        List<ExamDto> exams = examService.getAllExams();
        if (exams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    // NEW: Admin: Endpoint to upload questions from a CSV file (already exists)
    @PostMapping("/admin/questions/upload-csv/{examId}")
    public ResponseEntity<?> uploadQuestionsFromCsv(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long examId) throws IllegalArgumentException {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a CSV file to upload.", HttpStatus.BAD_REQUEST);
        }
        if (!file.getContentType().equals("text/csv")) {
            return new ResponseEntity<>("Invalid file type. Please upload a CSV file.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        try {
            List<QuestionDto> uploadedQuestions = questionService.uploadQuestionsFromCsv(file, examId);
            if (uploadedQuestions.isEmpty()) {
                return new ResponseEntity<>("No valid questions parsed from CSV.", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(uploadedQuestions, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Error uploading questions from CSV for exam {}: {}", examId, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // --- User Endpoints for Questions ---

    /**
     * User: Retrieves questions for a specific exam for a given user, ensuring a fixed set.
     * Endpoint: GET http://localhost:8080/api/users/{userId}/exams/{examId}/questions?limit={limit}
     * Correct answers are NOT included here.
     * @param userId The ID of the user.
     * @param examId The ID of the exam.
     * @param limit The maximum number of questions to assign/retrieve. Default is 100 if not provided.
     * @return ResponseEntity with a list of QuestionDto.
     */
    @GetMapping("/users/{userId}/exams/{examId}/questions")
    public ResponseEntity<List<QuestionDto>> getQuestionsForUserExam(
            @PathVariable Long userId,
            @PathVariable Long examId,
            @RequestParam(name = "limit", defaultValue = "100") int limit) { // Added limit parameter
        try {
            List<QuestionDto> questions = questionService.getQuestionsForUserExam(userId, examId, limit);
            if (questions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error fetching questions for user {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}