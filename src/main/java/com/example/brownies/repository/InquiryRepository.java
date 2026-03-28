package com.example.brownies.repository;

import com.example.brownies.model.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByStatusOrderBySubmittedAtDesc(String status);
    List<Inquiry> findAllByOrderBySubmittedAtDesc();
    List<Inquiry> findByEmail(String email);
    long countByStatus(String status);
}
