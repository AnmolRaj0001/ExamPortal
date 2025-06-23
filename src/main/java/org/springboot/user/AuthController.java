package org.springboot.user;

import org.springboot.userprofiles.UserProfileRepository; // Import UserProfileRepository
import org.springboot.userprofiles.UserProfiles; // Import UserProfiles
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
// CORRECTED: Set explicit allowed origin for CORS instead of "*" when allowCredentials is true.
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") 
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // Initialize logger

	@Autowired
    private final OtpStorageRepository otpStorageRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired // Autowire UserProfileRepository
    private UserProfileRepository userProfileRepository;
    
    // This map stores active user sessions in memory.
    // Key: User ID (Long), Value: Session ID (String - generated UUID for the current session)
    // NOTE: For true active logout across multiple server instances or after restarts,
    // this should be replaced with a persistent session store like Redis.
    private static final Map<Long, String> activeUserSessions = new ConcurrentHashMap<>();

    AuthController(OtpStorageRepository otpStorageRepository) {
        this.otpStorageRepository = otpStorageRepository;
    }

    // 1. Send OTP to email (used for registration or forgot password)
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) throws UnsupportedEncodingException {
        if (userRepo.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        String response = otpService.generateAndSendOtp(email);
        return ResponseEntity.ok(response);
    }
    // 2. Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpRequest otpRequest) {

    	 // Mobile verification: if mobile is present, bypass OTP verification
        if (otpRequest.getMobile() != null && !otpRequest.getMobile().isEmpty()) {
            return ResponseEntity.ok("Mobile OTP Verified (bypassed)");
        }


        boolean isValid = otpService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());
        if (isValid) {
            return ResponseEntity.ok("OTP Verified");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        }
    }

    public static class OtpRequest {
    	private String mobile;
        private String email;
        private String otp;

     // getters and setters
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getOtp() {
			return otp;
		}
		public void setOtp(String otp) {
			this.otp = otp;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}


    }


    // 3. Register User (after verifying OTP)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws UnsupportedEncodingException {
        System.out.println("Attempting to register user: " + user.getEmail() + ", name: " + user.getName());

        if (userRepo.existsByEmail(user.getEmail())) {
            System.out.println("Email already registered: " + user.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }

        user.setStatus("Active");
        // Ensure profileCompleted defaults to false upon registration if not already handled in User entity constructor
        user.setProfileCompleted(false); // Explicitly set to false on registration
        User savedUser = userRepo.save(user);

        if (savedUser.getId() != null) {
            System.out.println("User saved with id: " + savedUser.getId());
            emailService.sendSuccessEmail(user.getEmail());
            return ResponseEntity.ok("Registration Successful");
        } else {
            System.out.println("Failed to save user: " + user.getEmail());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }


    // 4. Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = null;

        if (loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()) {
            user = userRepo.findByEmail(loginRequest.getEmail());
        } else if (loginRequest.getMobile() != null && !loginRequest.getMobile().isEmpty()) {
            user = userRepo.findByMobile(loginRequest.getMobile());
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found with provided credentials.");
        }
        // IMPORTANT: In a real application, use a proper password encoder (e.g., BCryptPasswordEncoder)
        // For demonstration, direct comparison is used.
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password");
        }
        
        // --- LOGIC FOR CONCURRENT LOGIN HANDLING (UPDATED CONCEPTUALLY) ---
        // Get the current active session ID for this user, if any
        String existingSessionId = activeUserSessions.get(user.getId());

        // Check if the user is already logged in (based on the in-memory map)
        // If the user is already logged in AND forceLogin is NOT requested from the frontend
        if (existingSessionId != null && !loginRequest.isForceLogin()) {
            // Return a conflict status (409) with a specific message for the frontend
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "USER_ALREADY_LOGGED_IN_ELSEWHERE"));
        }

        // If forceLogin is true OR user is not currently in activeUserSessions, proceed to update/create session
        // Generate a new unique session ID for the new login attempt
        String newSessionId = java.util.UUID.randomUUID().toString();
        
        // --- CONCEPTUAL: If using a persistent store (e.g., Redis) ---
        // You would save newSessionId to Redis for this user.
        // Also, if existingSessionId was not null, you would explicitly invalidate
        // that old session in Redis here.
        // For example: redisTemplate.opsForValue().set("user_session:" + user.getId(), newSessionId);
        // And if an old session existed: redisTemplate.delete("user_session:" + user.getId() + ":" + existingSessionId);

        // --- CONCEPTUAL: If using WebSockets for active logout ---
        // If existingSessionId was not null (meaning a previous session existed),
        // you would send a "logout" message to the client associated with existingSessionId
        // via your WebSocket server.
        // Example: WebSocketSessionManager.sendMessage(existingSessionId, "logout");
        
        // Update the active session for this user in the in-memory map.
        // In a persistent store scenario, this would be a cache update after Redis.
        activeUserSessions.put(user.getId(), newSessionId);
        // --- END OF CONCEPTUAL LOGIC ---

        // Create a UserDTO to send back (don't send the password!)
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setMobile(user.getMobile());
        userDTO.setProfileCompleted(user.isProfileCompleted()); 
        // Include profile completed status

        // Log the profile completed status for debugging
        logger.info("User {} (ID: {}) profileCompleted status: {}", user.getEmail(), user.getId(), user.isProfileCompleted());


        // Fetch registrationId from UserProfile if it exists
        // Use findByUserId to get the user profile by user ID
        Optional<UserProfiles> userProfileOptional = userProfileRepository.findByUserId(user.getId());
        userProfileOptional.ifPresent(profile -> {
            userDTO.setRegistrationId(profile.getRegistrationId());
            // If UserProfile exists, also set college info from it if needed for login response
            if (profile.getCollege() != null) {
                userDTO.setCollegeId(String.valueOf(profile.getCollege().getId()));
                userDTO.setCollegeName(profile.getCollege().getCollegeName());
            }
        });


        return ResponseEntity.ok(userDTO); // Return user data on success
    }
    
    // Endpoint to handle user logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam Long userId) {
        // Remove the user's session from the active sessions map
        // In a persistent store scenario, you would also remove from Redis here.
        if (activeUserSessions.containsKey(userId)) {
            activeUserSessions.remove(userId);
            logger.info("User ID {} logged out.", userId);
            return ResponseEntity.ok("Logged out successfully.");
        } else {
            // If user ID is not found, they might not have been logged in or their session expired
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not logged in or session expired.");
        }
    }

    // Data Transfer Object (DTO) for User information sent to frontend
    public static class UserDTO {
        private Long id;
        private String name;
        private String email;
        private String mobile;
        private String registrationId; // Add registrationId
        private boolean profileCompleted; // Add profileCompleted flag
        private String collegeId; // Add college ID
        private String collegeName; // Add college Name

        // Getters and setters for all fields
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getRegistrationId() { return registrationId; }
        public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
        public boolean isProfileCompleted() { return profileCompleted; }
        public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }
		public String getCollegeId() { return collegeId; }
		public void setCollegeId(String collegeId) {
			this.collegeId = collegeId;
		}
		public String getCollegeName() {
			return collegeName;
		}
		public void setCollegeName(String collegeName) {
			this.collegeName = collegeName;
		}
    }

    // Data Transfer Object (DTO) for Login Request from frontend
    public static class LoginRequest {
        private String email;
        private String mobile;
        private String password;
        private boolean forceLogin; // Flag to indicate if a login should force out other sessions

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        // Corrected getter for forceLogin, returns the actual field value
        public boolean isForceLogin() {
			return forceLogin; 
		}
		// Setter for forceLogin field
		public void setForceLogin(boolean forceLogin) { 
			this.forceLogin = forceLogin;
		}
    }


    // 5. Forgot Password: Step 1 - Send OTP (existing functionality)
    @PostMapping("/forgot-password/send-otp")
    public String forgotPasswordOtp(@RequestParam String email) throws UnsupportedEncodingException {
        if (!userRepo.existsByEmail(email)) {
            return "Email not found";
        }
        return otpService.generateAndSendOtp(email);
    }

    // 6. Forgot Password: Step 2 - Verify OTP (existing functionality)
    @PostMapping("/forgot-password/verify-otp")
    public String verifyForgotOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        return isValid ? "OTP Verified" : "Invalid OTP";
    }

    // 7. Forgot Password: Step 3 - Set new password (existing functionality)
    @PostMapping("/forgot-password/reset")
    public String resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        Optional<User> user = Optional.of(userRepo.findByEmail(email));
        if (user.isPresent()) {
            user.get().setPassword(newPassword);
            userRepo.save(user.get());
            return "Password reset successfully";
        } else {
            return "Email not found";
        }
    }
}
