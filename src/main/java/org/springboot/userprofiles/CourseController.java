package org.springboot.userprofiles;

import org.springboot.userprofiles.dto.DropdownDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/courses")
@CrossOrigin(origins = "http://localhost:3000")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        try {
            logger.info("Fetching all courses for admin view.");
            List<Course> courses = courseService.findAllCourses();
            if (courses.isEmpty()) {
                logger.warn("No courses found for admin view.");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all courses: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        try {
            logger.info("Creating new course: {}", course.getCourseName());
            Course savedCourse = courseService.saveCourse(course);
            return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating course: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dropdowns/courses")
    public ResponseEntity<List<DropdownDto>> getCourseDropdowns() {
        logger.info("Fetching courses for dropdown from CourseController.");
        List<DropdownDto> dropdowns = courseService.findAllCourses().stream()
                .map(course -> new DropdownDto(course.getId(), course.getCourseName()))
                .collect(Collectors.toList());
        if (dropdowns.isEmpty()) {
            logger.warn("No courses found for dropdown from CourseController.");
        }
        return ResponseEntity.ok(dropdowns);
    }
}