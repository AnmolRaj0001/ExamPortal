// src/main/java/org/springboot/exam/UserExamApplicationService.java

package org.springboot.exam;

import org.springboot.exam.UserExamApplicationService.UserExamApplicationDto;
import org.springboot.user.User;
import org.springboot.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Access;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserExamApplicationService {

    @Autowired
    private UserExamApplicationRepository userExamApplicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private ExamSessionRepository examSessionRepository;

    // DTO for incoming application request
    public static class ApplyExamRequest {
        public Long userId;
        public Long examId;
    }

    // DTO for outgoing application response - IMPORTANT: ADDED EXAM DETAILS HERE
    public static class UserExamApplicationDto {
        public Long id;
        public Long userId;
        public String userName;
        public Long examId;
        public String examTitle;
        public ApplicationStatus status; // This is the application status (APPLIED, COMPLETED, etc.)
        public LocalDateTime applicationDate;
        // NEW FIELDS: Details from the Exam itself
        public LocalDateTime examScheduledDateTime; // Exam's scheduled start time
        public Integer examDurationInMinutes; // Exam's duration
        public ExamStatus examStatus; // Exam's current status (PUBLISHED, STARTED, FINISHED)
        public boolean hasSubmitted;
        


        // Constructor to convert entity to DTO
        public UserExamApplicationDto(UserExamApplication app) {
            this.id = app.getId();
            this.userId = app.getUser().getId();
            this.userName = app.getUser().getName();
            this.examId = app.getExam().getId();
            this.examTitle = app.getExam().getTitle();
            this.status = app.getStatus();
            this.applicationDate = app.getApplicationDate();
            // Populate new fields from the associated Exam
            this.examScheduledDateTime = app.getExam().getScheduledDateTime();
            this.examDurationInMinutes = app.getExam().getDurationInMinutes();
            this.examStatus = app.getExam().getStatus();
            this.hasSubmitted = app.getStatus() == ApplicationStatus.COMPLETED;
            
            
        }
    }
    
    public static class ExamSessionUpdateRequest {
        public Long userId;
        public Long examId;
        public ExamSessionStatus status; // The new status for the session
        public LocalDateTime timestamp; // Timestamp of the status update
    }

    public static class ActiveExamSessionDto {
        public String id; // सत्र ID
        public String userId;
        public String userName;
        public String examId;
        public String examTitle;
        public ExamSessionStatus status; // सत्र की वर्तमान वास्तविक समय स्थिति
        public LocalDateTime startTime;
        public LocalDateTime lastActivityTime;
        
        public Integer examDurationInMinutes; // Duration of the exam
        public LocalDateTime examScheduledDateTime; // Scheduled start time of the exam

        public ActiveExamSessionDto(ExamSession session) {
            this.id = String.valueOf(session.getId()); // Long को String में बदलें फ्रंटएंड के लिए
            this.userId = String.valueOf(session.getUser().getId());
            this.userName = session.getUser().getName();
            this.examId = String.valueOf(session.getExam().getId());
            this.examTitle = session.getExam().getTitle();
            this.status = session.getStatus();
            this.startTime = session.getStartTime();
            this.lastActivityTime = session.getLastActivityTime();
            this.examDurationInMinutes = session.getExam().getDurationInMinutes();
            this.examScheduledDateTime = session.getExam().getScheduledDateTime();
        }
    }

    @Transactional
    public UserExamApplicationDto applyForExam(ApplyExamRequest request) {
        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.userId));
        Exam exam = examRepository.findById(request.examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + request.examId));

        Optional<UserExamApplication> existingApplication = userExamApplicationRepository.findByUser_IdAndExam_Id(user.getId(), exam.getId());
        if (existingApplication.isPresent()) {
            return new UserExamApplicationDto(existingApplication.get());
        }

        UserExamApplication newApplication = new UserExamApplication(user, exam);
        newApplication.setStatus(ApplicationStatus.APPLIED);
        UserExamApplication savedApplication = userExamApplicationRepository.save(newApplication);

        return new UserExamApplicationDto(savedApplication);
    }

    /**
     * Get all applications for a specific user, regardless of exam status.
     * This method will now return the enriched UserExamApplicationDto.
     * @param userId The ID of the user.
     * @return List of UserExamApplicationDto.
     */
    public List<UserExamApplicationDto> getUserApplications(Long userId) {
        return userExamApplicationRepository.findByUser_Id(userId).stream()
                .map(UserExamApplicationDto::new)
                .collect(Collectors.toList());
    }

    /**
     * This method specifically fetches exams whose status is STARTED.
     * You might still use this for a dedicated "Currently Running Exams" section,
     * but for "My Applied Exams", getUserApplications is more general.
     */
    public List<UserExamApplicationDto> getUserActiveExams(Long userId) {
        return userExamApplicationRepository.findByUser_Id(userId).stream()
                .filter(app -> app.getExam().getStatus() == ExamStatus.STARTED)
                .map(UserExamApplicationDto::new)
                .collect(Collectors.toList());
    }
    
    public List<UserExamApplicationDto> getUserAppliedExams(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return userExamApplicationRepository.findByUser_Id(userId).stream()
                .filter(application ->
                    // Filter by application status 'APPLIED'
                    application.getStatus() == ApplicationStatus.APPLIED &&
                    // Ensure the exam's scheduled time is in the future
                    application.getExam().getScheduledDateTime().isAfter(now) &&
                    // Ensure the exam itself is not yet STARTED or COMPLETED
                    (application.getExam().getStatus() == ExamStatus.PUBLISHED || application.getExam().getStatus() == ExamStatus.STARTED)
                )
                .map(UserExamApplicationDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markExamApplicationAsCompleted(Long userId, Long examId) {
        UserExamApplication application = userExamApplicationRepository.findByUser_IdAndExam_Id(userId, examId)
            .orElseThrow(() -> new RuntimeException("User exam application not found for userId: " + userId + " and examId: " + examId));

        // Only mark as COMPLETED if it's currently APPLIED or STARTED (if you have a STARTED status for applications)
        // Adjust logic based on your ApplicationStatus lifecycle
        if (application.getStatus() != ApplicationStatus.COMPLETED) {
            application.setStatus(ApplicationStatus.COMPLETED);
            userExamApplicationRepository.save(application);
        }
    }

//    @Transactional
//    public void updateExamSession(ExamSessionUpdateRequest request) {
//           UserExamApplication application = userExamApplicationRepository.findByUser_IdAndExam_Id(request.userId, request.examId)
//            .orElseThrow(() -> new RuntimeException("User exam application not found for userId: " + request.userId + " and examId: " + request.examId));
//
//        
//        if (request.status == ExamSessionStatus.SUBMITTED) {
//            application.setStatus(ApplicationStatus.COMPLETED);
//        }
//        
//
//        userExamApplicationRepository.save(application);
//    }
    
    @Transactional
    public void updateExamSession(ExamSessionUpdateRequest request) {
        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.userId));
        Exam exam = examRepository.findById(request.examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + request.examId));

        
        Optional<ExamSession> existingSession = examSessionRepository.findByUser_IdAndExam_Id(user.getId(), exam.getId());
        ExamSession session;
        if (existingSession.isPresent()) {
            session = existingSession.get();
        } else {
            session = new ExamSession(user, exam);
        }

        session.setStatus(request.status);
        session.setLastActivityTime(request.timestamp); // अंतिम गतिविधि समय अपडेट करें
        examSessionRepository.save(session);

    }

    public List<ActiveExamSessionDto> getAllActiveExamSessions() {
        // उन स्थितियों को परिभाषित करें जिन्हें निगरानी के लिए "सक्रिय" माना जाता है
        List<ExamSessionStatus> activeStatuses = List.of(
            ExamSessionStatus.JOINED,
            ExamSessionStatus.STARTED,
            ExamSessionStatus.IN_PROGRESS
            
        );

        return examSessionRepository.findByStatusIn(activeStatuses).stream()
            .map(ActiveExamSessionDto::new)
            .collect(Collectors.toList());
    }
}