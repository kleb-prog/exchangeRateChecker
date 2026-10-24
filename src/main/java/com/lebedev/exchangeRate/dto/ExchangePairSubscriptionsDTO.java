package com.lebedev.exchangeRate.dto;

import java.util.List;

public record ExchangePairSubscriptionsDTO(
        ExchangePairDTO exchangePair,
        List<ChatExchangePairDTO> subscriptions) {
}
