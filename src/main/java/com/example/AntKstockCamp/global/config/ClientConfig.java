package com.example.AntKstockCamp.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

//    @Value("${API.kiwoom.host}")
//    public String apiUrl;


    @Bean
    public RestClient kiwoomRestClient(@Value("${API.kiwoom.host}") String apiUrl){
//        System.out.println(apiUrl);
        return RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json;charset=UTF-8")
                .build();
    }
}
