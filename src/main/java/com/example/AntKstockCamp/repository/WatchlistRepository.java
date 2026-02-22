package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.Ticker;
import com.example.AntKstockCamp.domain.Entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    // 관심 종목만 가져오기
    @Query("SELECT w.ticker FROM Watchlist w WHERE w.isWatching = true")
    List<Ticker> findActiveTickerSymbols();

}
