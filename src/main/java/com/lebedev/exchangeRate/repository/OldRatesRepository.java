package com.lebedev.exchangeRate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class OldRatesRepository {

    private static final String USD_RATES_TEMPLATE = "old%sTo%sRate";

    @Autowired
    private JedisPool jedisPool;

    public void saveExchangeRate(String base, String target, String rate) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(USD_RATES_TEMPLATE, base, target), rate);
        }
    }

    public String getOldChangeRate(String base, String target) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(USD_RATES_TEMPLATE, base, target));
        }
    }
}
