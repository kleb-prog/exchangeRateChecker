package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DefaultHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHandler.class);

    private final TelegramMessageService messageService;

    public DefaultHandler(TelegramMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        return () -> sendMessage(update);
    }

    private void sendMessage(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        messageService.sendMessage(chatId, "I can't understand you. Please choose a command from the menu");
        logger.warn("Default handler triggered for chat {}", chatId);
    }
}
