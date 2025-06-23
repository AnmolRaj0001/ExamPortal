package org.springboot.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminLoginRepository extends JpaRepository<AdminLogin, Long> {
	// AdminLoginRepository.java
	Optional<AdminLogin> findByEmailAndPassword(String email, String password);

}

