package com.lebedev.exchangeRate.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Getter
@Configuration
public class ApplicationConfiguration {

    @Value("${exchangeApiKey}")
    private String exchangeApiKey;

    @Value("${telegramToken}")
    private String telegramToken;

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool();
    }
}
