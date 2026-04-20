package com.internship.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Student Response
 * Used for returning student profile information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
    
    private Long id;
    private String name;
    private String email;
    private String branch;
    private Integer year;
    private Double cgpa;
    private String phone;
    private LocalDateTime createdAt;
    private String role;
    
    // Simplified skill list (just names)
    private List<String> skills;
    
    // Simplified interest list (just areas)
    private List<String> interests;
}
