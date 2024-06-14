package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.repository.ChatStorageRepository;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(StartCommandHandler.class);

    private static final String START_COMMAND = "/start";

    private final ChatStorageRepository chatStorageRepository;
    private final TelegramMessageService messageService;

    public StartCommandHandler(ChatStorageRepository chatStorageRepository, TelegramMessageService messageService) {
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

        if (START_COMMAND.equals(text)) {
            return () -> sendAnswer(chatId);
        }

        return null;
    }

    private void sendAnswer(String chatId) {
        chatStorageRepository.saveChatId(chatId);
        messageService.sendMessage(chatId, "Hi, I will let you know when the currency is changed! " +
                "For now it's USD to RUB.");
        logger.info("New chat added {}", chatId);
    }
}
