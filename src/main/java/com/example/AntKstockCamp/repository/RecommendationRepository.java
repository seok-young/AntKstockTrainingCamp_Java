package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
}
