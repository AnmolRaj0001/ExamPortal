package org.springboot.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    @Autowired
    private AdminLoginRepository adminRepo;

    public AdminLogin login(String email, String password) {
        return adminRepo.findByEmailAndPassword(email, password).orElse(null);
    }
    
    public AdminLogin createAdmin(AdminLogin admin) {
        // You might want to add validation here before saving
        // e.g., check if email already exists
        return adminRepo.save(admin);
    }
    
    public List<AdminLogin> getAllAdmins() {
        return adminRepo.findAll();
    }
}