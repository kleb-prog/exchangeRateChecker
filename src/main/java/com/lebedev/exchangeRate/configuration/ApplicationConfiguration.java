package com.lebedev.exchangeRate.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class ApplicationConfiguration {

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

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool();
    }
}
