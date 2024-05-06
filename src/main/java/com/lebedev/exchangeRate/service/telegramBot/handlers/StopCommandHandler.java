package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.repository.ChatStorageRepository;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StopCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(StopCommandHandler.class);

    private static final String STOP_COMMAND = "/stop";

    private final ChatStorageRepository chatStorageRepository;
    private final TelegramMessageService messageService;

    public StopCommandHandler(ChatStorageRepository chatStorageRepository, TelegramMessageService messageService) {
        this.chatStorageRepository = chatStorageRepository;
        this.messageService = messageService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        if (!update.getMessage().hasText()) {
            return null;
        }
        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        if (STOP_COMMAND.equals(text)) {
            return () -> sendAnswer(chatId);
        }

        return null;
    }

    private void sendAnswer(String chatId) {
        chatStorageRepository.removeChatId(chatId);
        messageService.sendMessage(chatId, "Ok, I will not bother you.");
        logger.info("Chat removed {}", chatId);
    }
}
