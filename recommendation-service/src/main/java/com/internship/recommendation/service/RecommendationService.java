package com.internship.recommendation.service;

import com.internship.recommendation.client.InternshipServiceClient;
import com.internship.recommendation.client.SkillAssessmentClient;
import com.internship.recommendation.client.UserServiceClient;
import com.internship.recommendation.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommendation Engine Service
 * Core business logic for internship recommendations
 * Uses Feign clients to fetch data from other microservices
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    
    private final UserServiceClient userServiceClient;
    private final SkillAssessmentClient skillAssessmentClient;
    private final InternshipServiceClient internshipServiceClient;
    
    /**
     * Get all recommendations for a student
     * @param studentId Student ID
     * @return List of recommendations sorted by match score
     */
    public List<RecommendationResponseDTO> getRecommendations(Long studentId) {
        log.info("Generating recommendations for student: {}", studentId);
        
        // Fetch student details via Feign
        StudentDTO student = userServiceClient.getStudent(studentId);
        List<String> studentSkills = userServiceClient.getStudentSkills(studentId);
        List<String> studentInterests = userServiceClient.getStudentInterests(studentId);
        
        // Fetch skill levels via Feign
        List<SkillLevelDTO> skillLevels = skillAssessmentClient.getStudentSkillLevels(studentId);
        Map<String, String> skillLevelMap = skillLevels.stream()
                .collect(Collectors.toMap(SkillLevelDTO::getSkillName, SkillLevelDTO::getLevel));
        
        // Fetch all internships via Feign
        List<InternshipDTO> internships = internshipServiceClient.getAllInternships();
        
        // Calculate match score for each internship
        List<RecommendationResponseDTO> recommendations = internships.stream()
                .map(internship -> calculateMatchScore(student, studentSkills, studentInterests, 
                                                       skillLevelMap, internship))
                .sorted((r1, r2) -> Integer.compare(r2.getMatchScore(), r1.getMatchScore()))
                .collect(Collectors.toList());
        
        log.info("Generated {} recommendations for student: {}", recommendations.size(), studentId);
        return recommendations;
    }
    
    /**
     * Get single internship recommendation
     * @param studentId Student ID
     * @param internshipId Internship ID
     * @return Recommendation details
     */
    public RecommendationResponseDTO getSingleRecommendation(Long studentId, Long internshipId) {
        log.info("Getting recommendation for student {} and internship {}", studentId, internshipId);
        
        StudentDTO student = userServiceClient.getStudent(studentId);
        List<String> studentSkills = userServiceClient.getStudentSkills(studentId);
        List<String> studentInterests = userServiceClient.getStudentInterests(studentId);
        
        List<SkillLevelDTO> skillLevels = skillAssessmentClient.getStudentSkillLevels(studentId);
        Map<String, String> skillLevelMap = skillLevels.stream()
                .collect(Collectors.toMap(SkillLevelDTO::getSkillName, SkillLevelDTO::getLevel));
        
        InternshipDTO internship = internshipServiceClient.getInternship(internshipId);
        
        return calculateMatchScore(student, studentSkills, studentInterests, skillLevelMap, internship);
    }
    
    /**
     * Calculate match score using weighted algorithm
     * 
     * Match Score Algorithm:
     * 1. Skill Match (40%): Compare student skills with required skills
     * 2. Assessment Score (30%): Check skill levels meet minimum requirements
     * 3. Interest Match (20%): Match student interests with internship type
     * 4. Eligibility Criteria (10%): CGPA, branch, year matching
     * 
     * Total Score: 0-100
     */
    private RecommendationResponseDTO calculateMatchScore(
            StudentDTO student,
            List<String> studentSkills,
            List<String> studentInterests,
            Map<String, String> skillLevelMap,
            InternshipDTO internship) {
        
        RecommendationResponseDTO response = new RecommendationResponseDTO();
        response.setInternshipId(internship.getId());
        response.setInternshipTitle(internship.getTitle());
        response.setCompany(internship.getCompany());
        response.setLocation(internship.getLocation());
        response.setStipend(internship.getStipend());
        
        Map<String, Integer> breakdown = new HashMap<>();
        int totalScore = 0;
        
        // 1. Skill Match (40%)
        int skillMatchScore = calculateSkillMatch(studentSkills, internship.getRequiredSkills());
        breakdown.put("skillMatch", skillMatchScore);
        totalScore += skillMatchScore;
        
        // 2. Assessment Score (30%)
        int assessmentScore = calculateAssessmentScore(skillLevelMap, internship.getRequiredSkills());
        breakdown.put("assessmentScore", assessmentScore);
        totalScore += assessmentScore;
        
        // 3. Interest Match (20%)
        int interestScore = calculateInterestMatch(studentInterests, internship.getTitle(), 
                                                   internship.getDescription());
        breakdown.put("interestMatch", interestScore);
        totalScore += interestScore;
        
        // 4. Eligibility Criteria (10%)
        int eligibilityScore = calculateEligibility(student, internship.getCriteria());
        breakdown.put("eligibility", eligibilityScore);
        totalScore += eligibilityScore;
        
        response.setMatchScore(totalScore);
        response.setBreakdown(breakdown);
        
        // Determine status based on total score
        if (totalScore >= 80) {
            response.setStatus("Highly Recommended");
        } else if (totalScore >= 60) {
            response.setStatus("Recommended");
        } else if (totalScore >= 40) {
            response.setStatus("Consider");
        } else {
            response.setStatus("Not Suitable");
        }
        
        // Identify matching and missing skills
        List<String> requiredSkillNames = internship.getRequiredSkills() != null 
                ? internship.getRequiredSkills().stream()
                    .map(RequiredSkillDTO::getSkillName)
                    .collect(Collectors.toList())
                : new ArrayList<>();
        
        List<String> matchingSkills = studentSkills.stream()
                .filter(skill -> requiredSkillNames.stream()
                        .anyMatch(rs -> rs.equalsIgnoreCase(skill)))
                .collect(Collectors.toList());
        
        List<String> missingSkills = requiredSkillNames.stream()
                .filter(rs -> studentSkills.stream()
                        .noneMatch(skill -> skill.equalsIgnoreCase(rs)))
                .collect(Collectors.toList());
        
        response.setMatchingSkills(matchingSkills);
        response.setMissingSkills(missingSkills);
        
        return response;
    }
    
    /**
     * Calculate skill match score (40% weight)
     */
    private int calculateSkillMatch(List<String> studentSkills, List<RequiredSkillDTO> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 40;  // Full score if no requirements
        }
        
        int matchCount = 0;
        int totalRequired = requiredSkills.size();
        
        for (RequiredSkillDTO required : requiredSkills) {
            boolean hasSkill = studentSkills.stream()
                    .anyMatch(skill -> skill.equalsIgnoreCase(required.getSkillName()));
            if (hasSkill) {
                matchCount++;
            }
        }
        
        double matchPercentage = (double) matchCount / totalRequired;
        return (int) (matchPercentage * 40);
    }
    
    /**
     * Calculate assessment score (30% weight)
     */
    private int calculateAssessmentScore(Map<String, String> skillLevelMap, 
                                         List<RequiredSkillDTO> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 30;
        }
        
        int qualifiedCount = 0;
        int totalRequired = requiredSkills.size();
        
        for (RequiredSkillDTO required : requiredSkills) {
            String studentLevel = skillLevelMap.get(required.getSkillName());
            if (studentLevel != null && meetsLevelRequirement(studentLevel, required.getMinLevel())) {
                qualifiedCount++;
            }
        }
        
        double qualifiedPercentage = (double) qualifiedCount / totalRequired;
        return (int) (qualifiedPercentage * 30);
    }
    
    /**
     * Check if student's skill level meets minimum requirement
     */
    private boolean meetsLevelRequirement(String studentLevel, String minLevel) {
        Map<String, Integer> levelRank = Map.of(
                "Beginner", 1,
                "Intermediate", 2,
                "Advanced", 3
        );
        
        int studentRank = levelRank.getOrDefault(studentLevel, 0);
        int minRank = levelRank.getOrDefault(minLevel, 1);
        
        return studentRank >= minRank;
    }
    
    /**
     * Calculate interest match score (20% weight)
     */
    private int calculateInterestMatch(List<String> studentInterests, String title, String description) {
        if (studentInterests == null || studentInterests.isEmpty()) {
            return 10;  // Partial score if no interests specified
        }
        
        String combinedText = (title + " " + description).toLowerCase();
        
        long matchCount = studentInterests.stream()
                .filter(interest -> combinedText.contains(interest.toLowerCase()))
                .count();
        
        double matchPercentage = (double) matchCount / studentInterests.size();
        return (int) (matchPercentage * 20);
    }
    
    /**
     * Calculate eligibility score (10% weight)
     */
    private int calculateEligibility(StudentDTO student, CriteriaDTO criteria) {
        if (criteria == null) {
            return 10;  // Full score if no criteria
        }
        
        int score = 0;
        
        // CGPA check (5 points)
        if (criteria.getMinCgpa() == null || student.getCgpa() >= criteria.getMinCgpa()) {
            score += 5;
        }
        
        // Branch check (3 points)
        if (criteria.getEligibleBranches() == null || 
            criteria.getEligibleBranches().contains(student.getBranch())) {
            score += 3;
        }
        
        // Year check (2 points)
        if (criteria.getMinYear() == null || student.getYear() >= criteria.getMinYear()) {
            score += 2;
        }
        
        return score;
    }
}
