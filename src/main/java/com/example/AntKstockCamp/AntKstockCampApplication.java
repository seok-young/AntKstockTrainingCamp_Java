package com.example.AntKstockCamp;

import com.example.AntKstockCamp.service.Collector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AntKstockCampApplication {
	@Value(("${API.kiwoom.oauth}"))
	private String oauthEndPoint;

	public static void main(String[] args) {

		SpringApplication.run(AntKstockCampApplication.class, args);
	}

//	@Bean
//	CommandLineRunner run(Collector collector) {
//		return args -> {
//			collector.runCollection();
//			System.out.println("Success saving all the price data");
//		};
//	}

	@Bean
	CommandLineRunner run(Collector collector) {
		List<String> stockList = new ArrayList<>(List.of("005930","000660","034730","001500","000270","035420","035720","207940","066570","005490",
				"006400","051910","012330","009540","018880","105560","055550","086790","032830","034220",
				"009150","009830","051900","096770","086280","010950","097950","090430","033780","011170",
				"323410","247540","112040","377300","035760","086520","196170","263750","011200","357780"
));
		List<String> etfList = new ArrayList<>(List.of("069500","102110","229200","143860","091160","091170","228800","469790","360750","132030"));
		// ETF, Stock
		return args -> {
			collector.saveAllWatchlist(stockList, "Stock");
			collector.saveAllWatchlist(etfList, "ETF");
		};
	}

}

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







