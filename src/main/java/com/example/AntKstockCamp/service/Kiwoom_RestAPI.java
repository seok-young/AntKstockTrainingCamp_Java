package com.example.AntKstockCamp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
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

    @Value(("${API.kiwoom.oauth}"))
    private String oauthEndPoint;

    @Value(("${API.kiwoom.search}"))
    private String searchEndPoint;

    private record TokenResponse(String token) {}
    private record ApiResponse(List<Map<String, Object>> data, String contYn, String nextKey) {}


    /*
    *  kiwoom 접근 토큰 발급
    */
    public String getToken(){
        try {

            Map<String, String> body = Map.of(
                    "grant_type", "client_credentials",
                    "appkey", appkey,
                    "secretkey", secretKey
            );

            return kiwoomRestClient.post()
                    .uri(oauthEndPoint)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        log.error("Error during calling Kiwoom API : {}", response.getStatusCode());
                        throw new RuntimeException("Fail getting Kiwoom access token");
                    })
                    .body(TokenResponse.class)
                    .token();
        } catch (Exception e) {
            log.error("Error during getting Kiwoom access token");
            return null;
        }
    }

    /*
     *  kiwoom 주가 조회
     */
    public ApiResponse getPrice(String token,
                                Map<String, Object> data,
                                String contYn,
                                String nextKey){
        try{
            var responseEntity = kiwoomRestClient.post()
                    .uri(searchEndPoint)
                    .header("autorization", "Bearer " + token)
                    .header("cont-yn", contYn)
                    .header("next-key", nextKey)
                    .header("api-id", "ka10086")
                    .body(data)
                    .retrieve()
                    .toEntity(Map.class);

            Map<String, Object> body = responseEntity.getBody();
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
