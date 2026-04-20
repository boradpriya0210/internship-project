package com.internship.recommendation.controller;

import com.internship.recommendation.dto.RecommendationResponseDTO;
import com.internship.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    private final RecommendationService recommendationService;

    @GetMapping("")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Recommendation Service. Access /health for status.");
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RecommendationResponseDTO>> getRecommendations(
            @PathVariable Long studentId) {
        log.info("Getting recommendations for student: {}", studentId);
        List<RecommendationResponseDTO> recommendations = 
                recommendationService.getRecommendations(studentId);
        return ResponseEntity.ok(recommendations);
    }
    
    @GetMapping("/student/{studentId}/internship/{internshipId}")
    public ResponseEntity<RecommendationResponseDTO> getSingleRecommendation(
            @PathVariable Long studentId,
            @PathVariable Long internshipId) {
        log.info("Getting recommendation for student {} and internship {}", studentId, internshipId);
        RecommendationResponseDTO recommendation = 
                recommendationService.getSingleRecommendation(studentId, internshipId);
        return ResponseEntity.ok(recommendation);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Recommendation Service is running!");
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
