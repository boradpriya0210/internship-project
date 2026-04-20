package com.internship.assessment.repository;

import com.internship.assessment.model.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
    List<AssessmentResult> findByStudentId(Long studentId);
    List<AssessmentResult> findByAssessmentId(Long assessmentId);
}
