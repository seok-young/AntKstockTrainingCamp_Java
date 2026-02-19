package com.example.AntKstockCamp.service;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public record TokenResponse(
            @JsonProperty("token") String token,
            @JsonProperty("return_code") Integer return_code,
            @JsonProperty("return_msg") String return_msg
    ) {}
    public record ApiResponse(List<Map<String, Object>> data, String contYn, String nextKey) {}


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
                System.out.println("토큰: " + response.token());
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
            System.out.println("getPrice 진입 성공");
            var responseEntity = kiwoomRestClient.post()
                    .uri(searchEndPoint)
                    .header("authorization", "Bearer " + token)
                    .header("cont-yn", contYn)
                    .header("next-key", nextKey)
                    .header("api-id", "ka10086")
                    .body(data)
                    .retrieve()
                    .toEntity(Map.class);
            System.out.println("키움 응답 수신 완료: " + responseEntity.getStatusCode());
            Map<String, Object> body = responseEntity.getBody();
            System.out.println("응답 바디 내용: " + body);
            String resContYn = responseEntity.getHeaders().getFirst("cont-yn");
            String resNextKey = responseEntity.getHeaders().getFirst("next-key");

            List<Map<String, Object>> dailyData = (List<Map<String, Object>>) body.get("daly_stkpc");

            return new ApiResponse(dailyData, resContYn, resNextKey);
        } catch (Exception e) {
            log.error("Error during Kiwoom API 10086{}, ", e.getMessage());
            return null;
        }
    }

}
