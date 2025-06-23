package org.springboot.questions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserExamQuestionRepository extends JpaRepository<UserExamQuestion, Long> {
    // Find questions assigned to a specific user for a specific exam, ordered by questionOrder
    List<UserExamQuestion> findByUserIdAndExamIdOrderByQuestionOrder(Long userId, Long examId);

    // Check if a user already has questions assigned for an exam
    Optional<UserExamQuestion> findFirstByUserIdAndExamId(Long userId, Long examId);
    long countByUserIdAndExamId(Long userId, Long examId);
    
    @Modifying // Indicates that this query will modify the database
    @Query("DELETE FROM UserExamQuestion ueq WHERE ueq.exam.id = :examId") // Adjust query if UserExamQuestion has a direct examId field, otherwise this is correct for a relationship
    void deleteAllByExamId(@Param("examId") Long examId);
}
