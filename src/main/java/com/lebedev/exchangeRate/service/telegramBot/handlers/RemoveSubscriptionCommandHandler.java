package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.service.SubscriptionService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.lebedev.exchangeRate.service.telegramBot.handlers.InfoSubscriptionCommandHandler.SEPARATOR;
import static com.lebedev.exchangeRate.util.TelegramHandlerUtil.createClearInlineButtonsMarkup;

public class RemoveSubscriptionCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(RemoveSubscriptionCommandHandler.class);

    public static final String REMOVE_SUBSCRIPTION_COMMAND = "/removesubscription";

    private final TelegramMessageService messageService;
    private final SubscriptionService subscriptionService;

    public RemoveSubscriptionCommandHandler(TelegramMessageService messageService,
                                            SubscriptionService subscriptionService) {
        this.messageService = messageService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        if (!update.hasCallbackQuery()) {
            return null;
        }

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        if (!data.startsWith(REMOVE_SUBSCRIPTION_COMMAND)) {
            return null;
        }

        String[] params = data.split(SEPARATOR);
        if (params.length != 2) {
            return null;
        }

        String exchangePairId = params[1];
        return () -> answerWithRemovingResult(callbackQuery, exchangePairId);
    }

    private void answerWithRemovingResult(CallbackQuery callbackQuery, String exchangePairId) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        try {
            removeSubscription(exchangePairId, chatId);
            messageService.sendEditMessageReplyMarkup(createClearInlineButtonsMarkup(chatId, messageId));
            messageService.sendMessage(chatId.toString(), "Subscription removed");
            logger.info("Exchange Pair subscription with id {} is removed for chat {}", exchangePairId, chatId);
        } catch (Exception e) {
            String message = "Something went wrong, please try again";
            messageService.sendMessage(chatId.toString(), message);
            logger.error("Failed to remove subscription", e);
        }
    }

    private void removeSubscription(String exchangePairId, Long chatId) {
        Chat chat = subscriptionService.findChat(chatId).orElseThrow();
        ChatExchangePair chatExchangePair = chat.getChatExchangePairs().stream()
                .filter(cep -> cep.getExchangePair().getPairId().equals(Long.parseLong(exchangePairId)))
                .findFirst()
                .orElseThrow();
        subscriptionService.removeChatExchangePairById(chatExchangePair.getId());
    }
}
