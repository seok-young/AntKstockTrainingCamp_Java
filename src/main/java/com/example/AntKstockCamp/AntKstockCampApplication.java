package com.example.AntKstockCamp;

import com.example.AntKstockCamp.dto.AnalysisDto;
import com.example.AntKstockCamp.service.AnalysisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

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
	CommandLineRunner run(AnalysisService analysisService) {
		return args -> {
			var response = analysisService.getPriceDataForAnalysis("000270", LocalDate.of(2025, 12, 31));
			if(response != null) {
				System.out.println("주가정보" + response);
				AnalysisDto indicators = analysisService.getIndicators("000270",LocalDate.of(2025, 12, 31))
								.orElse(null);
				System.out.println("지표정보" + indicators);
			} else {
				System.out.println("주가정보 수집 실패");
			}
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







