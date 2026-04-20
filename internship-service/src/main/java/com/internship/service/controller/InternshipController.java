package com.internship.service.controller;

import com.internship.service.model.Internship;
import com.internship.service.service.InternshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InternshipController {
    
    private final InternshipService internshipService;
    
    @GetMapping("")
    public ResponseEntity<List<Internship>> getAllInternships() {
        log.info("Fetching all internships");
        return ResponseEntity.ok(internshipService.getAllInternships());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Internship> getInternship(@PathVariable Long id) {
        log.info("Fetching internship: {}", id);
        return ResponseEntity.ok(internshipService.getInternshipById(id));
    }
    
    @PostMapping("")
    public ResponseEntity<Internship> createInternship(@RequestBody Internship internship) {
        log.info("Creating new internship");
        Internship created = internshipService.createInternship(internship);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Internship Service is running!");
    }

    /**
     * Handler for favicon.ico to prevent 400 errors from path variable mismatch
     */
    @GetMapping("favicon.ico")
    @ResponseBody
    public void disableFavicon() {
        // Do nothing
    }
}
