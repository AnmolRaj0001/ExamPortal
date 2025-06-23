package org.springboot.userprofiles.dto;

public class SemesterDto {
    private Long id;
    private String name; // Maps to semesterName in Semester entity

    public SemesterDto() {}
    public SemesterDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
