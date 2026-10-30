package com.lebedev.exchangeRate.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ChatDetailsDTO(
        String baseCurrency,
        String targetCurrency,
        BigDecimal threshold,
        Instant createdAt) {
}
