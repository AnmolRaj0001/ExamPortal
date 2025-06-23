package org.springboot.questions;

import org.springboot.exam.Exam;
import org.springboot.exam.ExamRepository;// New import
import org.springboot.user.User; // Assuming your User entity is in org.springboot.user
import org.springboot.user.UserRepository; // Assuming your UserRepository is in org.springboot.user

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*; // Added for Collections.shuffle
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ExamRepository examRepository; // To link questions to exams
    @Autowired
    private UserExamQuestionRepository userExamQuestionRepository; // New
    @Autowired
    private UserRepository userRepository; // New: To fetch User entity

    private static final int DEFAULT_QUESTION_LIMIT = 50; // Define your default limit

    /**
     * Admin: Creates a new question for a specific exam manually.
     * @param questionDto DTO containing question details.
     * @return The created QuestionDto.
     */
    @Transactional
    public QuestionDto createQuestion(QuestionDto questionDto) {
        Exam exam = examRepository.findById(questionDto.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + questionDto.getExamId()));

        // --- NEW CHECK FOR DUPLICATE CONTENT ---
        if (questionRepository.findByQuestionTextAndExamId(questionDto.getQuestionText(), exam.getId()).isPresent()) {
            throw new RuntimeException("A question with the same text already exists for this exam.");
        }
        // --- END NEW CHECK ---

        Question question = new Question(
                exam,
                questionDto.getQuestionText(),
                questionDto.getOptions(),
                questionDto.getCorrectAnswer()
        );
        Question savedQuestion = questionRepository.save(question);
        return new QuestionDto(savedQuestion);
    }

    /**
     * User: Retrieves questions for a specific exam for a specific user.
     * This method ensures a fixed set of questions is served to the user for the exam.
     * If the user hasn't started the exam before (no assigned questions), a new set
     * of random questions (up to a limit) is generated and stored.
     *
     * @param userId The ID of the user.
     * @param examId The ID of the exam.
     * @param limit The maximum number of questions to assign/retrieve.
     * @return List of QuestionDto suitable for users, with a fixed order.
     */
    @Transactional
    public List<QuestionDto> getQuestionsForUserExam(Long userId, Long examId, int limit) {
        List<UserExamQuestion> assignedQuestions = userExamQuestionRepository.findByUserIdAndExamIdOrderByQuestionOrder(userId, examId);

        if (!assignedQuestions.isEmpty()) {
            return assignedQuestions.stream()
                    .map(ueq -> new QuestionDto(ueq.getQuestion().getId(), ueq.getQuestion().getQuestionText(), ueq.getQuestion().getOptions()))
                    .collect(Collectors.toList());
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

            List<Question> allExamQuestions = questionRepository.findByExamId(examId);

            // --- IMPORTANT: Ensure limit doesn't exceed available questions ---
            int actualLimit = Math.min(limit, allExamQuestions.size());

            Collections.shuffle(allExamQuestions);
            List<Question> selectedQuestions = allExamQuestions.stream()
                    .limit(actualLimit) // Use actualLimit to prevent errors if not enough questions
                    .collect(Collectors.toList());

            if (selectedQuestions.isEmpty()) {
                throw new RuntimeException("No questions available for this exam to assign to the user.");
            }

            List<QuestionDto> userQuestionDtos = new ArrayList<>();
            for (int i = 0; i < selectedQuestions.size(); i++) {
                Question question = selectedQuestions.get(i);
                UserExamQuestion userExamQuestion = new UserExamQuestion(user, exam, question, i + 1);
                userExamQuestionRepository.save(userExamQuestion);
                userQuestionDtos.add(new QuestionDto(question.getId(), question.getQuestionText(), question.getOptions()));
            }

            return userQuestionDtos;
        }
    }



    /**
     * Admin: Retrieves all questions for a specific exam, including correct answers.
     * @param examId The ID of the exam.
     * @return List of QuestionDto for admin view.
     */
    public List<QuestionDto> getQuestionsForAdminExam(Long examId) {
        return questionRepository.findByExamId(examId).stream()
                .map(QuestionDto::new) // Includes correctAnswer for admin view
                .collect(Collectors.toList());
    }

    /**
     * Admin: Retrieves a single question by its ID.
     * @param questionId The ID of the question.
     * @return Optional containing the QuestionDto if found.
     */
    public Optional<QuestionDto> getQuestionByIdForAdmin(Long questionId) {
        return questionRepository.findById(questionId)
                .map(QuestionDto::new);
    }

    /**
     * Admin: Retrieves all questions in the system.
     * @return List of all QuestionDto objects.
     */
    public List<QuestionDto> getAllQuestionsForAdmin() {
        return questionRepository.findAll().stream()
                .map(QuestionDto::new) // Convert all Question entities to QuestionDto
                .collect(Collectors.toList());
    }


    /**
     * Admin: Updates an existing question.
     * @param questionDto DTO containing updated question details. Must include ID.
     * @return The updated QuestionDto.
     * @throws RuntimeException if question or exam is not found.
     */
    @Transactional
    public QuestionDto updateQuestion(QuestionDto questionDto) {
        Question existingQuestion = questionRepository.findById(questionDto.getId())
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionDto.getId()));

        Exam exam = examRepository.findById(questionDto.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + questionDto.getExamId()));

        existingQuestion.setExam(exam);
        existingQuestion.setQuestionText(questionDto.getQuestionText());
        existingQuestion.setOptions(questionDto.getOptions());
        existingQuestion.setCorrectAnswer(questionDto.getCorrectAnswer());

        Question updatedQuestion = questionRepository.save(existingQuestion);
        return new QuestionDto(updatedQuestion);
    }

    /**
     * Admin: Deletes a question by its ID.
     * @param questionId The ID of the question to delete.
     * @throws RuntimeException if question is not found.
     */
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new RuntimeException("Question not found with ID: " + questionId);
        }
        questionRepository.deleteById(questionId);
    }

    /**
     * Admin: Uploads questions for a specific exam from a CSV file.
     * The CSV format is expected to be:
     * Question Text,Option 1,Option 2,Option 3,Option 4,Correct Answer
     *
     * Example row:
     * "What is the capital of France?","Paris","London","Berlin","Rome","Paris"
     *
     * @param file The CSV file to upload.
     * @param examId The ID of the exam to associate questions with.
     * @return List of created QuestionDto objects.
     * @throws RuntimeException if parsing fails or exam is not found.
     */
    @Transactional
    public List<QuestionDto> uploadQuestionsFromCsv(MultipartFile file, Long examId) {
        if (file.isEmpty()) {
            throw new RuntimeException("CSV file is empty.");
        }

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

        List<QuestionDto> uploadedQuestions = new ArrayList<>();
        BufferedReader fileReader = null;
        CSVParser csvParser = null;

        try {
            fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim());

            List<String> headers = csvParser.getHeaderNames();
            if (!headers.contains("Question Text") || !headers.contains("Option 1") ||
                !headers.contains("Option 2") || !headers.contains("Option 3") ||
                !headers.contains("Option 4") || !headers.contains("Correct Answer")) {
                throw new IllegalArgumentException("CSV file must contain headers: 'Question Text', 'Option 1', 'Option 2', 'Option 3', 'Option 4', 'Correct Answer'.");
            }

            for (CSVRecord csvRecord : csvParser) {
                String questionText = csvRecord.get("Question Text");
                List<String> options = new ArrayList<>();
                options.add(csvRecord.get("Option 1"));
                options.add(csvRecord.get("Option 2"));
                options.add(csvRecord.get("Option 3"));
                options.add(csvRecord.get("Option 4"));
                String correctAnswer = csvRecord.get("Correct Answer");

                // Basic validation for data integrity
                if (questionText == null || questionText.trim().isEmpty() ||
                    options.stream().anyMatch(o -> o == null || o.trim().isEmpty()) ||
                    correctAnswer == null || correctAnswer.trim().isEmpty()) {
                    System.err.println("Skipping malformed row: " + csvRecord);
                    continue;
                }

                // --- NEW CHECK FOR DUPLICATE CONTENT FROM CSV ---
                if (questionRepository.findByQuestionTextAndExamId(questionText, exam.getId()).isPresent()) {
                    System.err.println("Skipping duplicate question text for exam: " + questionText);
                    continue; // Skip if a question with this text already exists for this exam
                }
                // --- END NEW CHECK ---

                Question question = new Question(exam, questionText, options, correctAnswer);
                questionRepository.save(question);
                uploadedQuestions.add(new QuestionDto(question));
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid CSV format: " + e.getMessage(), e);
        } finally {
            if (csvParser != null) {
                try {
                    csvParser.close();
                } catch (IOException e) {
                    System.err.println("Error closing CSVParser: " + e.getMessage());
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    System.err.println("Error closing BufferedReader: " + e.getMessage());
                }
            }
        }
        return uploadedQuestions;
    }
}