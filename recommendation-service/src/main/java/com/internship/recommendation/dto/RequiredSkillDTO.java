package com.internship.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequiredSkillDTO {
    private Long id;
    private String skillName;
    private String minLevel;
    private Integer priority;
}
