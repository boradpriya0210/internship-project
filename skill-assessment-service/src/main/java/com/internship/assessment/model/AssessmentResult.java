package com.internship.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessment_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "assessment_id", nullable = false)
    private Long assessmentId;
    
    private Integer score;
    
    @Column(name = "attempt_date", nullable = false, updatable = false)
    private LocalDateTime attemptDate;
    
    @PrePersist
    protected void onCreate() {
        attemptDate = LocalDateTime.now();
    }
}
