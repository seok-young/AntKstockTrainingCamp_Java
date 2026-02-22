package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyPriceRepository extends JpaRepository<DailyPrice,Long> {
    @Query("SELECT MAX(d.date) FROM DailyPrice d WHERE d.ticker.symbol = :symbol")
    Optional<LocalDate> findLateDateBySymbol(@Param("symbol") String symbol);
}
