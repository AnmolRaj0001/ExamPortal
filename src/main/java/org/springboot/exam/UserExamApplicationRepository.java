// src/main/java/org/springboot/exam/UserExamApplicationRepository.java
package org.springboot.exam; // Or a new package

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserExamApplicationRepository extends JpaRepository<UserExamApplication, Long> {
    // Find an application by user and exam
    Optional<UserExamApplication> findByUser_IdAndExam_Id(Long userId, Long examId);

    // Find all applications for a specific user
    List<UserExamApplication> findByUser_Id(Long userId);

    // Find all applications for a specific user with a specific status
    List<UserExamApplication> findByUser_IdAndStatus(Long userId, ApplicationStatus status);

    // Find all applications for a specific exam
    List<UserExamApplication> findByExam_Id(Long examId);
    
    @Modifying // Indicates that this query will modify the database
    @Query("DELETE FROM UserExamApplication uea WHERE uea.exam.id = :examId") // Use exam.id if UserExamApplication has an Exam object, otherwise use uea.examId if it's a direct ID field
    void deleteAllByExamId(@Param("examId") Long examId);
}