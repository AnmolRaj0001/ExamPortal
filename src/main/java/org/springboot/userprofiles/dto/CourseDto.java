package org.springboot.userprofiles.dto;

public class CourseDto {
    private Long id;
    private String name; // Maps to courseName in Course entity

    public CourseDto() {}
    public CourseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
