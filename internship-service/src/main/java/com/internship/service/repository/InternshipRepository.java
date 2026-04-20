package com.internship.service.repository;

import com.internship.service.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByCompany(String company);
    List<Internship> findByType(String type);
    List<Internship> findByLocation(String location);
}
