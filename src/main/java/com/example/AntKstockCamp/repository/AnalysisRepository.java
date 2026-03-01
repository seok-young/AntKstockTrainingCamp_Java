package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Indicator, Long> {
    Optional<Indicator> findFirstByOrderByDateDesc();
}
