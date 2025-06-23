// Path: src/main/java/org/springboot/exam/ExamResultRepository.java
package org.springboot.exam; // Or org.springboot.results;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    // Find an exam result by the associated user exam application ID
    Optional<ExamResult> findByUserExamApplication_Id(Long userExamApplicationId);

    // Find all exam results for a given user (via userExamApplication's user ID)
    // Note: This needs a path traversal in JPA, so ensure your UserExamApplication entity
    // has a proper `user` field, and ExamResult has `userExamApplication` link.
    List<ExamResult> findByUserExamApplication_User_Id(Long userId);

    // Delete results by exam ID (useful if an exam is deleted)
    void deleteByUserExamApplication_Exam_Id(Long examId);
    @Modifying
    @Query("DELETE FROM ExamResult er WHERE er.userExamApplication.exam.id = :examId")
    void deleteAllByExamExamId(@Param("examId") Long examId);
}