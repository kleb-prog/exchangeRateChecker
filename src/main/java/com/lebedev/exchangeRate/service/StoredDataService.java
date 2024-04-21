package com.lebedev.exchangeRate.service;

import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Repository
public class StoredDataService {

    private static final String CHAT_ID_SET_KEY = "telegramChatIDs";
    private static final String USD_RATES_TEMPLATE = "usdTo%sRates";

    private final JedisPool jedisPool = new JedisPool();

    public void saveChatId(String chatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(CHAT_ID_SET_KEY, chatId);
        }
    }

    public void removeChatId(String chatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.srem(CHAT_ID_SET_KEY, chatId);
        }
    }

    public Set<String> getAllChatIds() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(CHAT_ID_SET_KEY);
        }
    }

    public void saveUsdRate(String currencyName, String rate) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(USD_RATES_TEMPLATE, currencyName), rate);
        }
    }

    public String getStoredUsdRate(String currencyName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(USD_RATES_TEMPLATE, currencyName));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
