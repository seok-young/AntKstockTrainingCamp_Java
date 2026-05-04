package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Analysis;
import com.example.AntKstockCamp.domain.Entity.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Optional<Analysis> findFirstByOrderByDateDesc();

    @Query("SELECT a FROM Analysis a WHERE a.ticker = :ticker ORDER BY a.date DESC LIMIT 1")
    Optional<Analysis> findLastAnalysisByTicker(@Param("ticker") Ticker ticker);

}
