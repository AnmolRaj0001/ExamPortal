package org.springboot.userprofiles;

import org.springboot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfiles, Long> {
	Optional<UserProfiles> findByUser(User user);
	Optional<UserProfiles> findByUserEmail(String email);
    Optional<UserProfiles> findByUserId(Long userId); // Find profile by associated user ID
    boolean existsByRegistrationId(String registrationId); // For unique ID generation
}
