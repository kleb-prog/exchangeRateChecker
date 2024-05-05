package com.lebedev.exchangeRate.service.telegramBot;

import com.lebedev.exchangeRate.configuration.SpringConfiguration;
import com.lebedev.exchangeRate.repository.StoredDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

@Service
public class TelegramMessageService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramMessageService.class);

    private final TelegramClient telegramClient;
    private final StoredDataService dataService;

    public TelegramMessageService(SpringConfiguration configuration, StoredDataService dataService) {
        this.telegramClient = new OkHttpTelegramClient(configuration.getTelegramToken());
        this.dataService = dataService;
    }

    public boolean sendMessage(String chatId, String message) {
        SendMessage messageObject = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
        try {
            telegramClient.execute(messageObject);
            return true;
        } catch (TelegramApiException e) {
            logger.error("Failed to send message", e);
            return false;
        }
    }

    public void sendMessageToAllChats(String message) {
        Set<String> chats = dataService.getAllChatIds();
        chats.forEach(chatId -> sendMessage(chatId, message));
    }
}
