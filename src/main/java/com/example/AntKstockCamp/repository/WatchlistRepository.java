package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    @Query("SELECT w.ticker.symbol FROM Watchlist w WHERE w.isWatching = true")
    List<String> findActiveTickerSymbols();
}
