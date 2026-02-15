package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyPriceRepository extends JpaRepository<DailyPrice,Long> {
    Optional<DailyPrice> findFirstByTickerSymbolOrderByDatesDesc(String tickersymbol);
}
