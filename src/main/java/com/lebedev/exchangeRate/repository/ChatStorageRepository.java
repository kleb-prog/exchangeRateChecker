package com.lebedev.exchangeRate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Repository
public class ChatStorageRepository {

    private static final String CHAT_ID_SET_KEY = "telegramChatIDs";
    private static final String CHAT_STATE = "chat/%sState";

    @Autowired
    private JedisPool jedisPool;

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

    public String getChatState(String chatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(CHAT_STATE, chatId));
        }
    }

    public void setChatState(String chatId, String state) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(CHAT_STATE, chatId), state);
        }
    }

    public void removeChatState(String chatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.getDel(String.format(CHAT_STATE, chatId));
        }
    }
}
