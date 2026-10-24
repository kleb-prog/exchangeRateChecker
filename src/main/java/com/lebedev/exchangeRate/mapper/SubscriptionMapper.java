package com.lebedev.exchangeRate.mapper;

import com.lebedev.exchangeRate.dto.ChatExchangePairDTO;
import com.lebedev.exchangeRate.dto.ExchangePairDTO;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ExchangePair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "baseCurrency", source = "baseCurrency.currencyCode")
    @Mapping(target = "targetCurrency", source = "targetCurrency.currencyCode")
    ExchangePairDTO toDto(ExchangePair exchangePair);

    @Mapping(target = "chatId", source = "chat.chatId")
    @Mapping(target = "pairId", source = "exchangePair.pairId")
    ChatExchangePairDTO toDto(ChatExchangePair chatExchangePair);
}
