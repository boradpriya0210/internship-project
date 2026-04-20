package com.internship.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Internship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 100)
    private String company;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Integer duration;  // Duration in months
    
    private Double stipend;
    
    @Column(length = 100)
    private String location;
    
    @Column(length = 50)
    private String type;  // Remote, On-site, Hybrid
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "internship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequiredSkill> requiredSkills = new ArrayList<>();
    
    @OneToOne(mappedBy = "internship", cascade = CascadeType.ALL, orphanRemoval = true)
    private Criteria criteria;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
