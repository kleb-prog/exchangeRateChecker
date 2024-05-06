package com.lebedev.exchangeRate.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static java.util.Locale.ROOT;

@Repository
public class RatesCacheRepository {

    private static final Logger logger = LoggerFactory.getLogger(RatesCacheRepository.class);

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
        } catch (JedisConnectionException e) {
            logger.error("Failed to set cache value", e);
        }
    }

    public String getCacheRate(String base, String target) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(getFormatted(base, target));
        } catch (JedisConnectionException e) {
            logger.error("Failed to get cache value", e);
            return null;
        }
    }

    private static String getFormatted(String base, String target) {
        return String.format(CACHE_CURRENCY_RATE_TEMPLATE, base.toUpperCase(ROOT), target.toUpperCase(ROOT));
    }
}
