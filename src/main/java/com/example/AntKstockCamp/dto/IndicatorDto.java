package com.example.AntKstockCamp.dto;

import lombok.Builder;

@Builder
public record IndicatorDto(
        double ma5,
        double ma20,
        double ma60,
        double ma120,
        double rsi,
        double macd,
        double macdSignal,
        double macdHist,
        double bbUpper,
        double bbMiddle,
        double bbLower
) {}
