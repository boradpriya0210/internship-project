package com.internship.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillLevelDTO {
    private Long id;
    private Long studentId;
    private String skillName;
    private String level;
}
