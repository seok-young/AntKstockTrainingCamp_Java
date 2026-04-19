package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyPriceRepository extends JpaRepository<DailyPrice,Long> {

    @Query("SELECT MIN(d.date) FROM DailyPrice d WHERE d.ticker.symbol = :symbol")
    Optional<LocalDate> findFirstDateBySymbol(@Param("symbol") String symbol);

    @Query("SELECT MAX(d.date) FROM DailyPrice d WHERE d.ticker.symbol = :symbol")
    Optional<LocalDate> findLateDateBySymbol(@Param("symbol") String symbol);

    List<DailyPrice> findByTickerSymbolAndDateBetweenOrderByDateAsc(
            String symbol,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query(value = "SELECT * FROM daily_price WHERE ticker_symbol = :symbol " +
            "AND date <= :targetDate ORDER BY date DESC LIMIT :limit", nativeQuery = true)
    List<DailyPrice> findRecentPrices(@Param("symbol") String symbol,
                                      @Param("targetDate") LocalDate targetDate,
                                      @Param("limit") int limit);
}
