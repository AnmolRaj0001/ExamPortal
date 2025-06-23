// Assuming a SemesterRepository.java existed, it would look like this:
package org.springboot.userprofiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
}