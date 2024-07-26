package com.lebedev.exchangeRate.service;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ChatExchangePairId;
import com.lebedev.exchangeRate.entity.ExchangePair;
import com.lebedev.exchangeRate.repository.ChatExchangePairRepository;
import com.lebedev.exchangeRate.repository.ChatRepository;
import com.lebedev.exchangeRate.repository.ExchangePairRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SubscriptionService {

    private final ChatRepository chatRepository;
    private final ExchangePairRepository exchangePairRepository;
    private final ChatExchangePairRepository chatExchangePairRepository;

    public SubscriptionService(ChatRepository chatRepository,
                               ExchangePairRepository exchangePairRepository,
                               ChatExchangePairRepository chatExchangePairRepository) {
        this.chatRepository = chatRepository;
        this.exchangePairRepository = exchangePairRepository;
        this.chatExchangePairRepository = chatExchangePairRepository;
    }

    @Transactional
    public Chat createChat(Long chatId, String firstName, String lastName) {
        Chat chat = new Chat();
        chat.setChatId(chatId);
        chat.setFirstName(firstName);
        chat.setLastName(lastName);
        return chatRepository.save(chat);
    }

    @Transactional
    public Optional<Chat> findChat(Long chatId) {
        return chatRepository.findChatByIdWithDetails(chatId);
    }

    @Transactional
    public void deleteChat(Chat chat) {
        chatRepository.delete(chat);
    }

    @Transactional
    public ExchangePair createExchangePair(String baseCurrency, String targetCurrency) {
        ExchangePair exchangePair = new ExchangePair();
        exchangePair.setBaseCurrency(baseCurrency);
        exchangePair.setTargetCurrency(targetCurrency);

        Optional<ExchangePair> existingPair = exchangePairRepository
                .findByBaseCurrencyAndTargetCurrency(exchangePair.getBaseCurrency(), exchangePair.getTargetCurrency());
        if (existingPair.isPresent()) {
            throw new IllegalArgumentException(String.format("Exchange pair with %s and %s already exists", baseCurrency, targetCurrency));
        }

        return exchangePairRepository.save(exchangePair);
    }

    @Transactional
    public List<ExchangePair> getExchangePairs() {
        return exchangePairRepository.findAll();
    }

    @Transactional
    public Optional<ExchangePair> findExchangePairByBaseAndTargetCurrency(Currency baseCurrency, Currency targetCurrency) {
        return exchangePairRepository.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency);
    }

    @Transactional
    public void removeChatExchangePairById(ChatExchangePairId chatExchangePairId) {
        chatExchangePairRepository.deleteById(chatExchangePairId);
    }

    @Transactional
    public ChatExchangePair addExchangePairToChat(Long chatId, Long pairId, BigDecimal threshold) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        Optional<ExchangePair> exchangePairOpt = exchangePairRepository.findById(pairId);

        if (chatOpt.isPresent() && exchangePairOpt.isPresent()) {
            ChatExchangePair chatExchangePair = new ChatExchangePair();
            chatExchangePair.setChat(chatOpt.get());
            chatExchangePair.setExchangePair(exchangePairOpt.get());
            chatExchangePair.setThreshold(threshold);

            ChatExchangePairId chatExchangePairId = new ChatExchangePairId();
            chatExchangePairId.setChatId(chatId);
            chatExchangePairId.setPairId(pairId);
            chatExchangePair.setId(chatExchangePairId);

            return chatExchangePairRepository.save(chatExchangePair);
        } else {
            throw new RuntimeException("Chat or ExchangePair not found");
        }
    }

    @Transactional
    public Map<ExchangePair, List<ChatExchangePair>> getAllExchangePairsWithChatsGrouped() {
        List<ChatExchangePair> chatExchangePairs = chatExchangePairRepository.findAllWithDetails();
        Map<ExchangePair, List<ChatExchangePair>> groupedByExchangePair = new HashMap<>();

        for (ChatExchangePair chatExchangePair : chatExchangePairs) {
            ExchangePair exchangePair = chatExchangePair.getExchangePair();
            groupedByExchangePair
                    .computeIfAbsent(exchangePair, k -> new ArrayList<>())
                    .add(chatExchangePair);
        }

        return groupedByExchangePair;
    }
}
