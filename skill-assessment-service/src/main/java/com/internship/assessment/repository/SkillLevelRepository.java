package com.internship.assessment.repository;

import com.internship.assessment.model.SkillLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillLevelRepository extends JpaRepository<SkillLevel, Long> {
    List<SkillLevel> findByStudentId(Long studentId);
    Optional<SkillLevel> findByStudentIdAndSkillName(Long studentId, String skillName);
}
