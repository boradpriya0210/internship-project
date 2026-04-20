package com.internship.recommendation.client;

import com.internship.recommendation.dto.InternshipDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * Feign Client for Internship Service
 * Fetches internship details and requirements
 */
@FeignClient(name = "internship-service")
public interface InternshipServiceClient {
    
    @GetMapping("")
    List<InternshipDTO> getAllInternships();
    
    @GetMapping("/{id}")
    InternshipDTO getInternship(@PathVariable("id") Long id);
}
