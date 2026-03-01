package com.example.AntKstockCamp.service;

import com.example.AntKstockCamp.domain.Entity.Indicator;
import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import com.example.AntKstockCamp.dto.DailyPriceDto;
import com.example.AntKstockCamp.dto.IndicatorDto;
import com.example.AntKstockCamp.repository.AnalysisRepository;
import com.example.AntKstockCamp.repository.DailyPriceRepository;
import com.example.AntKstockCamp.util.IndicatorCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final IndicatorCalculator indicatorCalculator;

    public LocalDate getLatesDateOfAnalysis(){
        return analysisRepository.findFirstByOrderByDateDesc()
                .map(Indicator::getDate)
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

    public IndicatorDto getIndicators(String symbol, LocalDate targetDate){
        List<DailyPriceDto> dpDtoList = getPriceDataForAnalysis(symbol, targetDate);

        List<Double> prices = dpDtoList.stream()
                .map(dto -> Double.parseDouble(dto.close_pric()))
                .toList();

        double[] ma5 = indicatorCalculator.calculateMA(prices,5);
        double[] ma20 = indicatorCalculator.calculateMA(prices,20);
        double[] ma60 = indicatorCalculator.calculateMA(prices,60);
        double[] ma120 = indicatorCalculator.calculateMA(prices,120);
        double[] rsi = indicatorCalculator.calculateRSI(prices, 14);
        Map<String, double[]> macd = indicatorCalculator.calculateMACD(prices);
        Map<String, double[]> bb =indicatorCalculator.calculateBB(prices,20,2);

        int lastIdx = dpDtoList.size()-1;
        IndicatorDto indicators = IndicatorDto.builder()
                .ma5(ma5[lastIdx])
                .ma20(ma20[lastIdx])
                .ma60(ma60[lastIdx])
                .ma120(ma120[lastIdx])
                .rsi(rsi[lastIdx])
                .macd(macd.get("macd")[lastIdx])
                .macdSignal(macd.get("signal")[lastIdx])
                .macdHist(macd.get("hist")[lastIdx])
                .bbUpper(bb.get("upper")[lastIdx])
                .bbMiddle(bb.get("middle")[lastIdx])
                .bbLower(bb.get("lower")[lastIdx])
                .build();
        return indicators;
    }



}
