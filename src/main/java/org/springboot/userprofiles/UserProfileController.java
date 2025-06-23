package org.springboot.userprofiles;

import org.springboot.user.User;
import org.springboot.userprofiles.dto.CollegeDto;
import org.springboot.userprofiles.dto.CourseDto;
import org.springboot.userprofiles.dto.DropdownDto;
import org.springboot.userprofiles.dto.UserProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SemesterService semesterService;
    
    @Autowired
    private UserProfileRepository userProfileRepository;

    // Endpoint to get a user's profile (for pre-filling and display)
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        logger.info("Received GET request for user profile for userId: {}", userId);
        UserProfileDto userProfileDto = userProfileService.getUserProfileDtoByUserId(userId);
        if (userProfileDto != null) {
            return ResponseEntity.ok(userProfileDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint to check profile completion status (existing, though login will handle this now)
    @GetMapping("/{userId}/profile-status")
    public ResponseEntity<Map<String, Boolean>> getProfileCompletionStatus(@PathVariable Long userId) {
        logger.info("Checking profile status for userId: {}", userId);
        boolean isCompleted = userProfileService.isProfileCompleted(userId);
        logger.info("Profile status for userId {}: {}", userId, isCompleted);
        return ResponseEntity.ok(Map.of("profileCompleted", isCompleted));
    }

    // Changed from @PostMapping to @PutMapping for updating user profile
    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> saveUserProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileDto userProfileDto) {
        logger.info("Saving/updating user profile for userId: {}", userId);
        try {
            UserProfileDto savedProfile = userProfileService.saveOrUpdateUserProfile(userId, userProfileDto);
            return ResponseEntity.status(HttpStatus.OK).body(savedProfile); // Use OK for updates
        } catch (Exception e) {
            logger.error("Error saving/updating profile for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoints for independent dropdowns
    @GetMapping("/colleges/dropdowns")
    public ResponseEntity<List<DropdownDto>> getAllCollegesForDropdown() {
        logger.info("Received GET request for all colleges dropdowns.");
        List<DropdownDto> colleges = collegeService.getAllColleges().stream()
                .map(collegeDto -> new DropdownDto(collegeDto.getId(), collegeDto.getCollegeName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(colleges);
    }

    @GetMapping("/courses/dropdowns")
    public ResponseEntity<List<DropdownDto>> getAllCoursesForDropdown() {
        logger.info("Received GET request for all courses dropdowns.");
        List<DropdownDto> courses = courseService.findAllCourses().stream()
                .map(course -> new DropdownDto(course.getId(), course.getCourseName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/semesters/dropdowns")
    public ResponseEntity<List<DropdownDto>> getAllSemestersForDropdown() {
        logger.info("Received GET request for all semesters dropdowns.");
        List<DropdownDto> semesters = semesterService.findAllSemesters().stream()
                .map(semester -> new DropdownDto(semester.getId(), semester.getSemesterName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(semesters);
    }

    // Endpoint for file upload (existing functionality)
    @PostMapping("/{userId}/upload-profile-photo")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        logger.info("Received file upload request for userId: {}", userId);
        try {
            String fileUrl = userProfileService.saveProfilePhoto(userId, file);
            return ResponseEntity.ok(Map.of("profilePhotoUrl", fileUrl));
        } catch (IOException e) {
            logger.error("Error uploading profile photo for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Could not upload the file: " + file.getOriginalFilename() + "!"));
        }
    }
    
    @GetMapping("/user-profile-by-email/{email}")
    public ResponseEntity<UserProfileDto> getProfileByEmail(@PathVariable String email) {
        Optional<UserProfiles> userProfileOpt = userProfileRepository.findByUserEmail(email);

        if (userProfileOpt.isPresent()) {
            UserProfiles profile = userProfileOpt.get();
            User user = profile.getUser(); // Assuming you have @ManyToOne User user;
            UserProfileDto dto = convertToDto(profile, user);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    private UserProfileDto convertToDto(UserProfiles profile, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@GetMapping("/total")
    public ResponseEntity<Long> getTotalUserProfiles() {
        long totalUsers = userProfileService.countAllUserProfiles();
        return ResponseEntity.ok(totalUsers);
    }

    @GetMapping("/all") // Assuming you might have a general /api/users for all users, or create a new one
    public ResponseEntity<List<UserProfileDto>> getAllUserProfiles() {
        List<UserProfileDto> users = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(users);
    }
    
    @DeleteMapping("/{userId}/profile")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long userId) {
        logger.info("Received DELETE request for user profile for userId: {}", userId);
        boolean deleted = userProfileService.deleteUserProfile(userId);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found if profile/user not found
        }
    }
}
