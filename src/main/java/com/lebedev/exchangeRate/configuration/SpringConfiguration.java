package com.lebedev.exchangeRate.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SpringConfiguration {

    @Value("${exchangeApiURLTemplate}")
    private String exchangeApiURLTemplate;

    @Value("${exchangeApiKey}")
    private String exchangeApiKey;

    @Value("${telegramToken}")
    private String telegramToken;

    public String getExchangeApiURLTemplate() {
        return exchangeApiURLTemplate;
    }

    public String getExchangeApiKey() {
        return exchangeApiKey;
    }

    public String getTelegramToken() {
        return telegramToken;
    }
}
