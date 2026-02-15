package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
