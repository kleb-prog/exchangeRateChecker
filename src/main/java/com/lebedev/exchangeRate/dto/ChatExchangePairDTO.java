package com.lebedev.exchangeRate.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ChatExchangePairDTO(
        Long chatId,
        Long pairId,
        BigDecimal threshold,
        Instant createdAt) {
}
