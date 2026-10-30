package com.lebedev.exchangeRate.dto;

import java.time.Instant;

public record ExchangePairDTO(
        Long pairId,
        String baseCurrency,
        String targetCurrency,
        Instant createdAt) {
}
