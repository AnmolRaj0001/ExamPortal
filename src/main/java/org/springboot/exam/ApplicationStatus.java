// src/main/java/org/springboot/exam/ApplicationStatus.java (or similar package)
package org.springboot.exam;

public enum ApplicationStatus {
    APPLIED,       // User has applied for the exam
    REGISTERED,    // Application processed, user is fully registered
    COMPLETED,     // User has completed the exam (after it moves from STARTED to FINISHED)
    FAILED,        // User failed the exam (optional)
    CANCELLED      // User cancelled their application
}