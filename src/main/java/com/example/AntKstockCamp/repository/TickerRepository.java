package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TickerRepository extends JpaRepository<Ticker, Long> {
    Optional<Ticker> findBySymbol(String symbol);
}
