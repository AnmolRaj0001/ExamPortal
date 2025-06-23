package org.springboot.userprofiles;

import org.springboot.userprofiles.dto.CollegeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class CollegeService {

    private static final Logger logger = LoggerFactory.getLogger(CollegeService.class);

    @Autowired
    private CollegeRepository collegeRepository;

    public List<CollegeDto> getAllColleges() {
        return collegeRepository.findAll().stream()
                .map(college -> new CollegeDto(college.getId(), college.getCollegeName(), college.getCollegeLocation()))
                .collect(Collectors.toList());
    }

    public Optional<College> getCollegeById(Long id) {
        return collegeRepository.findById(id);
    }

    public College saveCollege(College college) {
        logger.info("Saving new college: {}", college.getCollegeName());
        return collegeRepository.save(college);
    }

    public College updateCollege(Long id, College collegeDetails) {
        College college = collegeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("College not found with id " + id));

        logger.info("Updating college with ID: {}", id);
        college.setCollegeCode(collegeDetails.getCollegeCode());
        college.setCollegeLocation(collegeDetails.getCollegeLocation());
        college.setCollegeName(collegeDetails.getCollegeName());
        college.setCollegeShortName(collegeDetails.getCollegeShortName());
        college.setCollegeWebsite(collegeDetails.getCollegeWebsite());

        return collegeRepository.save(college);
    }

    public void deleteCollege(Long id) {
        logger.info("Deleting college with ID: {}", id);
        collegeRepository.deleteById(id);
    }

    /**
     * NEW: Retrieves the total count of colleges.
     * @return A map containing the total count of colleges.
     */
    public Map<String, Long> getTotalCollegesCount() {
        long count = collegeRepository.count();
        logger.info("Fetched total colleges count: {}", count);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count); // Return as an object with 'count' field
        return response;
    }

    /**
     * NEW: Retrieves the most recently added colleges.
     * @return A list of recent College entities.
     */
    public List<College> getRecentColleges() {
        // This leverages the findTop5ByOrderByCreatedAtDesc method added to the repository
        List<College> recentColleges = collegeRepository.findTop5ByOrderByIdDesc();
        logger.info("Fetched {} recent colleges.", recentColleges.size());
        return recentColleges;
    }
}
