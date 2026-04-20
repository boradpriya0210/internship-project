package com.internship.user.service;

import com.internship.user.dto.StudentRegistrationDTO;
import com.internship.user.dto.StudentResponseDTO;
import com.internship.user.model.Interest;
import com.internship.user.model.Skill;
import com.internship.user.model.Student;
import com.internship.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Student operations
 * Contains business logic for student management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    
    private final StudentRepository studentRepository;
    
    /**
     * Register a new student
     * @param registrationDTO Student registration data
     * @return Created student response
     */
    @Transactional
    public StudentResponseDTO registerStudent(StudentRegistrationDTO registrationDTO) {
        log.info("Registering new student with email: {}", registrationDTO.getEmail());
        
        // Check if student already exists
        if (studentRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Student with email " + registrationDTO.getEmail() + " already exists");
        }
        
        // Create student entity
        Student student = new Student();
        student.setName(registrationDTO.getName());
        student.setEmail(registrationDTO.getEmail());
        student.setBranch(registrationDTO.getBranch());
        student.setYear(registrationDTO.getYear());
        student.setCgpa(registrationDTO.getCgpa());
        student.setPhone(registrationDTO.getPhone());
        
        // Add skills
        if (registrationDTO.getSkills() != null) {
            List<Skill> skills = registrationDTO.getSkills().stream()
                    .map(skillName -> {
                        Skill skill = new Skill();
                        skill.setSkillName(skillName);
                        skill.setProficiencyLevel("Beginner"); // Default proficiency
                        skill.setStudent(student);
                        return skill;
                    })
                    .collect(Collectors.toList());
            student.setSkills(skills);
        }
        
        // Add interests
        if (registrationDTO.getInterests() != null) {
            List<Interest> interests = registrationDTO.getInterests().stream()
                    .map(interestArea -> {
                        Interest interest = new Interest();
                        interest.setInterestArea(interestArea);
                        interest.setStudent(student);
                        return interest;
                    })
                    .collect(Collectors.toList());
            student.setInterests(interests);
        }
        
        // Save student
        Student savedStudent = studentRepository.save(student);
        log.info("Student registered successfully with ID: {}", savedStudent.getId());
        
        return convertToResponseDTO(savedStudent);
    }
    
    /**
     * Get student by ID
     * @param studentId Student ID
     * @return Student response
     */
    public StudentResponseDTO getStudentById(Long studentId) {
        log.info("Fetching student with ID: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        return convertToResponseDTO(student);
    }
    
    /**
     * Get all students
     * @return List of all students
     */
    public List<StudentResponseDTO> getAllStudents() {
        log.info("Fetching all students");
        return studentRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get student skills by student ID
     * @param studentId Student ID
     * @return List of skill names
     */
    public List<String> getStudentSkills(Long studentId) {
        log.info("Fetching skills for student ID: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        return student.getSkills().stream()
                .map(Skill::getSkillName)
                .collect(Collectors.toList());
    }
    
    /**
     * Get student interests by student ID
     * @param studentId Student ID
     * @return List of interest areas
     */
    public List<String> getStudentInterests(Long studentId) {
        log.info("Fetching interests for student ID: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        return student.getInterests().stream()
                .map(Interest::getInterestArea)
                .collect(Collectors.toList());
    }
    
    /**
     * Update student profile
     * @param studentId Student ID
     * @param registrationDTO Updated student data
     * @return Updated student response
     */
    @Transactional
    public StudentResponseDTO updateStudent(Long studentId, StudentRegistrationDTO registrationDTO) {
        log.info("Updating student with ID: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        
        // Update basic details
        student.setName(registrationDTO.getName());
        student.setBranch(registrationDTO.getBranch());
        student.setYear(registrationDTO.getYear());
        student.setCgpa(registrationDTO.getCgpa());
        student.setPhone(registrationDTO.getPhone());
        
        Student updatedStudent = studentRepository.save(student);
        log.info("Student updated successfully with ID: {}", updatedStudent.getId());
        
        return convertToResponseDTO(updatedStudent);
    }
    
    /**
     * Convert Student entity to StudentResponseDTO
     * @param student Student entity
     * @return StudentResponseDTO
     */
    private StudentResponseDTO convertToResponseDTO(Student student) {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setBranch(student.getBranch());
        dto.setYear(student.getYear());
        dto.setCgpa(student.getCgpa());
        dto.setPhone(student.getPhone());
        dto.setCreatedAt(student.getCreatedAt());
        
        // Extract skill names
        dto.setSkills(student.getSkills().stream()
                .map(Skill::getSkillName)
                .collect(Collectors.toList()));
        
        // Extract interest areas
        dto.setInterests(student.getInterests().stream()
                .map(Interest::getInterestArea)
                .collect(Collectors.toList()));
        
        return dto;
    }
}
