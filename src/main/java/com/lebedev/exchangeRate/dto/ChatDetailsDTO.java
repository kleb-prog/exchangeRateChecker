package com.lebedev.exchangeRate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public class ChatDetailsDTO {
    private String baseCurrency;
    private String targetCurrency;
    private BigDecimal threshold;
    @JsonFormat(pattern="dd-MM-yyyy HH:mm")
    private Date createdAt;

    public ChatDetailsDTO() {
    }

    public ChatDetailsDTO(String baseCurrency, String targetCurrency, BigDecimal threshold, Date createdAt) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.threshold = threshold;
        this.createdAt = createdAt;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
