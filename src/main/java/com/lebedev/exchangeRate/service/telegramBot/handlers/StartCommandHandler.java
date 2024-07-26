package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.service.SubscriptionService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.lebedev.exchangeRate.service.telegramBot.handlers.SubscriptionsCommandHandler.SUBSCRIPTIONS_COMMAND;

public class StartCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(StartCommandHandler.class);

    private static final String START_COMMAND = "/start";

    private final SubscriptionService subscriptionService;
    private final TelegramMessageService messageService;

    public StartCommandHandler(SubscriptionService subscriptionService,
                               TelegramMessageService messageService) {
        this.messageService = messageService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }
        String text = update.getMessage().getText();

        if (START_COMMAND.equals(text)) {
            return () -> sendAnswer(update);
        }

        return null;
    }

    private void sendAnswer(Update update) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getChat().getFirstName();
        String lastName = update.getMessage().getChat().getLastName();
        if (subscriptionService.findChat(chatId).isEmpty()) {
            subscriptionService.createChat(chatId, firstName != null ? firstName : "", lastName != null ? lastName : "");
        }

        messageService.sendMessage(chatId.toString(), String
                .format("Hi, I will let you know when the currency exchange rate changes! " +
                "Use the %s command to subscribe", SUBSCRIPTIONS_COMMAND));
        logger.info("New chat added {}", chatId);
    }
}
