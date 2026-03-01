package com.example.AntKstockCamp.service;

import com.example.AntKstockCamp.domain.Entity.Analysis;
import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import com.example.AntKstockCamp.dto.DailyPriceDto;
import com.example.AntKstockCamp.repository.AnalysisRepository;
import com.example.AntKstockCamp.repository.DailyPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final DailyPriceRepository dailyPriceRepository;

    public LocalDate getLatesDateOfAnalysis(){
        return analysisRepository.findFirstByOrderByDateDesc()
                .map(Analysis::getDate)
                .orElse(null);
    }

    public List<DailyPriceDto> getPriceDataForAnalysis(String symbol,
                                                       LocalDate targetStartDate){
        LocalDate fetchStart = targetStartDate.minusDays(170);
        List<DailyPrice> entities = dailyPriceRepository.findByTickerSymbolAndDateBetweenOrderByDateAsc(symbol,fetchStart,targetStartDate);

        return entities.stream()
                .map(entity -> DailyPriceDto.builder()
                        .date(entity.getDate().toString())
                        .open_pric(String.valueOf(entity.getOpenPrice()))
                        .high_pric(String.valueOf(entity.getHighPrice()))
                        .low_pric(String.valueOf(entity.getLowPrice()))
                        .close_pric(String.valueOf(Math.abs(entity.getClosePrice())))
                        .trde_qty(String.valueOf(entity.getTrdeQty()))
                        .build()
                ).toList();
    }



}
