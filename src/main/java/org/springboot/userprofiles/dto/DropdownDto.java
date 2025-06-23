package org.springboot.userprofiles.dto;

public class DropdownDto {
    private Long id;
    private String name;

    public DropdownDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setters (if needed for deserialization, though not strictly required for this use case)
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
