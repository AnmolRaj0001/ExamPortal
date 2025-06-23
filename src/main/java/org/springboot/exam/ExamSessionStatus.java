package org.springboot.exam;

public enum ExamSessionStatus {
	
	 	JOINED,         // User has entered the pre-exam page
	    STARTED,        // User has officially started the exam (after pre-exam checks)
	    IN_PROGRESS,    // User is actively taking the exam (can be updated periodically)
	    PAUSED,         // (Optional) User paused the exam
	    SUBMITTED,      // User has submitted the exam
	    GRADED,         // (Optional) Exam has been graded
	    EXPIRED,        // Exam session expired (e.g., ran out of time)
	    NOT_STARTED     // User has applied but not started the exam session (इसे पिछली सूची से रखा गया है)

}
