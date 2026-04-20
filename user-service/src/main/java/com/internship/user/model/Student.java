package com.internship.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Student Entity - Represents a student profile in the system
 * 
 * This entity stores basic information about students including their
 * personal details, academic information, and registration timestamp.
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(length = 50)
    private String branch;  // e.g., Computer Science, Mechanical, etc.
    
    private Integer year;  // Current year of study (1-4)
    
    private Double cgpa;  // CGPA out of 10.00
    
    @Column(length = 15)
    private String phone;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, length = 20)
    private String role; // STUDENT, ADMIN
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // One student can have multiple education records
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educationList = new ArrayList<>();
    
    // One student can have multiple skills
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();
    
    // One student can have multiple interests
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
