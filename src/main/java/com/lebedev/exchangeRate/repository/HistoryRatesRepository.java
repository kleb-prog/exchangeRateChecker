package com.lebedev.exchangeRate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class HistoryRatesRepository {

    private static final String CURRENCY_RATES_TEMPLATE = "old%sTo%sRate";

    @Autowired
    private JedisPool jedisPool;

    public void saveExchangeRate(String base, String target, String rate) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(CURRENCY_RATES_TEMPLATE, base, target), rate);
        }
    }

    public String getPreviousChangeRate(String base, String target) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(CURRENCY_RATES_TEMPLATE, base, target));
        }
    }
}
