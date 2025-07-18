package org.springboot.userprofiles;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long>{

	 List<College> findTop5ByOrderByIdDesc();


}