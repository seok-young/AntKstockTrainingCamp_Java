package com.example.AntKstockCamp;

import com.example.AntKstockCamp.service.Kiwoom_RestAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class AntKstockCampApplication {
	@Value(("${API.kiwoom.oauth}"))
	private String oauthEndPoint;

	public static void main(String[] args) {

		SpringApplication.run(AntKstockCampApplication.class, args);
	}

	@Bean
	public CommandLineRunner test(Kiwoom_RestAPI kiwoomRestAPI){
		return args -> {
			String token = kiwoomRestAPI.getToken();
			if (token != null) {
				Map<String, Object> requestData = Map.of(
						"stk_cd", "005930",
						"qry_dt", "20240101",
						"indc_tp", "0"
				);

				var response = kiwoomRestAPI.getPrice(token, requestData, "N","");
				if (response != null){
					System.out.println("주가 수집 성공" + response.data().get(0));
				} else {
					System.out.println("주가 수집 실패");
				}
			} else {
				System.out.println("토큰 발급 실패");
			}
		};
	}

}
