package com.example.AntKstockCamp.service;

import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import com.example.AntKstockCamp.domain.Entity.Ticker;
import com.example.AntKstockCamp.dto.ApiResponse;
import com.example.AntKstockCamp.dto.DailyPriceDto;
import com.example.AntKstockCamp.repository.DailyPriceRepository;
import com.example.AntKstockCamp.repository.TickerRepository;
import com.example.AntKstockCamp.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Collector {

    private final WatchlistRepository watchlistRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final TickerRepository tickerRepository;
    private final Kiwoom_RestAPI kiwoomRestAPI;



    /*
     * 주식 메타데이터 불러오기
     */
    public void readStockCSV(String assetType, String filepath) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filepath), "EUC-KR"))) {

            String line;
            br.readLine(); // 헤더 스킵

            List<Ticker> tickerList = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length < 12) continue;

                String symbolOrigin = data[0].replace("\"", "").trim();
                String symbol = data[1].replace("\"", "").trim(); // 알파벳 보존

                Ticker ticker = new Ticker();
                ticker.setSymbolOrigin(symbolOrigin);
                ticker.setSymbol(symbol);
                ticker.setNameKor(data[2].replace("\"", "").trim());
                ticker.setDateListing(data[5].replace("\"", "").trim());
                ticker.setMarketType(data[6].replace("\"", "").trim());
                ticker.setAssetType(assetType);

                String sharesStr = data[11].replace("\"", "").replaceAll("[^0-9]", "");
                ticker.setTotalShares(sharesStr.isEmpty() ? 0L : Long.parseLong(sharesStr));

                ticker.setIsActive(true);
                tickerList.add(ticker);

                // 500개 쌓일 때마다 DB 저장 후 리스트 비우기
                if (tickerList.size() >= 500) {
                    tickerRepository.saveAll(tickerList);
                    tickerRepository.flush(); // 영속성 컨텍스트를 DB에 반영하여 메모리 확보
                    tickerList.clear();

                }
            }


            if (!tickerList.isEmpty()) {
                tickerRepository.saveAll(tickerList);
                tickerRepository.flush();
            }

            System.out.println("Success saving Stock metadata");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 주식 메타데이터 불러오기
     */
    public void readETFCSV(String filepath) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filepath), "EUC-KR"))) {

            String line;
            br.readLine(); // 헤더 스킵

            List<Ticker> tickerList = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length < 12) continue;

                System.out.println("data" + Arrays.toString(data));

                String symbolOrigin = data[0].replace("\"", "").trim();
                String symbol = data[1].replace("\"", "").trim(); // 알파벳 보존
//
                Ticker ticker = new Ticker();
                ticker.setSymbolOrigin(symbolOrigin);
                ticker.setSymbol(symbol);
                ticker.setNameKor(data[2].replace("\"", "").trim());
                ticker.setDateListing(data[5].replace("\"", "").trim());
                ticker.setMarketType(data[10].replace("\"", "").trim());
                ticker.setAssetType("ETF");
//
                String sharesStr = data[12].replace("\"", "").replaceAll("[^0-9]", "");
                ticker.setTotalShares(sharesStr.isEmpty() ? 0L : Long.parseLong(sharesStr));
//
                ticker.setIsActive(true);
                System.out.println("ticker" + ticker);
                tickerList.add(ticker);


