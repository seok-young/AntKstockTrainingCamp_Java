package com.example.AntKstockCamp.service;

import com.example.AntKstockCamp.repository.DailyPriceRepository;
import com.example.AntKstockCamp.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Collector {

    private final WatchlistRepository watchlistRepository;
    private final DailyPriceRepository dailyPriceRepository;

    public List<String> getInterestStocksID() {
        try {
            return watchlistRepository.findActiveTickerSymbols();
        } catch (RuntimeException e) {
            log.error("Error during gettingInterestStocksID");
            return Collections.emptyList();
        }
    }

    public LocalDate getLateDate(String symbol) {
        try {
            return dailyPriceRepository.findLateDateBySymbol(symbol)
                    .orElse(LocalDate.of(2000, 1, 1));
        } catch (RuntimeException e) {
            log.error("Error during gettingLateDate");
            return LocalDate.of(2000, 1, 1);
        }
    }

}
