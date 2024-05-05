package com.lebedev.exchangeRate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static java.util.Locale.ROOT;

@Repository
public class RatesCacheRepository {

    private static final String CACHE_CURRENCY_RATE_TEMPLATE = "cacheCurRate:%sTo%s";
    private static final long TTL = 300L; // 5 min

    @Autowired
    private JedisPool jedisPool;

    public void setCacheRate(String base, String target, String value) {
        if (value == null) {
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(getFormatted(base, target), TTL, value);
        }
    }

    public String getCacheRate(String base, String target) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(getFormatted(base, target));
        }
    }

    private static String getFormatted(String base, String target) {
        return String.format(CACHE_CURRENCY_RATE_TEMPLATE, base.toUpperCase(ROOT), target.toUpperCase(ROOT));
    }
}
