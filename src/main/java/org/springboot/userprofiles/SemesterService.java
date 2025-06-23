// Assuming a SemesterService.java existed, it would look like this:
package org.springboot.userprofiles;

import org.springboot.userprofiles.dto.DropdownDto; // If using, keep this import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SemesterService {

    @Autowired
    private SemesterRepository semesterRepository; // Assuming you have a SemesterRepository

    public List<Semester> findAllSemesters() {
        return semesterRepository.findAll();
    }

    public Semester saveSemester(Semester semester) {
        return semesterRepository.save(semester);
    }

    // If there were methods like getSemestersByCollegeAndCourse, they would be removed here.
}