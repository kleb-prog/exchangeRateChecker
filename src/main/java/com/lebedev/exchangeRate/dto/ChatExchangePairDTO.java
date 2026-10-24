package com.lebedev.exchangeRate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public record ChatExchangePairDTO(
        Long chatId,
        Long pairId,
        BigDecimal threshold,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm") Date createdAt) {
}
