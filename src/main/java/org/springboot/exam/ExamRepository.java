package org.springboot.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    // Custom query to find all exams that are PUBLISHED, for the user view
    List<Exam> findByStatus(ExamStatus status);
    
    List<Exam> findByCollegeId(String collegeId);
    
    List<Exam> findByCollegeIdAndStatus(String collegeId, ExamStatus status);
    List<Exam> findByCollegeIdAndScheduledDateTimeAfterAndStatus(String collegeId, LocalDateTime scheduledDateTime, ExamStatus status);
    
}