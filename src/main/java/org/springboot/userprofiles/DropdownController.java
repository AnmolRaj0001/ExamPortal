package org.springboot.userprofiles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger; // <--- ADD THIS IMPORT
import org.slf4j.LoggerFactory; // <--- ADD THIS IMPORT

@RestController
@RequestMapping("/api/dropdowns")
public class DropdownController {

    private static final Logger logger = LoggerFactory.getLogger(DropdownController.class); // <--- ADD THIS LINE

    @Autowired
    private DropdownService dropdownService;

    @GetMapping("/colleges")
    public ResponseEntity<List<College>> getColleges() {
        logger.info("Fetching all colleges for dropdown."); // <--- ADD THIS LOG
        List<College> colleges = dropdownService.getAllColleges();
        if (colleges.isEmpty()) {
            logger.warn("No colleges found for dropdown."); // <--- ADD THIS LOG
        }
        return ResponseEntity.ok(colleges);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCourses() {
        logger.info("Fetching all courses for dropdown."); // <--- ADD THIS LOG
        List<Course> courses = dropdownService.getAllCourses();
        if (courses.isEmpty()) {
            logger.warn("No courses found for dropdown."); // <--- ADD THIS LOG
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/semesters")
    public ResponseEntity<List<Semester>> getSemesters() {
        logger.info("Fetching all semesters for dropdown."); // <--- ADD THIS LOG
        List<Semester> semesters = dropdownService.getAllSemesters();
        if (semesters.isEmpty()) {
            logger.warn("No semesters found for dropdown."); // <--- ADD THIS LOG
        }
        return ResponseEntity.ok(semesters);
    }
}