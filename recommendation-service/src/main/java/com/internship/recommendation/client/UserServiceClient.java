package com.internship.recommendation.client;

import com.internship.recommendation.dto.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * Feign Client for User Service
 * Communicates with user-service to fetch student details
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/{id}")
    StudentDTO getStudent(@PathVariable("id") Long id);
    
    @GetMapping("/{id}/skills")
    List<String> getStudentSkills(@PathVariable("id") Long id);
    
    @GetMapping("/{id}/interests")
    List<String> getStudentInterests(@PathVariable("id") Long id);
}
