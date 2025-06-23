
package org.springboot.exam;

public enum ExamStatus {
	DRAFT,      // Admin is still working on it
    PUBLISHED,  // Visible to users, can be taken
    STARTED,    // Exam is currently active (time has started)
    FINISHED,   // Exam has concluded (time has ended)
    ARCHIVED    // No longer active/visible for new attempts (manual archiving by admin)

}