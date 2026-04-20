package com.internship.user.repository;

import com.internship.user.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for Student entity
 * Provides database operations for Student table
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    /**
     * Find student by email address
     * @param email Email address
     * @return Optional containing Student if found
     */
    Optional<Student> findByEmail(String email);
    
    /**
     * Check if student exists by email
     * @param email Email address
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}
