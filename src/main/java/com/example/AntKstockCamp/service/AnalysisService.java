package com.example.AntKstockCamp.service;

import com.example.AntKstockCamp.domain.Entity.Analysis;
import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import com.example.AntKstockCamp.domain.Entity.Ticker;
import com.example.AntKstockCamp.dto.DailyPriceDto;
import com.example.AntKstockCamp.dto.AnalysisDto;
import com.example.AntKstockCamp.repository.AnalysisRepository;
import com.example.AntKstockCamp.repository.DailyPriceRepository;
import com.example.AntKstockCamp.repository.WatchlistRepository;
import com.example.AntKstockCamp.util.IndicatorCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final WatchlistRepository watchlistRepository;
    private final IndicatorCalculator indicatorCalculator;

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


    // 하루 지표 구하기
    public Optional<AnalysisDto> getIndicators(String symbol, LocalDate targetDate){
        List<DailyPriceDto> dpDtoList = getPriceDataForAnalysis(symbol, targetDate);

        if((dpDtoList.isEmpty()) | (dpDtoList.size() < 120)){
            return Optional.empty();
        }
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

        double lastprice = 0.0;
        lastprice = prices.get(prices.size()-1);

        int lastIdx = dpDtoList.size()-1;
        AnalysisDto indicators = AnalysisDto.builder()
                .ticker(symbol)
                .date(targetDate)
                .close_price((float) lastprice)
                .ma5((float) ma5[lastIdx])
                .ma20((float) ma20[lastIdx])
                .ma60((float) ma60[lastIdx])
                .ma120((float) ma120[lastIdx])
                .rsi((float) rsi[lastIdx])
                .macd((float) macd.get("macd")[lastIdx])
                .macdSignal((float) macd.get("signal")[lastIdx])
                .macdHist((float) macd.get("hist")[lastIdx])
                .bbUpper((float) bb.get("upper")[lastIdx])
                .bbMiddle((float) bb.get("middle")[lastIdx])
                .bbLower((float) bb.get("lower")[lastIdx])
                .build();
        return Optional.of(indicators);
    }

    public void saveAnalysis(String symbol, LocalDate targetDate){
        Ticker ticker = watchlistRepository.findByTicker_Symbol(symbol)
                .orElseThrow(()->new RuntimeException("There is no Ticker with that symbol"));
        AnalysisDto analysisDto = getIndicators(symbol, targetDate)
                .orElseThrow(()-> new RuntimeException("There is not enough data for Analysis"));
        Analysis analysis = analysisDto.toEntity(ticker);

        analysisRepository.save(analysis);
    }

}