//                // 500개 쌓일 때마다 DB 저장 후 리스트 비우기
                if (tickerList.size() >= 500) {
                    tickerRepository.saveAll(tickerList);
                    tickerRepository.flush(); // 영속성 컨텍스트를 DB에 반영하여 메모리 확보
                    tickerList.clear();

                }
            }


            if (!tickerList.isEmpty()) {
                tickerRepository.saveAll(tickerList);
                tickerRepository.flush();
            }

            System.out.println("Success saving Stock metadata");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    /*
    * 관심종목 불러오기
    */
    public List<Ticker> getInterestStocksID() {
        try {
            return watchlistRepository.findActiveTickerSymbols();
        } catch (RuntimeException e) {
            log.error("Error during gettingInterestStocksID");
            return Collections.emptyList();
        }
    }

    /*
     * 데이터 최신날짜 가져오기
     */
    public LocalDate getLateDate(String symbol) {
        try {
            return dailyPriceRepository.findLateDateBySymbol(symbol)
                    .orElse(LocalDate.of(2000, 1, 1));
        } catch (RuntimeException e) {
            log.error("Error during gettingLateDate");
            return LocalDate.of(2000, 1, 1);
        }
    }

    /*
     * 연속적으로 장기간 주가 수집
     */
    public List<DailyPriceDto> collectLongTermData(String token, String stkCd, LocalDate startDate){

        List<DailyPriceDto> allData = new ArrayList<>();
        String qryDt = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String contYn = "N";
        String nextKey = "";

        while (true){
            Map<String, Object> params = new HashMap<>();
            params.put("stk_cd", stkCd);
            params.put("qry_dt", qryDt);
            params.put("indc_tp","0");

            ApiResponse response = kiwoomRestAPI.getPrice(token, params, contYn, nextKey);

            if (response == null || response.daly_stkpc() ==null || response.daly_stkpc().isEmpty()){
                break;
            }

            boolean stopSignal = false;
            for(DailyPriceDto dto : response.daly_stkpc()){
                LocalDate recordDate = LocalDate.parse(dto.date(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                if (recordDate.isBefore(startDate)){
                    stopSignal = true;
                    break;
                }
                allData.add(dto);
            }

            if (stopSignal) break;

            if("Y".equals(response.contYn()) && response.nextKey() != null && !response.nextKey().isEmpty()){
                contYn = "Y";
                nextKey = response.nextKey();

                try {Thread.sleep(550);} catch (InterruptedException e){ Thread.currentThread().interrupt();}
            } else {
                break;
            }


        }

        return  allData;
    }

    /*
     * 최초 종가 수집 오케스트라
     */
    public void firstRunCollection(){
        List<Ticker> tickerList = watchlistRepository.findActiveTickerSymbols();
        if (tickerList.isEmpty()){
            System.out.println("No TickerSymbol in Interest");
            return;
        }

        LocalDate lastDate = dailyPriceRepository.findLateDateBySymbol(tickerList.get(0).getSymbol())
                .orElse(LocalDate.now().minusMonths(6));

        LocalDate startDate = lastDate.plusDays(1);

        // 최신 데이터 이미 있다면 메서드 종료
        if(!lastDate.isBefore(LocalDate.now().plusDays(1))){
            System.out.println("You collected latest data already!");
            return;
        }

        String token = kiwoomRestAPI.getToken();

        for (Ticker ticker : tickerList){
            List<DailyPriceDto> dtoList  = collectLongTermData(token, ticker.getSymbol(), startDate);
            List<DailyPrice> entities = dtoList.stream()
                    .filter(dto -> {
                        LocalDate dtoDate = LocalDate.parse(dto.date(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        // lastDate보다 이후인 데이터만 통과 (중복 방지)
                        return dtoDate.isAfter(lastDate);
                    })
                    .map(dto -> dto.toEntity(ticker))
                    .toList();

            if (!entities.isEmpty()) {
                dailyPriceRepository.saveAll(entities);
            }
            System.out.println("Success saving total " + entities.size());
        }
    }




    /*
    * 종가 수집 오케스트라
    */
    public void runCollection(){
        List<Ticker> tickerList = watchlistRepository.findActiveTickerSymbols();
        if (tickerList.isEmpty()){
            System.out.println("No TickerSymbol in Interest");
            return;
        }

        LocalDate lastDate = dailyPriceRepository.findLateDateBySymbol(tickerList.get(0).getSymbol())
                .orElse(LocalDate.now().minusMonths(6));

        LocalDate startDate = lastDate.plusDays(1);

        // 최신 데이터 이미 있다면 메서드 종료
        if(!lastDate.isBefore(LocalDate.now().plusDays(1))){
            System.out.println("You collected latest data already!");
            return;
        }

        String token = kiwoomRestAPI.getToken();

        for (Ticker ticker : tickerList){
            List<DailyPriceDto> dtoList  = collectLongTermData(token, ticker.getSymbol(), startDate);
            List<DailyPrice> entities = dtoList.stream()
                    .filter(dto -> {
                        LocalDate dtoDate = LocalDate.parse(dto.date(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        // lastDate보다 이후인 데이터만 통과 (중복 방지)
                        return dtoDate.isAfter(lastDate);
                    })
                    .map(dto -> dto.toEntity(ticker))
                    .toList();

            if (!entities.isEmpty()) {
                dailyPriceRepository.saveAll(entities);
            }
            System.out.println("Success saving total " + entities.size());
        }
    }

}
