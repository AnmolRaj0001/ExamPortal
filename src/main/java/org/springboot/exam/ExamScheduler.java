
package org.springboot.exam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExamScheduler {

    @Autowired
    private ExamRepository examRepository;

    /**
     * Scheduled task to update exam statuses from PUBLISHED to STARTED.
     * Runs every minute.
     */
    @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    @Transactional // Ensure the database update is atomic
    public void updateExamStatusToStarted() {
        LocalDateTime now = LocalDateTime.now();

        // Find PUBLISHED exams whose scheduled start time has passed or is exactly now
        List<Exam> publishedExams = examRepository.findByStatus(ExamStatus.PUBLISHED);

        for (Exam exam : publishedExams) {
            // Check if the exam's scheduled start time is in the past or current
            if (exam.getScheduledDateTime().isBefore(now) || exam.getScheduledDateTime().isEqual(now)) {
                System.out.println("Changing status of exam: " + exam.getTitle() + " to STARTED.");
                exam.setStatus(ExamStatus.STARTED);
                examRepository.save(exam);
            }
        }
    }

    /**
     * Scheduled task to update exam statuses from STARTED to FINISHED.
     * Runs every minute.
     */
    @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    @Transactional // Ensure the database update is atomic
    public void updateExamStatusToFinished() {
        LocalDateTime now = LocalDateTime.now();

        // Find STARTED exams
        List<Exam> startedExams = examRepository.findByStatus(ExamStatus.STARTED);

        for (Exam exam : startedExams) {
            // Calculate the exam's end time
            LocalDateTime examEndTime = exam.getScheduledDateTime().plusMinutes(exam.getDurationInMinutes());

            // Check if the exam's calculated end time has passed or is exactly now
            if (examEndTime.isBefore(now) || examEndTime.isEqual(now)) {
                System.out.println("Changing status of exam: " + exam.getTitle() + " to FINISHED.");
                exam.setStatus(ExamStatus.FINISHED);
                examRepository.save(exam);
            }
        }
    }
}