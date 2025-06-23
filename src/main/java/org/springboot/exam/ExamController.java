// ExamController.java
package org.springboot.exam;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springboot.questions.UserExamService.ExamResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ExamController {
	
	 private static final Logger logger = LoggerFactory.getLogger(ExamController.class);

    @Autowired
    private ExamService examService;

    // --- Admin Endpoints ---

    @PostMapping("/admin/exams")
    public ResponseEntity<ExamDto> createExam(@RequestBody ExamDto examDto) {
        ExamDto createdExam = examService.createExam(examDto);
        return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
    }

    @GetMapping("/admin/exams")
    public ResponseEntity<List<ExamDto>> getAllExamsForAdmin() {
        List<ExamDto> exams = examService.getAllExams();
        if (exams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/admin/exams/{id}")
    public ResponseEntity<ExamDto> getExamByIdForAdmin(@PathVariable Long id) {
        Optional<ExamDto> examDto = examService.getExamById(id);
        return examDto.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/exams/{id}")
    public ResponseEntity<ExamDto> updateExam(@PathVariable Long id, @RequestBody ExamDto examDto) {
        try {
            ExamDto updatedExam = examService.updateExam(id, examDto);
            return ResponseEntity.ok(updatedExam);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/exams/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    // --- User Endpoints ---

    @GetMapping("/exams")
    public ResponseEntity<List<ExamDto>> getAllPublishedExamsForUsers() {
        List<ExamDto> exams = examService.getAllExams();
        if (exams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<ExamDto> getExamByIdForUser(@PathVariable Long id) {
        Optional<ExamDto> examDto = examService.getExamById(id);
        return examDto.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/exams/count")
    public ResponseEntity<Map<String, Long>> getTotalExamsCount() {
        long count = examService.getTotalExamCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

//    // NEW: Get exams by college ID for users (or specific roles)
//    @GetMapping("/exams/by-college/{collegeId}")
//    public ResponseEntity<List<ExamDto>> getExamsByCollegeForUsers(@PathVariable String collegeId) {
//        List<ExamDto> exams = examService.getExamsByCollegeId(collegeId);
//        if (exams.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        // You might want to filter by status here (e.g., only PUBLISHED exams)
//        // exams = exams.stream().filter(e -> e.getStatus() == ExamStatus.PUBLISHED).collect(Collectors.toList());
//        return new ResponseEntity<>(exams, HttpStatus.OK);
//    }
//    
//    @GetMapping("/exams/upcoming/by-college/{collegeId}")
//    public ResponseEntity<List<ExamDto>> getUpcomingExamsByCollege(@PathVariable String collegeId) {
//        List<ExamDto> exams = examService.getUpcomingExamsForCollege(collegeId);
//        if (exams.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(exams, HttpStatus.OK);
//    }
    
    // NEW: Get exams by college ID for users (or specific roles) - This endpoint will be consumed by the upcoming.tsx
    @GetMapping("/exams/by-college/{collegeId}")
    public ResponseEntity<List<ExamDto>> getExamsByCollegeForUsers(@PathVariable String collegeId) {
        List<ExamDto> exams = examService.getExamsByCollegeId(collegeId);
        if (exams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }
    
    // NEW: Get upcoming exams specifically for a college.
    @GetMapping("/exams/upcoming/by-college/{collegeId}")
    public ResponseEntity<List<ExamDto>> getUpcomingExamsByCollege(@PathVariable String collegeId) {
        List<ExamDto> exams = examService.getUpcomingExamsForCollege(collegeId);
        if (exams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }
    
    
 // Changed mapping to avoid conflict with UserExamController
    @PostMapping("/user-exam-sessions/{examId}/submit")
    public ResponseEntity<?> submitExamSession(@PathVariable Long examId, @RequestParam Long userId) {
        try {
            // ExamService में नई submitUserExam विधि को कॉल करें
            // अब UserExamApplicationService.ExamResultDto को सीधे ExamResultDto के रूप में संदर्भित किया जा सकता है
            ExamResultDto result = examService.submitUserExam(userId, examId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error submitting exam for user {}: {}", userId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}