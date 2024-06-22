package com.lebedev.exchangeRate.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChatExchangePairId implements Serializable {

    private Long chatId;
    private Long pairId;

    public ChatExchangePairId() {
    }

    public ChatExchangePairId(Long chatId, Long pairId) {
        this.chatId = chatId;
        this.pairId = pairId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatExchangePairId that = (ChatExchangePairId) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(pairId, that.pairId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, pairId);
    }
}

