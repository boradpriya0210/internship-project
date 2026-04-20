package com.internship.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaDTO {
    private Long id;
    private Double minCgpa;
    private String eligibleBranches;
    private Integer minYear;
}
