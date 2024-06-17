package com.lebedev.exchangeRate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Repository
public class ChatStorageRepository {

    private static final String CHAT_STATE = "chat/%sState";
    private static final String CHAT_STATE_VARIABLE = "chat/%sVar";

    @Autowired
    private JedisPool jedisPool;

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

    public void setChatVariable(String chatId, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(CHAT_STATE_VARIABLE, chatId), value);
        }
    }

    public String getChatVariable(String chatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(CHAT_STATE_VARIABLE, chatId));
        }
    }

    public void removeChatVariable(String chatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.getDel(String.format(CHAT_STATE_VARIABLE, chatId));
        }
    }
}
