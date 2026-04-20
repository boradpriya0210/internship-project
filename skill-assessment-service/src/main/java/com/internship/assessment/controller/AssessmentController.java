package com.internship.assessment.controller;

import com.internship.assessment.dto.SubmitAssessmentDTO;
import com.internship.assessment.model.Assessment;
import com.internship.assessment.model.AssessmentResult;
import com.internship.assessment.model.SkillLevel;
import com.internship.assessment.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AssessmentController {
    
    private final AssessmentService assessmentService;
    
    @GetMapping("")
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        log.info("Fetching all assessments");
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessment(@PathVariable Long id) {
        log.info("Fetching assessment: {}", id);
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }
    
    @PostMapping("/submit")
    public ResponseEntity<AssessmentResult> submitAssessment(
            @Valid @RequestBody SubmitAssessmentDTO dto) {
        log.info("Submitting assessment");
        AssessmentResult result = assessmentService.submitAssessment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AssessmentResult>> getStudentAssessments(
            @PathVariable Long studentId) {
        log.info("Fetching assessments for student: {}", studentId);
        return ResponseEntity.ok(assessmentService.getStudentResults(studentId));
    }
    
    @GetMapping("/student/{studentId}/levels")
    public ResponseEntity<List<SkillLevel>> getStudentSkillLevels(
            @PathVariable Long studentId) {
        log.info("Fetching skill levels for student: {}", studentId);
        return ResponseEntity.ok(assessmentService.getStudentSkillLevels(studentId));
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Skill Assessment Service is running!");
    }

    /**
     * Handler for favicon.ico to prevent errors from path variable mismatch
     */
    @GetMapping("favicon.ico")
    @ResponseBody
    public void disableFavicon() {
        // Do nothing
    }
}
