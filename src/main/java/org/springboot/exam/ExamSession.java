// src/main/java/org/springboot/exam/ExamSession.java
package org.springboot.exam;

import jakarta.persistence.*;
import org.springboot.user.User; // सुनिश्चित करें कि आपका User.java यहाँ है
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_sessions")
public class ExamSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamSessionStatus status; // JOINED, STARTED, IN_PROGRESS, SUBMITTED, DISCONNECTED, EXPIRED

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // जब उपयोगकर्ता पहली बार परीक्षा सत्र में शामिल हुआ (उदाहरण के लिए, प्री-एग्जाम पेज)

    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime; // उपयोगकर्ता से अंतिम ज्ञात गतिविधि

    // Default constructor
    public ExamSession() {
        this.startTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();
        this.status = ExamSessionStatus.JOINED; // निर्माण पर डिफ़ॉल्ट रूप से JOINED
    }

    // Constructor with user and exam
    public ExamSession(User user, Exam exam) {
        this.user = user;
        this.exam = exam;
        this.startTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();
        this.status = ExamSessionStatus.JOINED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public ExamSessionStatus getStatus() {
        return status;
    }

    public void setStatus(ExamSessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }
}