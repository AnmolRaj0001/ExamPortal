package org.springboot.questions;

import org.springboot.exam.Exam;
import org.springboot.questions.Question;
import org.springboot.user.User; // Assuming you have a User entity
import jakarta.persistence.*;

@Entity
@Table(name = "user_exam_questions")
public class UserExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Link to the User entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam; // Link to the Exam entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // Link to the specific Question

    @Column(nullable = false)
    private Integer questionOrder; // The order of this question for this user in this exam

    // Constructors
    public UserExamQuestion() {}

    public UserExamQuestion(User user, Exam exam, Question question, Integer questionOrder) {
        this.user = user;
        this.exam = exam;
        this.question = question;
        this.questionOrder = questionOrder;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }
}
