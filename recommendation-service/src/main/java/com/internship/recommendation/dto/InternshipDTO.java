package com.internship.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipDTO {
    private Long id;
    private String title;
    private String company;
    private String description;
    private Integer duration;
    private Double stipend;
    private String location;
    private String type;
    private List<RequiredSkillDTO> requiredSkills;
    private CriteriaDTO criteria;
}
