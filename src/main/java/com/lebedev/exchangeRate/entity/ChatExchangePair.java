package com.lebedev.exchangeRate.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "chat_exchange_pairs")
public class ChatExchangePair {

    @EmbeddedId
    private ChatExchangePairId id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    private Chat chat;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pairId")
    private ExchangePair exchangePair;

    @Column(nullable = false)
    private BigDecimal threshold;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    public ChatExchangePairId getId() {
        return id;
    }

    public void setId(ChatExchangePairId id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public ExchangePair getExchangePair() {
        return exchangePair;
    }

    public void setExchangePair(ExchangePair exchangePair) {
        this.exchangePair = exchangePair;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

