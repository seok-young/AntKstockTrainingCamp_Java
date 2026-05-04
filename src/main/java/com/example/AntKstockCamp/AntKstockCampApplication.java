package com.example.AntKstockCamp;

import com.example.AntKstockCamp.domain.Entity.Watchlist;
import com.example.AntKstockCamp.repository.WatchlistRepository;
import com.example.AntKstockCamp.service.AnalysisService;
import com.example.AntKstockCamp.service.Collector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class AntKstockCampApplication {
	@Value(("${API.kiwoom.oauth}"))
	private String oauthEndPoint;

	public static void main(String[] args) {
		SpringApplication.run(AntKstockCampApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(
			Collector collector,
			AnalysisService analysisService,
			WatchlistRepository watchlistRepository
	) {
		return args -> {
			collector.runCollection();

			List<Watchlist> activeList = watchlistRepository.findActiveTickerSymbols();

			for (Watchlist item : activeList) {
				String symbol = item.getTicker().getSymbol();

				try {
					analysisService.runAnanlysis(symbol);
				} catch (Exception e) {
					System.err.println(symbol + "분석 중 오류 발생" + e.getMessage());
				}
			}
		};
	}
}

//	@Bean
//	CommandLineRunner run(Collector collector) {
//		return args -> {
//			collector.runCollection();
//			System.out.println("Success saving all the price data");
//		};
//	}

//	@Bean
//	CommandLineRunner run(AnalysisService analysisService, WatchlistRepository watchlistRepository) {
//		return args -> {
//			List<Watchlist> ticker_list = watchlistRepository.findActiveTickerSymbols();
//
//			for (Watchlist wl : ticker_list){
//				String symbol = wl.getTicker().getSymbol();
//				System.out.println("Start analyzing  - " + symbol);
//
//				try {
//					analysisService.saveAllAnalysis(symbol);
//				} catch (Exception e){
//					System.err.println("Error during analyzing" + symbol+ "  " +e.getMessage());
//				}
//			}
//			System.out.println("Finished analyzing all Stocks");
//		};
//	}}



//	@Bean
//	public CommandLineRunner test(Kiwoom_RestAPI kiwoomRestAPI){
//		return args -> {
//			String token = kiwoomRestAPI.getToken();
//			if (token != null) {
//				Map<String, Object> requestData = Map.of(
//						"stk_cd", "005930",
//						"qry_dt", "20240101",
//						"indc_tp", "0"
//				);
//
//				var response = kiwoomRestAPI.getPrice(token, requestData, "N","");
//				if (response != null){
//					System.out.println("주가 수집 성공" + response.data().get(0));
//				} else {
//					System.out.println("주가 수집 실패");
//				}
//			} else {
//				System.out.println("토큰 발급 실패");
//			}
//		};
//	}







