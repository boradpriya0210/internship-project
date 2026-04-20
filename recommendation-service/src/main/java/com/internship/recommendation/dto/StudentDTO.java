package com.internship.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String name;
    private String email;
    private String branch;
    private Integer year;
    private Double cgpa;
    private List<String> skills;
    private List<String> interests;
}
