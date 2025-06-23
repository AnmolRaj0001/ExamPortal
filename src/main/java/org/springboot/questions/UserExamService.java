// UserExamService.java (Only relevant parts updated)
package org.springboot.questions; // This package might be different if UserAnswer is in a different package

import org.springboot.exam.ApplicationStatus;
import org.springboot.exam.Exam;
import org.springboot.exam.ExamRepository;
import org.springboot.exam.ExamResult;
import org.springboot.exam.ExamResultRepository;
import org.springboot.exam.UserCompletedExamResultDto;
import org.springboot.exam.UserExamApplication;
import org.springboot.exam.UserExamApplicationRepository;
import org.springboot.questions.Question;
import org.springboot.questions.QuestionRepository;
import org.springboot.user.User;
import org.springboot.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserExamService {

    @Autowired
    private UserAnswerRepository userAnswerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserExamQuestionRepository userExamQuestionRepository; // Inject this
    @Autowired // NEW: Inject UserExamApplicationRepository
    private UserExamApplicationRepository userExamApplicationRepository;
    @Autowired // NEW: Autowire the new ExamResultRepository
    private ExamResultRepository examResultRepository;


    // DTO for returning exam results
    public static class ExamResultDto {
        private int score;
        private int totalQuestions;
        private int correct;
        private int wrong;

        public ExamResultDto(int score, int totalQuestions, int correct, int wrong) {
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.correct = correct;
            this.wrong = wrong;
        }

        // Getters
        public int getScore() {
            return score;
        }

        public int getTotalQuestions() {
            return totalQuestions;
        }

        public int getCorrect() {
            return correct;
        }

        public int getWrong() {
            return wrong;
        }
    }


    @Transactional
    public void saveUserAnswer(UserAnswerDto userAnswerDto) {
        User user = userRepository.findById(userAnswerDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userAnswerDto.getUserId()));
        Exam exam = examRepository.findById(userAnswerDto.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + userAnswerDto.getExamId()));
        Question question = questionRepository.findByIdAndExamId(userAnswerDto.getQuestionId(), userAnswerDto.getExamId())
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + userAnswerDto.getQuestionId() + " for Exam ID: " + userAnswerDto.getExamId()));

        // Check if an answer for this question and user already exists
        Optional<UserAnswer> existingAnswer = userAnswerRepository.findByUserAndExamAndQuestion(user, exam, question);

        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(userAnswerDto.getSelectedOption());

        UserAnswer userAnswer;
        if (existingAnswer.isPresent()) {
            userAnswer = existingAnswer.get();
            userAnswer.setSelectedAnswer(userAnswerDto.getSelectedOption());
            userAnswer.setIsCorrect(isCorrect);
        } else {
            userAnswer = new UserAnswer(user, exam, question, userAnswerDto.getSelectedOption(), isCorrect);
        }
        userAnswerRepository.save(userAnswer);
    }

    @Transactional
    public ExamResultDto submitExam(Long userId, Long examId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

        // Get all answers submitted by the user for this exam
        List<UserAnswer> userAnswers = userAnswerRepository.findByUserAndExam(user, exam);

        // Get the total number of questions assigned to this exam
        // Assuming you have a countByExamId method in your QuestionRepository, or adjust this line
        long totalAssignedQuestions = questionRepository.countByExamId(examId);

        // Calculate score
        int correctAnswersCount = (int) userAnswers.stream()
                .filter(UserAnswer::getIsCorrect)
                .count();

        int attemptedQuestionsCount = userAnswers.size(); // Questions for which an answer was saved

        // The number of wrong answers is based on attempted questions
        int wrongAnswersCount = attemptedQuestionsCount - correctAnswersCount;

        // Score can be simply the count of correct answers
        int score = correctAnswersCount;

        // Create the DTO for immediate return (frontend will use this)
        ExamResultDto calculatedResultDto = new ExamResultDto(score, (int)totalAssignedQuestions, correctAnswersCount, wrongAnswersCount);

        // --- NEW LOGIC STARTS HERE ---

        // 1. Fetch the UserExamApplication
        UserExamApplication application = userExamApplicationRepository.findByUser_IdAndExam_Id(userId, examId)
                .orElseThrow(() -> new RuntimeException("User exam application not found for user " + userId + " and exam " + examId));

        // 2. Persist the ExamResult
        // Check if a result already exists for this application (e.g., if re-submitting an exam)
        Optional<ExamResult> existingExamResult = examResultRepository.findByUserExamApplication_Id(application.getId());

        ExamResult examResult;
        if (existingExamResult.isPresent()) {
            // Update existing result if it exists
            examResult = existingExamResult.get();
            examResult.setScore(calculatedResultDto.getScore());
            examResult.setTotalQuestions(calculatedResultDto.getTotalQuestions());
            examResult.setCorrectAnswers(calculatedResultDto.getCorrect());
            examResult.setWrongAnswers(calculatedResultDto.getWrong());
            examResult.setSubmissionTime(LocalDateTime.now()); // Update timestamp
        } else {
            // Create a new ExamResult record if it doesn't exist
            examResult = new ExamResult(
                application, // Link to the UserExamApplication
                calculatedResultDto.getScore(),
                calculatedResultDto.getTotalQuestions(),
                calculatedResultDto.getCorrect(),
                calculatedResultDto.getWrong()
            );
        }
        examResultRepository.save(examResult); // Save or update the ExamResult entity

        // 3. Update the UserExamApplication status to COMPLETED
        application.setStatus(ApplicationStatus.COMPLETED); // Assuming ApplicationStatus is an enum with a COMPLETED state
        userExamApplicationRepository.save(application); // Save the updated application status

        // --- NEW LOGIC ENDS HERE ---

        // Return the DTO for immediate frontend display
        return calculatedResultDto;
    }

    /**
     * New method: Get user's answers for a specific exam.
     * @param userId
     * @param examId
     * @return List of UserAnswerDto
     */
