package com.lebedev.exchangeRate.mapper;

import com.lebedev.exchangeRate.dto.ChatDTO;
import com.lebedev.exchangeRate.dto.ChatDetailsDTO;
import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ExchangePair;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMapperTest {

    private final ChatMapper chatMapper = Mappers.getMapper(ChatMapper.class);
    private final ChatDetailsMapper chatDetailsMapper = Mappers.getMapper(ChatDetailsMapper.class);

    @Test
    void mapsChatToDto() {
        Chat chat = new Chat();
        chat.setChatId(42L);
        chat.setFirstName("Ada");
        chat.setLastName("Lovelace");
        Instant createdAt = Instant.now();
        chat.setCreatedAt(createdAt);

        ChatDTO result = chatMapper.toDto(chat);

        assertThat(result.getChatId()).isEqualTo(42L);
        assertThat(result.getFirstName()).isEqualTo("Ada");
        assertThat(result.getLastName()).isEqualTo("Lovelace");
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void mapsExchangePairDetailsToDto() {
        ExchangePair exchangePair = new ExchangePair();
        exchangePair.setBaseCurrency("USD");
        exchangePair.setTargetCurrency("RUB");

        ChatExchangePair chatExchangePair = new ChatExchangePair();
        chatExchangePair.setExchangePair(exchangePair);
        chatExchangePair.setThreshold(new BigDecimal("90.50"));

        ChatDetailsDTO result = chatDetailsMapper.toDto(chatExchangePair);

        assertThat(result.baseCurrency()).isEqualTo("US Dollar");
        assertThat(result.targetCurrency()).isEqualTo("Russian Ruble");
        assertThat(result.threshold()).isEqualByComparingTo("90.50");
    }
}
