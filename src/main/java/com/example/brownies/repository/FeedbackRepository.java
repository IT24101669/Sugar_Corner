package com.example.brownies.repository;

import com.example.brownies.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStatusOrderBySubmittedAtDesc(String status);
    List<Feedback> findAllByOrderBySubmittedAtDesc();
    long countByStatus(String status);
}
