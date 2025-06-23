package org.springboot.userprofiles;

import org.springboot.user.User;
import org.springboot.user.UserRepository;
import org.springboot.userprofiles.dto.CollegeDto; // Import CollegeDto
import org.springboot.userprofiles.dto.UserProfileDto;
import org.springboot.user.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CollegeRepository collegeRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final EmailService emailService;

    @Autowired
    public UserProfileService(UserRepository userRepository, UserProfileRepository userProfileRepository,
                              CollegeRepository collegeRepository, CourseRepository courseRepository,
                              SemesterRepository semesterRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.collegeRepository = collegeRepository;
        this.courseRepository = courseRepository;
        this.semesterRepository = semesterRepository;
        this.emailService = emailService;
    }

    // Existing method to get a user profile DTO
    public UserProfileDto getUserProfileDtoByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            logger.warn("User not found for ID: {}", userId);
            return null; // Or throw an exception
        }
        User user = userOptional.get();

        Optional<UserProfiles> profileOptional = userProfileRepository.findByUser(user);
        UserProfileDto dto = new UserProfileDto();

        // Pre-fill user details from User entity
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());

        if (profileOptional.isPresent()) {
            UserProfiles profile = profileOptional.get();
            dto.setId(profile.getId()); // UserProfiles ID
            dto.setDateOfBirth(profile.getDateOfBirth());
            dto.setGender(profile.getGender());
            dto.setFatherName(profile.getFatherName());
            dto.setProfilePhotoUrl(profile.getProfilePhotoUrl());
            dto.setRegistrationId(profile.getRegistrationId());

            // Permanent Address
            dto.setHouseNo(profile.getHouseNo());
            dto.setStreetLane(profile.getStreetLane());
            dto.setLandmark(profile.getLandmark());
            dto.setCity(profile.getCity());
            dto.setPincode(profile.getPincode());
            dto.setState(profile.getState());
            dto.setCountry(profile.getCountry());

            // Present Address
            dto.setSameAsPermanent(profile.isSameAsPermanent());
            dto.setPresentHouseNo(profile.getPresentHouseNo());
            dto.setPresentStreetLane(profile.getPresentStreetLane());
            dto.setPresentLandmark(profile.getPresentLandmark());
            dto.setPresentCity(profile.getPresentCity());
            dto.setPresentPincode(profile.getPresentPincode()); // Corrected to use presentPincode
            dto.setPresentState(profile.getPresentState());
            dto.setPresentCountry(profile.getPresentCountry());

            // --- CHANGE STARTS HERE ---
            // Populate the nested CollegeDto object
            if (profile.getCollege() != null) {
                dto.setCollege(new CollegeDto(profile.getCollege().getId(), profile.getCollege().getCollegeName()));
            }
            // --- CHANGE ENDS HERE ---

            if (profile.getCourse() != null) {
                dto.setCourseName(profile.getCourse().getCourseName());
                dto.setCourseId(profile.getCourse().getId());
            }
            if (profile.getSemester() != null) {
                dto.setSemesterName(profile.getSemester().getSemesterName());
                dto.setSemesterId(profile.getSemester().getId());
            }
        }
        return dto;
    }


    // Existing method to save or update user profile
    @Transactional
    public UserProfileDto saveOrUpdateUserProfile(Long userId, UserProfileDto userProfileDto) throws Exception {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new Exception("User not found with ID: " + userId);
        }
        User user = userOptional.get();

        Optional<UserProfiles> existingProfileOptional = userProfileRepository.findByUser(user);
        UserProfiles profile;
        boolean isNewProfile = false;

        if (existingProfileOptional.isPresent()) {
            profile = existingProfileOptional.get();
            logger.info("Updating existing user profile for user ID: {}", userId);
        } else {
            profile = new UserProfiles();
            profile.setUser(user); // Link the profile to the user
            isNewProfile = true;
            logger.info("Creating new user profile for user ID: {}", userId);
        }

        // Map DTO to entity
        profile.setDateOfBirth(userProfileDto.getDateOfBirth());
        profile.setGender(userProfileDto.getGender());
        profile.setFatherName(userProfileDto.getFatherName());

        // Handle profile photo URL if it exists in DTO (for loading existing)
        // If a new photo is uploaded, it's handled by saveProfilePhoto method.
        if (userProfileDto.getProfilePhotoUrl() != null) {
            profile.setProfilePhotoUrl(userProfileDto.getProfilePhotoUrl());
        }

        // Permanent Address
        profile.setHouseNo(userProfileDto.getHouseNo());
        profile.setStreetLane(userProfileDto.getStreetLane());
        profile.setLandmark(userProfileDto.getLandmark());
        profile.setCity(userProfileDto.getCity());
        profile.setPincode(userProfileDto.getPincode());
        profile.setState(userProfileDto.getState());
        profile.setCountry(userProfileDto.getCountry());

        // Present Address
        profile.setSameAsPermanent(userProfileDto.isSameAsPermanent());
        if (userProfileDto.isSameAsPermanent()) {
            profile.setPresentHouseNo(userProfileDto.getHouseNo());
            profile.setPresentStreetLane(userProfileDto.getStreetLane());
            profile.setPresentLandmark(userProfileDto.getLandmark());
            profile.setPresentCity(userProfileDto.getCity());
            profile.setPresentPincode(userProfileDto.getPincode());
            profile.setPresentState(userProfileDto.getState());
            profile.setPresentCountry(userProfileDto.getCountry());
        } else {
            profile.setPresentHouseNo(userProfileDto.getPresentHouseNo());
            profile.setPresentStreetLane(userProfileDto.getPresentStreetLane());
            profile.setPresentLandmark(userProfileDto.getPresentLandmark());
            profile.setPresentCity(userProfileDto.getPresentCity());
            profile.setPresentPincode(userProfileDto.getPresentPincode());
            profile.setPresentState(userProfileDto.getPresentState());
            profile.setPresentCountry(userProfileDto.getPresentCountry());
        }

        // College, Course, Semester
        // --- CHANGE STARTS HERE (related to saving incoming CollegeDto) ---
        // If the incoming DTO has a nested CollegeDto, use its ID.
        if (userProfileDto.getCollege() != null && userProfileDto.getCollege().getId() != null) {
            collegeRepository.findById(userProfileDto.getCollege().getId()).ifPresent(profile::setCollege);
        } else if (userProfileDto.getCollegeId() != null) { // Fallback for old flat collegeId if still used
             collegeRepository.findById(userProfileDto.getCollegeId()).ifPresent(profile::setCollege);
        } else {
            profile.setCollege(null);
        }
        // --- CHANGE ENDS HERE ---

        if (userProfileDto.getCourseId() != null) {
            courseRepository.findById(userProfileDto.getCourseId()).ifPresent(profile::setCourse);
        } else {
            profile.setCourse(null);
        }
        if (userProfileDto.getSemesterId() != null) {
            semesterRepository.findById(userProfileDto.getSemesterId()).ifPresent(profile::setSemester);
        } else {
            profile.setSemester(null);
        }

        // Generate unique registration ID only for new profiles
        if (isNewProfile || profile.getRegistrationId() == null || profile.getRegistrationId().isEmpty()) {
            String newRegistrationId = generateUniqueRegistrationId();
            profile.setRegistrationId(newRegistrationId);
            logger.info("Generated new registration ID {} for user ID: {}", newRegistrationId, userId);
        }

        UserProfiles savedProfile = userProfileRepository.save(profile);

        // Update the 'profileCompleted' flag in the User entity
        user.setProfileCompleted(true);
        userRepository.save(user); // Save the updated User entity
        logger.info("User profileCompletion status updated to true for user ID: {}", userId);

        // Send registration confirmation email with the registration ID
        // Ensure that user.getName() is not null before sending
        String userNameForEmail = user.getName() != null ? user.getName() : "Valued User";
        if (isNewProfile) {
            // Send registration confirmation email with the registration ID for new profiles
            emailService.sendRegistrationConfirmation(user.getEmail(), userNameForEmail, savedProfile.getRegistrationId());
            logger.info("Registration confirmation email sent to user ID: {}", userId);
        } else {
            // Send profile update confirmation email for existing profiles
            emailService.sendProfileUpdateConfirmation(user.getEmail(), userNameForEmail); // Assuming you create this method
            logger.info("Profile update confirmation email sent to user ID: {}", userId);
        }

        // Convert saved entity back to DTO
        return convertToDto(savedProfile, user); // Pass user to convertToDto to get name, email, mobile
    }

    // Helper method to convert UserProfiles entity to UserProfileDto
    private UserProfileDto convertToDto(UserProfiles profile, User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(user.getId());
        dto.setName(user.getName()); // Get from User entity
        dto.setEmail(user.getEmail()); // Get from User entity
        dto.setMobile(user.getMobile()); // Get from User entity

        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setGender(profile.getGender());
        dto.setFatherName(profile.getFatherName());
        dto.setProfilePhotoUrl(profile.getProfilePhotoUrl());
        dto.setRegistrationId(profile.getRegistrationId());

        dto.setHouseNo(profile.getHouseNo());
        dto.setStreetLane(profile.getStreetLane());
        dto.setLandmark(profile.getLandmark());
        dto.setCity(profile.getCity());
        dto.setPincode(profile.getPincode());
        dto.setState(profile.getState());
        dto.setCountry(profile.getCountry());

        dto.setSameAsPermanent(profile.isSameAsPermanent());
        dto.setPresentHouseNo(profile.getPresentHouseNo());
        dto.setPresentStreetLane(profile.getPresentStreetLane());
        dto.setPresentLandmark(profile.getPresentLandmark());
        dto.setPresentCity(profile.getPresentCity());
        dto.setPresentPincode(profile.getPresentPincode());
        dto.setPresentState(profile.getPresentState());
        dto.setPresentCountry(profile.getPresentCountry());

        // --- CHANGE STARTS HERE ---
        // Populate the nested CollegeDto object
        if (profile.getCollege() != null) {
            dto.setCollege(new CollegeDto(profile.getCollege().getId(), profile.getCollege().getCollegeName()));
        }
        // --- CHANGE ENDS HERE ---

        if (profile.getCourse() != null) {
            dto.setCourseName(profile.getCourse().getCourseName());
            dto.setCourseId(profile.getCourse().getId());
        }
        if (profile.getSemester() != null) {
            dto.setSemesterName(profile.getSemester().getSemesterName());
            dto.setSemesterId(profile.getSemester().getId());
        }
        return dto;
    }


    // New method to check if profile is completed
    public boolean isProfileCompleted(Long userId) {
        return userRepository.findById(userId)
                .map(User::isProfileCompleted)
                .orElse(false);
    }

    // New method to delete a user profile and reset user's profileCompleted status
    @Transactional // Ensures both operations succeed or rollback
    public boolean deleteUserProfile(Long userId) {
        logger.info("Attempting to delete user profile for user ID: {}", userId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            logger.warn("User not found for ID: {}", userId);
            return false;
        }

        User user = userOptional.get();

        // Log current profile status before deletion
        logger.debug("User {} profileCompleted status before deletion: {}", userId, user.isProfileCompleted());

        // 1. Find and delete the UserProfile
        Optional<UserProfiles> userProfileOptional = userProfileRepository.findByUser(user);
        if (userProfileOptional.isPresent()) {
            UserProfiles profileToDelete = userProfileOptional.get();
            // Optionally, if profilePhotoUrl refers to a physical file, delete it here
            // (e.g., if you store files on disk, add logic to delete from filesystem)

            userProfileRepository.delete(profileToDelete);
            logger.info("Successfully deleted UserProfile entity for user ID: {}", userId);

            // Explicitly clear the in-memory reference to the UserProfile from the User entity
            // This is good practice for managing object state, though cascade type might handle it.
            user.setUserProfile(null);


            // 2. Reset the profileCompleted flag in the User table
            user.setProfileCompleted(false);
            userRepository.save(user); // Save the updated User entity
            logger.info("Successfully reset profileCompleted flag to FALSE for user ID: {}", userId);
            logger.debug("User {} profileCompleted status after deletion: {}", userId, user.isProfileCompleted());

            return true;
        } else {
            // No profile found to delete for this user.
            // However, we still need to ensure the user's profileCompleted flag is false,
            // in case it was set to true previously (e.g., by manual DB manipulation).
            logger.warn("No user profile found for user ID: {} to delete. Ensuring user profileCompleted flag is false.", userId);

            if (user.isProfileCompleted()) {
                user.setProfileCompleted(false);
                userRepository.save(user);
                logger.info("Corrected profileCompleted flag to FALSE for user ID: {} as no profile was found.", userId);
            }
            return false; // Still return false as no profile was actually deleted
        }
    }


    // Helper method for generating unique registration ID
    private String generateUniqueRegistrationId() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (userProfileRepository.existsByRegistrationId(uuid)); // Check uniqueness against UserProfiles
        return uuid;
    }

    // Method for saving profile photo
    private static final String UPLOAD_DIR = "uploads/profile_photos/"; // Base upload directory

    @Transactional
    public String saveProfilePhoto(Long userId, MultipartFile file) throws IOException {
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Resolve file name (e.g., userId_profile.jpg)
        String fileName = userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg"; // Use unique ID to prevent overwrites
        Path filePath = uploadPath.resolve(fileName);

        // Copy file to target location
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update user profile with photo URL
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IOException("User not found for photo upload: " + userId);
        }
        User user = userOptional.get();

        // Ensure UserProfile exists or create it
        Optional<UserProfiles> profileOptional = userProfileRepository.findByUser(user);
        UserProfiles userProfile;
        if (profileOptional.isPresent()) {
            userProfile = profileOptional.get();
        } else {
            userProfile = new UserProfiles();
            userProfile.setUser(user);
            // Initialize other fields if creating new profile to avoid nulls, or ensure they are nullable in entity
            userProfile.setRegistrationId(generateUniqueRegistrationId()); // Generate if new profile
        }

        userProfile.setProfilePhotoUrl("/" + UPLOAD_DIR + fileName); // Store relative URL
        userProfileRepository.save(userProfile);

        // Also update user's profileCompleted status if a profile was created just for the photo
        user.setProfileCompleted(true);
        userRepository.save(user);

        return userProfile.getProfilePhotoUrl();
    }
    
    
    public long countAllUserProfiles() {
        return userProfileRepository.count();
    }

    @Transactional(readOnly = true) // Use @Transactional for fetching related entities
    public List<UserProfileDto> getAllUserProfiles() {
        // Use findAll() and then stream to convert to DTO
        // Ensure lazy loaded relationships are fetched within the transaction if needed
        return userProfileRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert UserProfiles entity to UserProfileDto
    private UserProfileDto convertToDto(UserProfiles userProfile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(userProfile.getId());

        // Populate User details from the linked User entity
        if (userProfile.getUser() != null) {
            dto.setUserId(userProfile.getUser().getId());
            dto.setName(userProfile.getUser().getName());
            dto.setEmail(userProfile.getUser().getEmail());
            dto.setMobile(userProfile.getUser().getMobile());
        }

        // Personal Details
        dto.setDateOfBirth(userProfile.getDateOfBirth());
        dto.setGender(userProfile.getGender());
        dto.setFatherName(userProfile.getFatherName());
        dto.setProfilePhotoUrl(userProfile.getProfilePhotoUrl());

        // Permanent Address
        dto.setHouseNo(userProfile.getHouseNo());
        dto.setStreetLane(userProfile.getStreetLane());
        dto.setLandmark(userProfile.getLandmark());
        dto.setCity(userProfile.getCity());
        dto.setPincode(userProfile.getPincode());
        dto.setState(userProfile.getState());
        dto.setCountry(userProfile.getCountry());

        // Present Address
        
        dto.setPresentHouseNo(userProfile.getPresentHouseNo());
        dto.setPresentStreetLane(userProfile.getPresentStreetLane());
        dto.setPresentLandmark(userProfile.getPresentLandmark());
        dto.setPresentCity(userProfile.getPresentCity());
        dto.setPresentPincode(userProfile.getPresentPincode());
        dto.setPresentState(userProfile.getPresentState());
        dto.setPresentCountry(userProfile.getPresentCountry());

        // Populate College details from the linked College entity
        if (userProfile.getCollege() != null) {
            dto.setCollegeId(userProfile.getCollege().getId());
        }

        return dto;
    }
}
