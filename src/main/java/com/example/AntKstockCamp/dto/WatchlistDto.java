package com.example.AntKstockCamp.dto;

import com.example.AntKstockCamp.domain.Entity.Ticker;
import com.example.AntKstockCamp.domain.Entity.Watchlist;
import lombok.Builder;



@Builder
public record WatchlistDto(
        String assetType,
        String symbol
) {
    public Watchlist toEntity(Ticker ticker){
        return Watchlist.builder()
                .assetType(this.assetType)
                .ticker(ticker)
                .isWatching(true)
                .build();
    }
}
