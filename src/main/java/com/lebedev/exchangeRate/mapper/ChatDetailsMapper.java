package com.lebedev.exchangeRate.mapper;

import com.lebedev.exchangeRate.dto.ChatDetailsDTO;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Currency;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface ChatDetailsMapper {

    @Mapping(target = "baseCurrency", source = "exchangePair.baseCurrency", qualifiedByName = "displayName")
    @Mapping(target = "targetCurrency", source = "exchangePair.targetCurrency", qualifiedByName = "displayName")
    ChatDetailsDTO toDto(ChatExchangePair chatExchangePair);

    @Named("displayName")
    default String displayName(Currency currency) {
        return currency == null ? null : currency.getDisplayName(Locale.ENGLISH);
    }
}
