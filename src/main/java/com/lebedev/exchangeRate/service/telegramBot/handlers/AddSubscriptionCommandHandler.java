package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ExchangePair;
import com.lebedev.exchangeRate.repository.ChatStorageRepository;
import com.lebedev.exchangeRate.service.SubscriptionService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import com.lebedev.exchangeRate.util.CurrencyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

import static com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService.SUPPORTED_CURRENCIES;
import static com.lebedev.exchangeRate.util.TelegramHandlerUtil.*;

public class AddSubscriptionCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(AddSubscriptionCommandHandler.class);

    public static final String ADD_SUBSCRIPTION_COMMAND = "/addsubscription";
    public static final String ADD_SUBSCRIPTION_CURRENCY_STATE_BASE = "addSubscriptionCurrencyStateBase";
    public static final String ADD_SUBSCRIPTION_CURRENCY_STATE_TARGET = "addSubscriptionCurrencyStateTarget";
    public static final String ADD_SUBSCRIPTION_CURRENCY_STATE_THRESHOLD = "addSubscriptionCurrencyStateThreshold";
    private static final String VARIABLES_SEPARATOR = ";";

    private final ChatStorageRepository chatStorageRepository;
    private final TelegramMessageService messageService;
    private final SubscriptionService subscriptionService;

    public AddSubscriptionCommandHandler(ChatStorageRepository chatStorageRepository,
                                         TelegramMessageService messageService,
                                         SubscriptionService subscriptionService) {
        this.chatStorageRepository = chatStorageRepository;
        this.messageService = messageService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (ADD_SUBSCRIPTION_COMMAND.equals(data)) {
                Long chatId = callbackQuery.getMessage().getChatId();
                Integer messageId = callbackQuery.getMessage().getMessageId();
                return () -> askToChooseBaseCurrency(chatId, messageId);
            } else {
                return null;
            }
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }

        Long chatId = update.getMessage().getChatId();
        String chatState = chatStorageRepository.getChatState(chatId.toString());
        if (chatState == null) {
            return null;
        }
        String text = update.getMessage().getText();

        switch (chatState) {
            case ADD_SUBSCRIPTION_CURRENCY_STATE_BASE -> {
                if (isAnswerContainsInvalidCurrencyCode(text)) {
                    return () -> answerOnInvalidCurrency(chatId.toString());
                }
                chatStorageRepository.setChatVariable(chatId.toString(), text);
                return () -> askToChooseTargetCurrency(chatId.toString());
            }
            case ADD_SUBSCRIPTION_CURRENCY_STATE_TARGET -> {
                if (isAnswerContainsInvalidCurrencyCode(text)) {
                    return () -> answerOnInvalidCurrency(chatId.toString());
                }
                String baseCurrency = chatStorageRepository.getChatVariable(chatId.toString());
                chatStorageRepository.setChatVariable(chatId.toString(), baseCurrency + VARIABLES_SEPARATOR + text);
                if (isExchangePairAlreadySubscribed(chatId, baseCurrency, text)) {
                    return () -> answerOnAlreadySubscribed(chatId);
                }
                return () -> askToChooseThreshold(chatId.toString());
            }
            case ADD_SUBSCRIPTION_CURRENCY_STATE_THRESHOLD -> {
                if (isAnswerContainsInvalidThreshold(text)) {
                    return () -> answerOnInvalidThreshold(chatId.toString());
                }
                return () -> answerWithSaveResult(chatId, text);
            }
            default -> {
                return null;
            }
        }
    }

    private void askToChooseBaseCurrency(Long chatId, Integer messageId) {
        int supportedCurrencies = SUPPORTED_CURRENCIES.size();
        String message = String.format("Please select base currency, %s currencies supported", supportedCurrencies);
        logger.info("Chat with id {} asking to create new subscription, requested base currency", chatId);
        messageService.sendEditMessageReplyMarkup(createClearInlineButtonsMarkup(chatId, messageId));
        if (messageService.sendCustomMessage(createMessageWithButtons(chatId.toString(), message))) {
            chatStorageRepository.setChatState(chatId.toString(), ADD_SUBSCRIPTION_CURRENCY_STATE_BASE);
            logChatState(chatId.toString(), ADD_SUBSCRIPTION_CURRENCY_STATE_BASE);
        }
    }

    private void askToChooseTargetCurrency(String chatId) {
        String message = "Please select target currency";
        logger.info("Chat with id {} answer with base currency, requested target currency", chatId);
        if (messageService.sendCustomMessage(createMessageWithButtons(chatId, message))) {
            chatStorageRepository.setChatState(chatId, ADD_SUBSCRIPTION_CURRENCY_STATE_TARGET);
            logChatState(chatId, ADD_SUBSCRIPTION_CURRENCY_STATE_TARGET);
        }
    }

    private void askToChooseThreshold(String chatId) {
        String message = "Please select the currency change delta limit below which you will not receive notifications. " +
                "Zero means you want to see every change";
        logger.info("Chat with id {} answer with target currency, requested threshold", chatId);
        if (messageService.sendMessage(chatId, message)) {
            chatStorageRepository.setChatState(chatId, ADD_SUBSCRIPTION_CURRENCY_STATE_THRESHOLD);
            logChatState(chatId, ADD_SUBSCRIPTION_CURRENCY_STATE_THRESHOLD);
        }
    }

    private void answerWithSaveResult(Long chatId, String text) {
        try {
            String[] splitVariable = chatStorageRepository.getChatVariable(chatId.toString()).split(VARIABLES_SEPARATOR);
            Currency baseCurrency = CurrencyUtil.createCurrencyByCode(splitVariable[0]);
            Currency targetCurrency = CurrencyUtil.createCurrencyByCode(splitVariable[1]);

            ExchangePair exchangePair = subscriptionService.findExchangePairByBaseAndTargetCurrency(baseCurrency, targetCurrency)
                    .orElseGet(() -> subscriptionService.createExchangePair(baseCurrency.getCurrencyCode(), targetCurrency.getCurrencyCode()));

            subscriptionService.addExchangePairToChat(chatId, exchangePair.getPairId(), BigDecimal.valueOf(Double.parseDouble(text)));

            String message = "Subscription created";
            messageService.sendCustomMessage(createMessageWithButtonsRemove(chatId.toString(), message));
            logger.info("Subscription created for chat {} and pair {}", chatId, exchangePair.getPairId());
        } catch (Exception e) {
            String message = "Something went wrong, please try again";
            messageService.sendCustomMessage(createMessageWithButtonsRemove(chatId.toString(), message));
            logger.error("Subscription creation failed", e);
        }
        chatStorageRepository.removeChatState(chatId.toString());
        chatStorageRepository.removeChatVariable(chatId.toString());
    }

    private boolean isExchangePairAlreadySubscribed(Long chatId, String baseCurrencyStr, String targetCurrencyStr) {
        Currency baseCurrency = CurrencyUtil.createCurrencyByCode(baseCurrencyStr);
        Currency targetCurrency = CurrencyUtil.createCurrencyByCode(targetCurrencyStr);

        Optional<ExchangePair> exchangePair = subscriptionService.findExchangePairByBaseAndTargetCurrency(baseCurrency, targetCurrency);
        if (exchangePair.isEmpty()) {
            return false;
        }

        Chat chat = subscriptionService.findChat(chatId).orElseThrow();
        Optional<ChatExchangePair> chatExchangePair = chat.getChatExchangePairs().stream()
                .filter(cep -> cep.getExchangePair().getPairId().equals(exchangePair.get().getPairId()))
                .findFirst();

        return chatExchangePair.isPresent();
    }

    private boolean isAnswerContainsInvalidCurrencyCode(String text) {
        try {
            CurrencyUtil.createCurrencyByCode(text);
            return false;
        } catch (Exception ignore) {
            return true;
        }
    }

    private boolean isAnswerContainsInvalidThreshold(String text) {
        try {
            Double.parseDouble(text);
            return false;
        } catch (NumberFormatException ignore) {
            return true;
        }
    }

    private void answerOnInvalidCurrency(String chatId) {
        String message = "You have entered the wrong currency code, please try again";
        messageService.sendMessage(chatId, message);
        logger.info("User of chat id {} typed wrong currency code", chatId);
    }

    private void answerOnAlreadySubscribed(Long chatId) {
        String message = "This currency exchange pair is already subscribed";
        messageService.sendCustomMessage(createMessageWithButtonsRemove(chatId.toString(), message));
        chatStorageRepository.removeChatState(chatId.toString());
        logger.info("User of chat id {} attempt to subscribe to already subscribed exchange pair", chatId);
    }

    private void answerOnInvalidThreshold(String chatId) {
        String message = "You have entered the wrong limit value, please try again";
        messageService.sendMessage(chatId, message);
        logger.info("User of chat id {} typed wrong threshold value", chatId);
    }

    private void logChatState(String chatId, String state) {
        logger.info("Chat with id {} now has a new state : {}", chatId, state);
    }
}
