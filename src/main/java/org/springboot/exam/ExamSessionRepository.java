// src/main/java/org/springboot/exam/ExamSessionRepository.java
package org.springboot.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    // उपयोगकर्ता और परीक्षा द्वारा एक सत्र खोजें
    Optional<ExamSession> findByUser_IdAndExam_Id(Long userId, Long examId);

    // सभी सक्रिय सत्र खोजें (सबमिट नहीं किए गए, डिस्कनेक्ट नहीं किए गए, या समाप्त नहीं हुए)
    List<ExamSession> findByStatusNot(ExamSessionStatus status);

    // वे सत्र खोजें जो सक्रिय रूप से लिए जा रहे हैं (JOINED, STARTED, IN_PROGRESS)
    List<ExamSession> findByStatusIn(List<ExamSessionStatus> statuses);
}