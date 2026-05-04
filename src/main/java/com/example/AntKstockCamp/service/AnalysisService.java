package com.example.AntKstockCamp.service;

import com.example.AntKstockCamp.domain.Entity.Analysis;
import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import com.example.AntKstockCamp.domain.Entity.Watchlist;
import com.example.AntKstockCamp.dto.AnalysisDto;
import com.example.AntKstockCamp.dto.DailyPriceDto;
import com.example.AntKstockCamp.repository.AnalysisRepository;
import com.example.AntKstockCamp.repository.DailyPriceRepository;
import com.example.AntKstockCamp.repository.WatchlistRepository;
import com.example.AntKstockCamp.util.IndicatorCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Comparator;

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

    public List<DailyPriceDto> getRecentPriceData(String symbol, LocalDate targetDate, int limit) {
        List<DailyPrice> entities = dailyPriceRepository.findRecentPrices(symbol, targetDate, limit);

        // 날짜 오름차순(과거 -> 현재)으로 다시 정렬
        return entities.stream()
                .sorted(Comparator.comparing(DailyPrice::getDate))
                .map(entity -> DailyPriceDto.builder()
                        .date(entity.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
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

    // 최초 분석데이터 저장
    @Transactional
    public void saveAllAnalysis(String symbol) {
        Watchlist watchlist = watchlistRepository.findByTicker_Symbol(symbol)
                .orElseThrow(() -> new RuntimeException("Ticker not found: " + symbol));

        LocalDate firstDate = dailyPriceRepository.findFirstDateBySymbol(symbol)
                .orElse(LocalDate.now().minusMonths(16));
        LocalDate lastDate = dailyPriceRepository.findLateDateBySymbol(symbol)
                .orElse(LocalDate.now());

        LocalDate currentTargetDate = firstDate;
        List<Analysis> analysisList = new ArrayList<>();
        while (!currentTargetDate.isAfter(lastDate)){
            List<DailyPriceDto> dpDtoList = getRecentPriceData(symbol, currentTargetDate, 170);
            System.out.println(currentTargetDate + " 조회된 데이터 개수: " + dpDtoList.size());

            if (dpDtoList.size()>=120){
                List<Double> prices = dpDtoList.stream()
                        .map(dto -> Double.parseDouble(dto.close_pric()))
                        .toList();

                double[] ma5 = indicatorCalculator.calculateMA(prices, 5);
                double[] ma20 = indicatorCalculator.calculateMA(prices, 20);
                double[] ma60 = indicatorCalculator.calculateMA(prices, 60);
                double[] ma120 = indicatorCalculator.calculateMA(prices, 120);
                double[] rsi = indicatorCalculator.calculateRSI(prices, 14);
                Map<String, double[]> macd = indicatorCalculator.calculateMACD(prices);
                Map<String, double[]> bb = indicatorCalculator.calculateBB(prices, 20, 2);

                int lastIdx = dpDtoList.size() - 1;

                Analysis analysis = Analysis.builder()
                        .ticker(watchlist.getTicker())
                        .date(currentTargetDate)
                        .closePrice(Float.parseFloat(dpDtoList.get(lastIdx).close_pric()))
                        // 지표값이 아직 계산되지 않은 앞부분은 0.0f로 들어감
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

                analysisList.add(analysis);
                // 데이터가 너무 많으면 중간 저장 (메모리 관리)
                if (analysisList.size() >= 500) {
                    System.out.println("--- 500개 중간 저장 시도 ---");
                    analysisRepository.saveAll(analysisList);
                    analysisList.clear();
                }

            }
            currentTargetDate = currentTargetDate.plusDays(1);
        }

        if (!analysisList.isEmpty()) {
            System.out.println("--- 최종 " + analysisList.size() + "개 저장 시도 ---");
            analysisRepository.saveAll(analysisList);
        }
    }

    // 분석 오케스트라 함수
    @Transactional
    public void runAnanlysis(String symbol){
        Optional<Watchlist> watchlistOpt = watchlistRepository.findByTicker_Symbol(symbol);

        if(watchlistOpt.isEmpty()) {
            System.out.println( symbol+ " Not in Watchlist");
            return;
        }

        Watchlist watchlist = watchlistOpt.get();
        LocalDate tdy = LocalDate.now();
        Optional<Analysis> analysisOpt = analysisRepository.findLastAnalysisByTicker(watchlist.getTicker());

        if (analysisOpt.isEmpty()) {
            System.out.println("No Analasis Data for " + symbol);
            return;
        }

        LocalDate lastDate = analysisOpt.get().getDate();

        List<Analysis> analysisList = new ArrayList<>();
        LocalDate currentTargetDate = lastDate.plusDays(1);
        while (!currentTargetDate.isAfter(tdy)) {
            List<DailyPriceDto> dpDtoList = getRecentPriceData(symbol, currentTargetDate, 170);
            System.out.println(currentTargetDate + "조회된 데이터 개수" + dpDtoList.size());

            if (dpDtoList.size() > 120) {
                List<Double> prices = dpDtoList.stream()
                        .map(dto -> Double.parseDouble(dto.close_pric()))
                        .toList();

                double[] ma5 = indicatorCalculator.calculateMA(prices, 5);
                double[] ma20 = indicatorCalculator.calculateMA(prices, 20);
                double[] ma60 = indicatorCalculator.calculateMA(prices, 60);
                double[] ma120 = indicatorCalculator.calculateMA(prices, 120);
                double[] rsi = indicatorCalculator.calculateRSI(prices, 14);
                Map<String, double[]> macd = indicatorCalculator.calculateMACD(prices);
                Map<String, double[]> bb = indicatorCalculator.calculateBB(prices, 20, 2);

                int lastIdx = dpDtoList.size() - 1;

                Analysis analysis = Analysis.builder()
                        .ticker(watchlist.getTicker())
                        .date(currentTargetDate)
                        .closePrice(Float.parseFloat(dpDtoList.get(lastIdx).close_pric()))
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

                analysisList.add(analysis);
                if (analysisList.size() >= 500) {
                    System.out.println("--- 500개 중간 저장 시도 ---");
                    analysisRepository.saveAll(analysisList);
                    analysisList.clear();
                }
            }
            currentTargetDate = currentTargetDate.plusDays(1);
        }
        if(!analysisList.isEmpty()){
            System.out.println("--- 최종 " + analysisList.size() + "개 저장 시도 ---");
            analysisRepository.saveAll(analysisList);
        }
    }

    // 하루치 분석 및 저장
    public void saveAnalysis(String symbol, LocalDate targetDate){
        Watchlist watchlist = watchlistRepository.findByTicker_Symbol(symbol)
                .orElseThrow(()->new RuntimeException("There is no Ticker with that symbol"));
        AnalysisDto analysisDto = getIndicators(symbol, targetDate)
                .orElseThrow(()-> new RuntimeException("There is not enough data for Analysis"));
        Analysis analysis = analysisDto.toEntity(watchlist.getTicker());

        analysisRepository.save(analysis);
    }

}
