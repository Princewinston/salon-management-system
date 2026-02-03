package com.salon.salon_management.repository;

import com.salon.salon_management.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    java.util.List<Feedback> findByProductProductId(Long productId);
}
