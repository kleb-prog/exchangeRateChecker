package com.lebedev.exchangeRate.repository;

import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Repository
public class StoredDataService {

    private static final String CHAT_ID_SET_KEY = "telegramChatIDs";
    private static final String USD_RATES_TEMPLATE = "usdTo%sRates";
    private static final String CHAT_STATE = "chat/%sState";

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
        }
    }

    public String getChatState(String chatId) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(CHAT_STATE, chatId));
        }
    }

    public void setChatState(String chatId, String state) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(CHAT_STATE, chatId), state);
        }
    }

    public void removeChatState(String chatId) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.getDel(String.format(CHAT_STATE, chatId));
        }
    }
}
