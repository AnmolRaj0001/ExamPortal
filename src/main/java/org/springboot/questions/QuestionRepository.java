package org.springboot.questions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExamId(Long examId);
    Optional<Question> findByIdAndExamId(Long questionId, Long examId);
    Optional<Question> findByQuestionTextAndExamId(String questionText, Long examId);
    
    @Modifying
    @Query("DELETE FROM Question q WHERE q.exam.id = :examId") // Adjust query based on your Question entity's mapping
    void deleteAllByExamId(@Param("examId") Long examId);
	long countByExamId(Long examId);
}