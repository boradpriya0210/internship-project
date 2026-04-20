package com.internship.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for Student Registration
 * Used for creating new student profiles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationDTO {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Branch is required")
    private String branch;
    
    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be between 1 and 4")
    @Max(value = 4, message = "Year must be between 1 and 4")
    private Integer year;
    
    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA must be between 0 and 10")
    @DecimalMax(value = "10.0", message = "CGPA must be between 0 and 10")
    private Double cgpa;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;
    
    // List of skill names
    private List<String> skills;
    
    // List of interest areas
    private List<String> interests;
}
