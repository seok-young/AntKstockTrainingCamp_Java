package com.example.AntKstockCamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("token") String token,
        @JsonProperty("return_code") Integer return_code,
        @JsonProperty("return_msg") String return_msg
) {}
