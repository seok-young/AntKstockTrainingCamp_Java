package com.example.AntKstockCamp.dto;

import com.example.AntKstockCamp.domain.Entity.DailyPrice;
import com.example.AntKstockCamp.domain.Entity.Ticker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DailyPriceDto (
        String date,
        String open_pric,
        String high_pric,
        String low_pric,
        String close_pric,
        String trde_qty
){
    public Float parsePrice(String priceStr){
        if (priceStr == null || priceStr.isEmpty()) return 0f;
        String cleaned = priceStr.replaceAll("[^0-9.]","");
        return  cleaned.isEmpty() ? 0f : Float.parseFloat(cleaned);
    }

    public DailyPrice toEntity(Ticker tickerEntity){
        return DailyPrice.builder()
                .ticker(tickerEntity)
                .date(LocalDate.parse(this.date, DateTimeFormatter.ofPattern("yyyyMMdd")))
                .openPrice(parsePrice(open_pric))
                .highPrice(parsePrice(high_pric))
                .lowPrice(parsePrice(low_pric))
                .closePrice(parsePrice(close_pric))
                .trdeQty(Long.parseLong(trde_qty.replace("+","")))
                .build();
    }
}
