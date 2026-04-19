package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    // 관심 종목만 가져오기
    @Query("SELECT w FROM Watchlist w JOIN w.ticker t WHERE w.isWatching = true")
    List<Watchlist> findActiveTickerSymbols();

    Optional<Watchlist> findByTicker_Symbol(String symbol);

}
