package com.lebedev.exchangeRate.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lebedev.exchangeRate.util.CurrencyUtil;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Currency;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "exchange_pairs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"baseCurrency", "targetCurrency"})
})
public class ExchangePair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pairId;

    @Column(nullable = false)
    private Currency baseCurrency;

    @Column(nullable = false)
    private Currency targetCurrency;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "exchangePair", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatExchangePair> chatExchangePairs;

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = CurrencyUtil.createCurrencyByCode(baseCurrency);
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = CurrencyUtil.createCurrencyByCode(targetCurrency);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<ChatExchangePair> getChatExchangePairs() {
        return chatExchangePairs;
    }

    public void setChatExchangePairs(Set<ChatExchangePair> chatExchangePairs) {
        this.chatExchangePairs = chatExchangePairs;
    }
}

