package com.example.AntKstockCamp.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record IndicatorDto(
        String ticker,
        LocalDate date,
        float close_price,
        float ma5,
        float ma20,
        float ma60,
        float ma120,
        float rsi,
        float macd,
        float macdSignal,
        float macdHist,
        float bbUpper,
        float bbMiddle,
        float bbLower
) {}
