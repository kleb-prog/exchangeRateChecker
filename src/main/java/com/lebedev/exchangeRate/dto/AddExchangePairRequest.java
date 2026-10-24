package com.lebedev.exchangeRate.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddExchangePairRequest(
        @NotNull BigDecimal threshold) {
}
