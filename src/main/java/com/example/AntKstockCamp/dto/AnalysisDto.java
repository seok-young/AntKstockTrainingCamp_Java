package com.example.AntKstockCamp.dto;

import com.example.AntKstockCamp.domain.Entity.Analysis;
import com.example.AntKstockCamp.domain.Entity.Ticker;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AnalysisDto(
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
) {
    public Analysis toEntity(Ticker tickerEntity) {
        return Analysis.builder()
                .ticker(tickerEntity)
                .date(this.date)
                .closePrice(this.close_price)
                .ma5(this.ma5)
                .ma20(this.ma20)
                .ma60(this.ma60)
                .ma120(this.ma120)
                .rsi(this.rsi)
                .macd(this.macd)
                .macdSignal(this.macdSignal)
                .macdHist(this.macdHist)
                .bbUpper(this.bbUpper)
                .bbMiddle(this.bbMiddle)
                .bbLower(this.bbLower)
                .build();
    }
}
