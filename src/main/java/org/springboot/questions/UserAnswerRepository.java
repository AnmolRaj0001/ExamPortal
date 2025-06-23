package org.springboot.questions;

import java.util.List;
import java.util.Optional;

import org.springboot.exam.Exam;
import org.springboot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long>{

	Optional<UserAnswer> findByUserAndExamAndQuestion(User user, Exam exam, Question question);

	List<UserAnswer> findByUserAndExam(User user, Exam exam);
	
	 @Modifying // This annotation is crucial for executing DML statements like DELETE
	    @Query("DELETE FROM UserAnswer ua WHERE ua.question.exam.id = :examId") // This query deletes user answers whose questions belong to the given exam ID
	    void deleteAllByExamId(@Param("examId") Long examId);

}
