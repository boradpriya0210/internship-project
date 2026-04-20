package com.internship.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java. util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDTO {
    private Long internshipId;
    private String internshipTitle;
    private String company;
    private String location;
    private Double stipend;
    private Integer matchScore;  // 0-100
    private String status;  // Highly Recommended, Recommended, Consider, Not Suitable
    private Map<String, Integer> breakdown;  // Score breakdown
    private List<String> matchingSkills;
    private List<String> missingSkills;
}
