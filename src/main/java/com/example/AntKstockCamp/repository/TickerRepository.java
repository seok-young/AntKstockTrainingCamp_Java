package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TickerRepository extends JpaRepository<Ticker, Long> {
}
