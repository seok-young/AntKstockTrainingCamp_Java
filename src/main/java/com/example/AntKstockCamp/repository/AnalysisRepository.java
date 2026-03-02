package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Optional<Analysis> findFirstByOrderByDateDesc();
}
