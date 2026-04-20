package com.internship.user.controller;

import com.internship.user.dto.StudentRegistrationDTO;
import com.internship.user.dto.StudentResponseDTO;
import com.internship.user.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User Service
 * Handles all HTTP requests related to student management
 * 
 * Base URL (via API Gateway): http://localhost:8080/api/users
 * Direct URL: http://localhost:8081
 */
@RestController
@RequestMapping("")  // Empty because API Gateway adds /api/users prefix
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StudentController {
    
    private final StudentService studentService;
    
    /**
     * Register a new student
     * POST /api/users/register
     * @param registrationDTO Student registration data
     * @return Created student
     */
    @PostMapping("/register")
    public ResponseEntity<StudentResponseDTO> registerStudent(
            @Valid @RequestBody StudentRegistrationDTO registrationDTO) {
        log.info("Received registration request for email: {}", registrationDTO.getEmail());
        StudentResponseDTO response = studentService.registerStudent(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get student by ID
     * GET /api/users/{id}
     * @param id Student ID
     * @return Student details
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudent(@PathVariable Long id) {
        log.info("Received request to fetch student with ID: {}", id);
        StudentResponseDTO response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all students
     * GET /api/users
     * @return List of all students
     */
    @GetMapping("")
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        log.info("Received request to fetch all students");
        List<StudentResponseDTO> response = studentService.getAllStudents();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get student skills
     * GET /api/users/{id}/skills
     * @param id Student ID
     * @return List of skills
     */
    @GetMapping("/{id}/skills")
    public ResponseEntity<List<String>> getStudentSkills(@PathVariable Long id) {
        log.info("Received request to fetch skills for student ID: {}", id);
        List<String> skills = studentService.getStudentSkills(id);
        return ResponseEntity.ok(skills);
    }
    
    /**
     * Get student interests
     * GET /api/users/{id}/interests
     * @param id Student ID
     * @return List of interests
     */
    @GetMapping("/{id}/interests")
    public ResponseEntity<List<String>> getStudentInterests(@PathVariable Long id) {
        log.info("Received request to fetch interests for student ID: {}", id);
        List<String> interests = studentService.getStudentInterests(id);
        return ResponseEntity.ok(interests);
    }
    
    /**
     * Update student profile
     * PUT /api/users/{id}
     * @param id Student ID
     * @param registrationDTO Updated student data
     * @return Updated student
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRegistrationDTO registrationDTO) {
        log.info("Received request to update student with ID: {}", id);
        StudentResponseDTO response = studentService.updateStudent(id, registrationDTO);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint
     * GET /health
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running!");
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
