// UserExamController.java (Only relevant parts updated)
package org.springboot.questions; // This package might be different if UserAnswer is in a different package

import org.springboot.exam.UserCompletedExamResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-exams")
@CrossOrigin(origins = "http://localhost:3000")
public class UserExamController {

    @Autowired
    private UserExamService userExamService;

    @PostMapping("/answer")
    public ResponseEntity<Void> submitAnswer(@RequestBody UserAnswerDto userAnswerDto) {
        try {
            userExamService.saveUserAnswer(userAnswerDto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Log the exception for debugging
            System.err.println("Error saving user answer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<UserExamService.ExamResultDto> submitExam(
            @PathVariable Long examId,
            @RequestParam Long userId) {
        try {
            UserExamService.ExamResultDto result = userExamService.submitExam(userId, examId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            System.err.println("Error submitting exam: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // NEW ENDPOINT: To fetch previously saved answers for a user for a given exam
    @GetMapping("/{examId}/answers")
    public ResponseEntity<List<UserAnswerDto>> getUserAnswers(
            @PathVariable Long examId,
            @RequestParam Long userId) {
        try {
            List<UserAnswerDto> answers = userExamService.getUserAnswersForExam(userId, examId);
            if (answers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Or HttpStatus.OK with empty list
            }
            return ResponseEntity.ok(answers);
        } catch (RuntimeException e) {
            System.err.println("Error fetching user answers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
//    @GetMapping("/{examId}/answers")
//    public ResponseEntity<List<UserAnswerDto>> getUserAnswers(@PathVariable Long examId,@RequestParam Long userId) {
//        try {
//            List<UserAnswerDto> answers = userExamService.getUserAnswersForExam(userId, examId);
//            if (answers.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Or HttpStatus.OK with empty list
//            }
//            return ResponseEntity.ok(answers);
//        } catch (RuntimeException e) {
//            System.err.println("Error fetching user answers: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//    }

    /**
     * NEW ENDPOINT: To fetch completed exam details along with user results for a specific user.
     * Endpoint: GET /api/user-exams/results/user/{userId}
     *
     * @param userId The ID of the user.
     * @return List of completed exam results (UserCompletedExamResultDto).
     */
    @GetMapping("/results/user/{userId}")
    public ResponseEntity<List<UserCompletedExamResultDto>> getUserCompletedExamResults(@PathVariable Long userId) {
        try {
            List<UserCompletedExamResultDto> results = userExamService.getCompletedExamResultsForUser(userId);
            if (results.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) { // Catching generic Exception to be robust
            System.err.println("Error fetching completed exam results for user " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}