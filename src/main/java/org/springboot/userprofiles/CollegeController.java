package org.springboot.userprofiles;

import java.util.List;
import java.util.Map; // Import for Map
import java.util.stream.Collectors;

import org.springboot.userprofiles.dto.CollegeDto;
import org.springboot.userprofiles.dto.DropdownDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory


@RestController
@RequestMapping("/api/admin/colleges")
@CrossOrigin(origins = "http://localhost:3000")
public class CollegeController {

    private static final Logger logger = LoggerFactory.getLogger(CollegeController.class); // Initialize Logger

    @Autowired
    private CollegeService collegeService;

    // GET all colleges: http://localhost:8080/api/admin/colleges
    @GetMapping
    public List<CollegeDto> getAllColleges() {
        logger.info("Received GET request for all colleges.");
        return collegeService.getAllColleges();
    }

    // GET college by ID: http://localhost:8080/api/admin/colleges/{id}
    @GetMapping("/{id}")
    public ResponseEntity<College> getCollegeById(@PathVariable Long id) {
        logger.info("Received GET request for college by ID: {}", id);
        return collegeService.getCollegeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST create new college: http://localhost:8080/api/admin/colleges
    @PostMapping
    public ResponseEntity<College> createCollege(@RequestBody College college) {
        logger.info("Received POST request to create new college: {}", college.getCollegeName());
        College savedCollege = collegeService.saveCollege(college);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCollege);
    }

    // PUT update college: http://localhost:8080/api/admin/colleges/{id}
    @PutMapping("/{id}")
    public ResponseEntity<College> updateCollege(@PathVariable Long id, @RequestBody College college) {
        logger.info("Received PUT request to update college with ID: {}", id);
        return collegeService.getCollegeById(id)
                .map(existingCollege -> {
                    existingCollege.setCollegeName(college.getCollegeName());
                    existingCollege.setCollegeCode(college.getCollegeCode()); // Ensure all fields are updated
                    existingCollege.setCollegeLocation(college.getCollegeLocation());
                    existingCollege.setCollegeShortName(college.getCollegeShortName());
                    existingCollege.setCollegeWebsite(college.getCollegeWebsite());
                    return ResponseEntity.ok(collegeService.saveCollege(existingCollege));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE college: http://localhost:8080/api/admin/colleges/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollege(@PathVariable Long id) {
        logger.info("Received DELETE request for college with ID: {}", id);
        collegeService.deleteCollege(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to get all colleges for dropdowns.
    @GetMapping("/dropdowns")
    public ResponseEntity<List<DropdownDto>> getCollegeDropdowns() {
        logger.info("Received GET request for college dropdowns.");
        List<DropdownDto> dropdowns = collegeService.getAllColleges().stream()
                .map(collegeDto -> new DropdownDto(collegeDto.getId(), collegeDto.getCollegeName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dropdowns);
    }

    /**
     * NEW: Endpoint to get the total count of colleges for the admin dashboard.
     * Endpoint: GET http://localhost:8080/api/admin/colleges/count
     * @return A ResponseEntity containing a map with the count (e.g., {"count": 10}).
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalCollegesCount() {
        logger.info("Received request for total colleges count.");
        Map<String, Long> count = collegeService.getTotalCollegesCount();
        return ResponseEntity.ok(count);
    }

    /**
     * NEW: Endpoint to get the most recently added colleges for the admin dashboard.
     * Endpoint: GET http://localhost:8080/api/admin/colleges/recent
     * @return A ResponseEntity containing a list of College entities.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<College>> getRecentColleges() {
        logger.info("Received request for recent colleges.");
        List<College> recentColleges = collegeService.getRecentColleges();
        return ResponseEntity.ok(recentColleges);
    }
}
