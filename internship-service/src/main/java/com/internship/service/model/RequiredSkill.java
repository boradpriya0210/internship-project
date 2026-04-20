package com.internship.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "required_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequiredSkill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id")
    @JsonIgnore
    private Internship internship;
    
    @Column(name = "skill_name", length = 50)
    private String skillName;
    
    @Column(name = "min_level", length = 20)
    private String minLevel;  // Beginner, Intermediate, Advanced
    
    private Integer priority;  // 1-5, higher is more important
}
