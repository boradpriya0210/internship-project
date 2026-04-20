package com.internship.service.service;

import com.internship.service.model.Internship;
import com.internship.service.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipService {
    
    private final InternshipRepository internshipRepository;
    
    public List<Internship> getAllInternships() {
        log.info("Fetching all internships");
        return internshipRepository.findAll();
    }
    
    public Internship getInternshipById(Long id) {
        log.info("Fetching internship with ID: {}", id);
        return internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found with ID: " + id));
    }
    
    public Internship createInternship(Internship internship) {
        log.info("Creating new internship: {}", internship.getTitle());
        return internshipRepository.save(internship);
    }
}
