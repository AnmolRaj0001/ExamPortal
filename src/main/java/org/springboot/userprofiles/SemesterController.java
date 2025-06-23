package org.springboot.userprofiles;

import java.util.List;
import java.util.stream.Collectors;

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

@RestController
@RequestMapping("/api/admin/semesters")
@CrossOrigin(origins = "http://localhost:3000")
public class SemesterController {

    private static final Logger logger = LoggerFactory.getLogger(SemesterController.class);

    @Autowired
    private SemesterService semesterService;

    @GetMapping
    public ResponseEntity<List<Semester>> getAllSemesters() {
        try {
            logger.info("Fetching all semesters for admin view.");
            List<Semester> semesters = semesterService.findAllSemesters();
            if (semesters.isEmpty()) {
                logger.warn("No semesters found for admin view.");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(semesters, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all semesters: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Semester> createSemester(@RequestBody Semester semester) {
        try {
            logger.info("Creating new semester: {}", semester.getSemesterName());
            Semester savedSemester = semesterService.saveSemester(semester);
            return new ResponseEntity<>(savedSemester, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating semester: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dropdowns/semesters")
    public ResponseEntity<List<DropdownDto>> getSemesterDropdowns() {
        logger.info("Fetching semesters for dropdown from SemesterController.");
        List<DropdownDto> dropdowns = semesterService.findAllSemesters().stream()
                .map(semester -> new DropdownDto(semester.getId(), semester.getSemesterName()))
                .collect(Collectors.toList());
        if (dropdowns.isEmpty()) {
            logger.warn("No semesters found for dropdown from SemesterController.");
        }
        return ResponseEntity.ok(dropdowns);
    }
}