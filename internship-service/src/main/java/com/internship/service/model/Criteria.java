package com.internship.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "criteria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Criteria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id")
    @JsonIgnore
    private Internship internship;
    
    private Double minCgpa;
    
    @Column(name = "eligible_branches", length = 200)
    private String eligibleBranches;  // Comma-separated: "CSE,IT,ECE"
    
    @Column(name = "min_year")
    private Integer minYear;  // Minimum year of study
}
