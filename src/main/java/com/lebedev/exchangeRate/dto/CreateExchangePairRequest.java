package com.lebedev.exchangeRate.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateExchangePairRequest(
        @NotBlank String baseCurrency,
        @NotBlank String targetCurrency) {
}
