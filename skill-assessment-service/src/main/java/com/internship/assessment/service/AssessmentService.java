package com.internship.assessment.service;

import com.internship.assessment.dto.SubmitAssessmentDTO;
import com.internship.assessment.model.Assessment;
import com.internship.assessment.model.AssessmentResult;
import com.internship.assessment.model.SkillLevel;
import com.internship.assessment.repository.AssessmentRepository;
import com.internship.assessment.repository.AssessmentResultRepository;
import com.internship.assessment.repository.SkillLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentService {
    
    private final AssessmentRepository assessmentRepository;
    private final AssessmentResultRepository resultRepository;
    private final SkillLevelRepository skillLevelRepository;
    
    public List<Assessment> getAllAssessments() {
        log.info("Fetching all assessments");
        return assessmentRepository.findAll();
    }
    
    public Assessment getAssessmentById(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + id));
    }
    
    @Transactional
    public AssessmentResult submitAssessment(SubmitAssessmentDTO dto) {
        log.info("Submitting assessment for student: {}, assessment: {}", 
                dto.getStudentId(), dto.getAssessmentId());
        
        Assessment assessment = getAssessmentById(dto.getAssessmentId());
        
        // Save assessment result
        AssessmentResult result = new AssessmentResult();
        result.setStudentId(dto.getStudentId());
        result.setAssessmentId(dto.getAssessmentId());
        result.setScore(dto.getScore());
        AssessmentResult savedResult = resultRepository.save(result);
        
        // Calculate and update skill level based on score
        updateSkillLevel(dto.getStudentId(), assessment.getSkillCategory(), dto.getScore());
        
        return savedResult;
    }
    
    public List<AssessmentResult> getStudentResults(Long studentId) {
        log.info("Fetching assessment results for student: {}", studentId);
        return resultRepository.findByStudentId(studentId);
    }
    
    public List<SkillLevel> getStudentSkillLevels(Long studentId) {
        log.info("Fetching skill levels for student: {}", studentId);
        return skillLevelRepository.findByStudentId(studentId);
    }
    
    /**
     * Calculate skill level based on assessment score
     * 0-40: Beginner
     * 41-70: Intermediate
     * 71-100: Advanced
     */
    private void updateSkillLevel(Long studentId, String skillName, Integer score) {
        String level;
        if (score <= 40) {
            level = "Beginner";
        } else if (score <= 70) {
            level = "Intermediate";
        } else {
            level = "Advanced";
        }
        
        // Check if skill level already exists
        SkillLevel skillLevel = skillLevelRepository
                .findByStudentIdAndSkillName(studentId, skillName)
                .orElse(new SkillLevel());
        
        skillLevel.setStudentId(studentId);
        skillLevel.setSkillName(skillName);
        skillLevel.setLevel(level);
        
        skillLevelRepository.save(skillLevel);
        log.info("Updated skill level for student {} in {}: {}", studentId, skillName, level);
    }
}
