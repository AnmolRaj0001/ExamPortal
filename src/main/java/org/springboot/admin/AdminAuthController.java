package org.springboot.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springboot.userprofiles.UserProfileService;
import org.springboot.userprofiles.dto.UserProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;
    
    @Autowired
     private UserProfileService userProfileService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLogin loginData) {
        AdminLogin admin = adminAuthService.login(loginData.getEmail(), loginData.getPassword());
        if (admin != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", admin.getId());
            response.put("name", admin.getName());
            response.put("email", admin.getEmail());
            // Future me image bhi ho to add karo
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password.");
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody AdminLogin newAdmin) {
        // Basic validation: Check if email already exists
        // This is a simple example, consider more robust validation in a real application
        if (adminAuthService.login(newAdmin.getEmail(), newAdmin.getPassword()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin with this email already exists.");
        }

        AdminLogin createdAdmin = adminAuthService.createAdmin(newAdmin);
        if (createdAdmin != null) {
            return ResponseEntity.ok("Admin created successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create admin.");
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllAdmins() {
        List<AdminLogin> admins = adminAuthService.getAllAdmins();

        // Convert AdminLogin entities to a more suitable DTO/Map for frontend
        List<Map<String, Object>> adminDtos = admins.stream().map(admin -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", admin.getId());
            dto.put("name", admin.getName());
            dto.put("email", admin.getEmail());
            dto.put("imageUrl", admin.getImageUrl());
            dto.put("createdAt", admin.getCreatedAt()); // Include creation timestamp if needed
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(adminDtos);
    }
    
    

//    @GetMapping
//    public ResponseEntity<List<UserProfileDto>> getAllCandidates() {
//        List<UserProfileDto> candidates = userProfileService.getAllUserProfiles();
//        return ResponseEntity.ok(candidates);
//    }
}