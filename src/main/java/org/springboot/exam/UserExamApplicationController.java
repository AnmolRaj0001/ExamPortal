// src/main/java/org/springboot/exam/UserExamApplicationController.java
package org.springboot.exam; // Or a new package

import org.springboot.exam.UserExamApplicationService.UserExamApplicationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/api/applications") // Base path for user exam applications
@CrossOrigin(origins = "http://localhost:3000")
public class UserExamApplicationController {

    private final UserExamApplicationRepository userExamApplicationRepository;

    @Autowired
    private UserExamApplicationService userExamApplicationService;

    UserExamApplicationController(UserExamApplicationRepository userExamApplicationRepository) {
        this.userExamApplicationRepository = userExamApplicationRepository;
    }

    /**
     * User: Endpoint for a user to apply for an exam.
     * Endpoint: POST http://localhost:8080/api/applications/apply
     * Request Body: { "userId": 1, "examId": 101 }
     * @param request - DTO containing userId and examId.
     * @return ResponseEntity with the created application DTO.
     */
    @PostMapping("/apply")
    public ResponseEntity<?> applyForExam(@RequestBody UserExamApplicationService.ApplyExamRequest request) {
        try {
            UserExamApplicationService.UserExamApplicationDto applicationDto = userExamApplicationService.applyForExam(request);
            return new ResponseEntity<>(applicationDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Return 400 Bad Request for user/exam not found or already applied
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * User: Get all applications for a specific user.
     * Endpoint: GET http://localhost:8080/api/applications/user/{userId}
     * @param userId The ID of the user.
     * @return List of user's exam applications.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserExamApplicationService.UserExamApplicationDto>> getUserApplications(@PathVariable Long userId) {
        List<UserExamApplicationService.UserExamApplicationDto> applications = userExamApplicationService.getUserApplications(userId);
        if (applications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    /**
     * User: Get exams for a specific user that are currently 'active' (status STARTED).
     * Endpoint: GET http://localhost:8080/api/applications/user/{userId}/active
     * @param userId The ID of the user.
     * @return List of user's active exam applications.
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<UserExamApplicationService.UserExamApplicationDto>> getUserActiveExams(@PathVariable Long userId) {
        List<UserExamApplicationService.UserExamApplicationDto> activeExams = userExamApplicationService.getUserActiveExams(userId);
        if (activeExams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(activeExams, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/applied")
    public ResponseEntity<List<UserExamApplicationService.UserExamApplicationDto>> getUserAppliedExams(@PathVariable Long userId) {
        List<UserExamApplicationService.UserExamApplicationDto> appliedExams = userExamApplicationService.getUserAppliedExams(userId);
        if (appliedExams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Or HttpStatus.OK with empty list
        }
        return new ResponseEntity<>(appliedExams, HttpStatus.OK);
    }
    
    @GetMapping("/admin/active-exam-sessions")
    public ResponseEntity<List<UserExamApplicationService.ActiveExamSessionDto>> getAllActiveExamSessions() {
        List<UserExamApplicationService.ActiveExamSessionDto> activeSessions = userExamApplicationService.getAllActiveExamSessions();
        if (activeSessions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(activeSessions, HttpStatus.OK);
    }
}