package com.internship.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Education Entity - Represents educational background of a student
 */
@Entity
@Table(name = "education")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;
    
    @Column(length = 100)
    private String degree;  // e.g., B.Tech, M.Tech, 12th, etc.
    
    @Column(length = 200)
    private String institution;
    
    private Integer year;  // Year of completion
    
    private Double percentage;
}
