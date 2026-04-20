package com.internship.recommendation.client;

import com.internship.recommendation.dto.SkillLevelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * Feign Client for Skill Assessment Service
 * Fetches skill levels and assessment results
 */
@FeignClient(name = "skill-assessment-service")
public interface SkillAssessmentClient {
    
    @GetMapping("/student/{studentId}/levels")
    List<SkillLevelDTO> getStudentSkillLevels(@PathVariable("studentId") Long studentId);
}
