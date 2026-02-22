package com.example.AntKstockCamp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public record ApiResponse(List<DailyPriceDto> daly_stkpc, String contYn, String nextKey) {}

