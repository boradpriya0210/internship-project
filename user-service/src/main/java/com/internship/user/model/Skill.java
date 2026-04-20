package com.internship.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Skill Entity - Represents skills possessed by a student
 */
@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;
    
    @Column(name = "skill_name", length = 50)
    private String skillName;  // e.g., Java, Python, Communication, etc.
    
    @Column(name = "proficiency_level", length = 20)
    private String proficiencyLevel;  // Beginner, Intermediate, Advanced
}
