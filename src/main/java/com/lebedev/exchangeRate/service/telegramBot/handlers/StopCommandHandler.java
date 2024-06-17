package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.service.SubscriptionService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StopCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(StopCommandHandler.class);

    private static final String STOP_COMMAND = "/stop";

    private final SubscriptionService subscriptionService;
    private final TelegramMessageService messageService;

    public StopCommandHandler(SubscriptionService subscriptionService, TelegramMessageService messageService) {
        this.subscriptionService = subscriptionService;
        this.messageService = messageService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }
        String text = update.getMessage().getText();

        if (STOP_COMMAND.equals(text)) {
            return () -> sendAnswer(update);
        }

        return null;
    }

    private void sendAnswer(Update update) {
        Long chatId = update.getMessage().getChatId();
        subscriptionService.findChat(chatId)
                .ifPresent(subscriptionService::deleteChat);
        messageService.sendMessage(chatId.toString(), "Ok, I will not bother you.");
        logger.info("Chat removed {}", chatId);
    }
}
