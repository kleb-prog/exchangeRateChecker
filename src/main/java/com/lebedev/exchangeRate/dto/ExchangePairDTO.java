package com.lebedev.exchangeRate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record ExchangePairDTO(
        Long pairId,
        String baseCurrency,
        String targetCurrency,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm") Date createdAt) {
}
