package com.example.AntKstockCamp.service;

import com.example.AntKstockCamp.dto.ApiResponse;
import com.example.AntKstockCamp.dto.DailyPriceDto;
import com.example.AntKstockCamp.dto.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class Kiwoom_RestAPI {

    private final ObjectMapper objectMapper;
    private final RestClient kiwoomRestClient;

    @Value("${API.kiwoom.app_key}")
    private String appkey;

    @Value("${API.kiwoom.secret_key}")
    private String secretKey;

    @Value("${API.kiwoom.host}")
    private String host;

    @Value(("${API.kiwoom.oauth}"))
    private String oauthEndPoint;

    @Value(("${API.kiwoom.search}"))
    private String searchEndPoint;



    /*
    *  kiwoom 접근 토큰 발급
    */
    public String getToken() {
        System.out.println("oauthEndpoint = " + oauthEndPoint);
        try {

            Map<String, String> body = Map.of(
                    "grant_type", "client_credentials",
                    "appkey", appkey,
                    "secretkey", secretKey
            );

            TokenResponse response = kiwoomRestClient.post()
                    .uri(oauthEndPoint)
                    .body(body)
                    .retrieve()
                    .body(TokenResponse.class);

            if (response != null && response.token() != null) {
                System.out.println("토큰 발급 성공" );
                return response.token();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     *  kiwoom 주가 수집
     */
    public ApiResponse getPrice(String token,
                                Map<String, Object> data,
                                String contYn,
                                String nextKey){
        try{
            var responseEntity = kiwoomRestClient.post()
                    .uri(searchEndPoint)
                    .header("authorization", "Bearer " + token)
                    .header("cont-yn", contYn)
                    .header("next-key", nextKey)
                    .header("api-id", "ka10086")
                    .body(data)
                    .retrieve()
                    .toEntity(Map.class);
            Map<String, Object> body = responseEntity.getBody();
            System.out.println("응답 바디 내용: " + body);
            String resContYn = responseEntity.getHeaders().getFirst("cont-yn");
            String resNextKey = responseEntity.getHeaders().getFirst("next-key");

            List<Map<String, Object>> dailyDataRaw = (List<Map<String, Object>>) body.get("daly_stkpc");

            List<DailyPriceDto> dailyPriceDtos = dailyDataRaw.stream()
                    .map(map -> objectMapper.convertValue(map, DailyPriceDto.class))
                    .toList();

            return new ApiResponse(dailyPriceDtos, resContYn, resNextKey);
        } catch (Exception e) {
            log.error("Error during Kiwoom API 10086{}, ", e.getMessage());
            return null;
        }
    }

}
