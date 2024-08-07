package com.lebedev.exchangeRate.service.telegramBot;

import com.lebedev.exchangeRate.configuration.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TelegramMessageService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramMessageService.class);

    private final TelegramClient telegramClient;

    public TelegramMessageService(ApplicationConfiguration configuration) {
        this.telegramClient = new OkHttpTelegramClient(configuration.getTelegramToken());
    }

    public boolean sendMessage(String chatId, String message) {
       return sendMessageInternal(getMessage(chatId, message));
    }

    public boolean sendCustomMessage(SendMessage sendMessage) {
        return sendMessageInternal(sendMessage);
    }

    public boolean sendEditMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) {
        return sendMessageInternal(editMessageReplyMarkup);
    }

    private boolean sendMessageInternal(BotApiMethod<?> sendMessage) {
        try {
            telegramClient.execute(sendMessage);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send message", e);
            return false;
        }
    }

    private static SendMessage getMessage(String chatId, String message) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
    }

}
