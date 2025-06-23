package org.springboot.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByMobile(String mobile);
    boolean existsByEmail(String email);
}