//    public List<UserAnswerDto> getUserAnswersForExam(Long userId, Long examId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
//        Exam exam = examRepository.findById(examId)
//                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));
//
//        List<UserAnswer> answers = userAnswerRepository.findByUserAndExam(user, exam);
//
//        return answers.stream()
//                .map(answer -> new UserAnswerDto(
//                        answer.getUser().getId(),
//                        answer.getExam().getId(),
//                        answer.getQuestion().getId(),
//                        answer.getSelectedAnswer()
//                ))
//                .collect(Collectors.toList());
//    }
    
    @Transactional(readOnly = true)
    public List<UserAnswerDto> getUserAnswersForExam(Long userId, Long examId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

        List<UserAnswer> answers = userAnswerRepository.findByUserAndExam(user, exam);

        return answers.stream()
                .map(answer -> new UserAnswerDto(
                        answer.getUser().getId(),
                        answer.getExam().getId(),
                        answer.getQuestion().getId(),
                        answer.getSelectedAnswer()
                ))
                .collect(Collectors.toList());
    }

    /**
     * NEW SERVICE METHOD: Fetches all completed exams and their results for a given user.
     * It retrieves completed applications and, for each, reconstructs the result data.
     * This method assumes that when an exam is 'COMPLETED', its result is implicitly known
     * by re-calculating from `UserAnswer`s or if results are persisted somewhere.
     * For simplicity, this implementation will recalculate the score for each completed exam.
     */
    @Transactional(readOnly = true)
    public List<UserCompletedExamResultDto> getCompletedExamResultsForUser(Long userId) {
        // Find all applications for the user that have a 'COMPLETED' status
        List<UserExamApplication> completedApplications = userExamApplicationRepository.findByUser_IdAndStatus(userId, ApplicationStatus.COMPLETED);

        return completedApplications.stream()
            .map(application -> {
                // For each completed application, fetch or re-calculate its result
                // NOTE: Calling submitExam again here might change state or not be desired for a read-only history.
                // A better approach would be:
                // 1. To store the ExamResultDto when submitExam is first called.
                // 2. Or, to create a dedicated read-only method that computes ExamResultDto from UserAnswers
                //    without affecting application status.
                // For this example, I'll create a helper method `calculateExamResultForUserExam`
                // to avoid issues with `submitExam`'s transactional nature and status update.
                ExamResultDto result = calculateExamResultForUserExam(userId, application.getExam().getId());
                return new UserCompletedExamResultDto(application, result);
            })
            .collect(Collectors.toList());
    }
    
    
    private ExamResultDto calculateExamResultForUserExam(Long userId, Long examId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

        long totalAssignedQuestions = questionRepository.countByExamId(examId);
        List<UserAnswer> userAnswers = userAnswerRepository.findByUserAndExam(user, exam);

        int correctAnswersCount = (int) userAnswers.stream()
                .filter(UserAnswer::getIsCorrect)
                .count();

        int attemptedQuestionsCount = userAnswers.size();
        int wrongAnswersCount = attemptedQuestionsCount - correctAnswersCount;
        int score = correctAnswersCount;

        return new ExamResultDto(score, (int)totalAssignedQuestions, correctAnswersCount, wrongAnswersCount);
    }
    
    

}