package org.springboot.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpStorageRepository extends JpaRepository<OtpStorage, Long> {
    OtpStorage findByEmail(String email);
}
