// ExamService.java
package org.springboot.exam;

import org.springboot.questions.QuestionRepository;
import org.springboot.questions.UserAnswerRepository;
import org.springboot.questions.UserExamQuestionRepository;
import org.springboot.questions.UserExamService.ExamResultDto;
import org.springboot.user.EmailService;
import org.springboot.user.User;
import org.springboot.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserExamApplicationRepository userExamApplicationRepository;
    @Autowired
    private UserExamQuestionRepository userExamQuestionRepository;
    @Autowired // <-- Make sure QuestionRepository is Autowired
    private QuestionRepository questionRepository;
    @Autowired // <-- Autowire the new/existing UserAnswerRepository
    private UserAnswerRepository userAnswerRepository;
    
    @Autowired
    private ExamResultRepository examResultRepository;
    
    @Autowired
    private UserExamApplicationService userExamApplicationService; //new

    public long getTotalExamCount() {
        return examRepository.count();
    }

    public ExamDto createExam(ExamDto examDto) {
        Exam exam = convertToEntity(examDto);
        exam.setStatus(ExamStatus.PUBLISHED); // Default status for new exams
        Exam savedExam = examRepository.save(exam);
        return convertToDto(savedExam);
    }

    public Optional<ExamDto> getExamById(Long id) {
        return examRepository.findById(id).map(this::convertToDto);
    }

    public List<ExamDto> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ExamDto updateExam(Long id, ExamDto examDto) {
        return examRepository.findById(id).map(existingExam -> {
            existingExam.setTitle(examDto.getTitle());
            existingExam.setDescription(examDto.getDescription());
            existingExam.setDurationInMinutes(examDto.getDurationInMinutes());
            existingExam.setScheduledDateTime(examDto.getScheduledDateTime());
            existingExam.setStatus(examDto.getStatus());
            existingExam.setCollegeId(examDto.getCollegeId());
            existingExam.setCollegeName(examDto.getCollegeName());
            existingExam.setIsFree(examDto.getIsFree()); // Update isFree
            existingExam.setAmount(examDto.getAmount()); // Update amount
            return convertToDto(examRepository.save(existingExam));
        }).orElseThrow(() -> new RuntimeException("Exam not found with id " + id));
    }

    @Transactional // This annotation is essential for the entire operation to be atomic
    public void deleteExam(Long id) {
        // Step 1: Delete all associated user answers for questions belonging to this exam ID.
        // This MUST happen before deleting questions.
        userAnswerRepository.deleteAllByExamId(id);
        examResultRepository.deleteAllByExamExamId(id);

        // Step 2: Delete all associated user exam questions for this exam ID.
        userExamQuestionRepository.deleteAllByExamId(id);

        // Step 3: Delete all associated user exam applications for this exam ID.
        userExamApplicationRepository.deleteAllByExamId(id);

        // Step 4: Delete all associated questions for this exam ID.
        // This can now happen because user_answers are gone.
        questionRepository.deleteAllByExamId(id);

        // Step 5: Now that all dependent records are gone, delete the exam itself.
        examRepository.deleteById(id);
    }

    public List<ExamDto> getExamsByCollegeId(String collegeId) {
        return examRepository.findByCollegeId(collegeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ExamDto> getUpcomingExamsForCollege(String collegeId) {
        // Filter for exams in the future and with PUBLISHED status for the given college
        LocalDateTime now = LocalDateTime.now();
        return examRepository.findByCollegeIdAndScheduledDateTimeAfterAndStatus(
                collegeId, now, ExamStatus.PUBLISHED).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ExamDto convertToDto(Exam exam) {
        ExamDto dto = new ExamDto();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setDurationInMinutes(exam.getDurationInMinutes());
        dto.setScheduledDateTime(exam.getScheduledDateTime());
        dto.setStatus(exam.getStatus());
        dto.setCollegeId(exam.getCollegeId());
        dto.setCollegeName(exam.getCollegeName());
        dto.setIsFree(exam.getIsFree()); // Set isFree
        dto.setAmount(exam.getAmount()); // Set amount
        return dto;
    }

    private Exam convertToEntity(ExamDto examDto) {
        // Corrected constructor call to match the updated Exam entity constructor
        Exam exam = new Exam(
            examDto.getTitle(),
            examDto.getDescription(),
            examDto.getDurationInMinutes(),
            examDto.getScheduledDateTime(),
            examDto.getStatus(),
            examDto.getCollegeId(),
            examDto.getCollegeName(),
            examDto.getIsFree(),  // Pass isFree
            examDto.getAmount()   // Pass amount
        );
        if (examDto.getId() != null) {
            exam.setId(examDto.getId());
        }
        return exam;
    }
    
    
    @Transactional
    public ExamResultDto submitUserExam(Long userId, Long examId) {
        // यह वह जगह है जहाँ आप उपयोगकर्ता के उत्तरों को पुनः प्राप्त करेंगे,
        // उन्हें सही उत्तरों के विरुद्ध मूल्यांकन करेंगे, और स्कोर की गणना करेंगे।
        // अभी के लिए, यह एक प्लेसहोल्डर है।

        // 1. उपयोगकर्ता के उत्तर प्राप्त करें (आपको UserAnswerRepository की आवश्यकता होगी)
        // 2. इस परीक्षा के लिए सभी प्रश्न प्राप्त करें (आपको QuestionRepository की आवश्यकता होगी)
        // 3. उत्तरों का मूल्यांकन करें और स्कोर की गणना करें
        int totalQuestions = 10; // उदाहरण मूल्य
        int correctAnswers = 7;   // उदाहरण मूल्य
        int wrongAnswers = 3;     // उदाहरण मूल्य
        int score = correctAnswers * 10; // उदाहरण स्कोरिंग (प्रत्येक सही उत्तर के लिए 10 अंक)

        // UserExamApplication की स्थिति को COMPLETED में अपडेट करें
        userExamApplicationService.markExamApplicationAsCompleted(userId, examId);

        // ExamSession की स्थिति को SUBMITTED में अपडेट करें
        userExamApplicationService.updateExamSession(new UserExamApplicationService.ExamSessionUpdateRequest() {{
            this.userId = userId;
            this.examId = examId;
            this.status = ExamSessionStatus.SUBMITTED;
            this.timestamp = LocalDateTime.now();
        }});

        // ExamResult को सहेजें (आपको ExamResultRepository की आवश्यकता होगी)
        // आपको UserExamApplication entity को पुनः प्राप्त करने की आवश्यकता होगी
        Optional<UserExamApplication> applicationOpt = userExamApplicationRepository.findByUser_IdAndExam_Id(userId, examId);
        UserExamApplication application = applicationOpt.orElseThrow(() -> new RuntimeException("User application not found for exam submission."));

        ExamResult examResult = new ExamResult(application, score, totalQuestions, correctAnswers, wrongAnswers);
        examResultRepository.save(examResult);

        // परिणाम DTO वापस करें (यह org.springboot.questions.UserExamService.ExamResultDto हो सकता है)
        // यदि यह आपके पास नहीं है तो आपको इसे यहां बनाना होगा।
        return new ExamResultDto(score, totalQuestions, correctAnswers, wrongAnswers);
    }
}
