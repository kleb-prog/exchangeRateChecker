package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.repository.ChatStorageRepository;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import com.lebedev.exchangeRate.util.CurrencyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService.SUPPORTED_CURRENCIES;
import static com.lebedev.exchangeRate.util.TelegramHandlerUtil.createMessageWithButtons;
import static com.lebedev.exchangeRate.util.TelegramHandlerUtil.createMessageWithButtonsRemove;

public class InstantCurrencyCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(InstantCurrencyCommandHandler.class);

    private static final String CURRENCY_COMMAND = "/instantcurrency";
    public static final String CHOOSE_CURRENCY_STATE_BASE = "chooseCurrencyStateBase";
    public static final String CHOOSE_CURRENCY_STATE_TARGET = "chooseCurrencyStateTarget";

    private final ChatStorageRepository chatStorageRepository;
    private final TelegramMessageService messageService;
    private final ExchangeRatesService exchangeRatesService;


    public InstantCurrencyCommandHandler(ChatStorageRepository chatStorageRepository,
                                         TelegramMessageService messageService,
                                         ExchangeRatesService exchangeRatesService) {
        this.chatStorageRepository = chatStorageRepository;
        this.messageService = messageService;
        this.exchangeRatesService = exchangeRatesService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }

        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        if (CURRENCY_COMMAND.equals(text)) {
            return () -> askToChooseBaseCurrency(chatId);
        }

        String chatState = chatStorageRepository.getChatState(chatId);
        if (chatState == null) {
            return null;
        }

        switch (chatState) {
            case CHOOSE_CURRENCY_STATE_BASE -> {
                if (isAnswerContainsInvalidCurrencyCode(text)) {
                    return () -> answerOnInvalidInput(chatId);
                }
                saveAnswer(chatId, text);
                return () -> askToChooseTargetCurrency(chatId);
            }
            case CHOOSE_CURRENCY_STATE_TARGET -> {
                if (isAnswerContainsInvalidCurrencyCode(text)) {
                    return () -> answerOnInvalidInput(chatId);
                }
                return () -> answerWithCurrentExchangeRate(chatId, text);
            }
            default -> {
                return null;
            }
        }
    }

    private boolean isAnswerContainsInvalidCurrencyCode(String text) {
        try {
            CurrencyUtil.createCurrencyByCode(text);
            return false;
        } catch (Exception ignore) {
            return true;
        }
    }

    private void saveAnswer(String chatId, String text) {
        chatStorageRepository.setChatVariable(chatId, text);
    }

    private void askToChooseBaseCurrency(String chatId) {
        int supportedCurrencies = SUPPORTED_CURRENCIES.size();
        String message = String.format("Please tell me which currency you would like to see, enter 3 letter code (e.g. EUR). " +
                "Supports %s currencies.", supportedCurrencies);
        logger.info("Chat with id {} asks to show current exchange rate, requested base currency", chatId);
        if (messageService.sendCustomMessage(createMessageWithButtons(chatId, message))) {
            chatStorageRepository.setChatState(chatId, CHOOSE_CURRENCY_STATE_BASE);
            logChatState(chatId, CHOOSE_CURRENCY_STATE_BASE);
        }
    }

    private void askToChooseTargetCurrency(String chatId) {
        String message = "Please tell me the target currency you would like to see";
        logger.info("Chat with id {} answer with base currency, requested target currency", chatId);
        if (messageService.sendCustomMessage(createMessageWithButtons(chatId, message))) {
            chatStorageRepository.setChatState(chatId, CHOOSE_CURRENCY_STATE_TARGET);
            logChatState(chatId, CHOOSE_CURRENCY_STATE_TARGET);
        }
    }

    private void answerWithCurrentExchangeRate(String chatId, String text) {
        String variable = chatStorageRepository.getChatVariable(chatId);
        String baseCurrency = CurrencyUtil.createCurrencyByCode(variable).getCurrencyCode();
        String targetCurrency = CurrencyUtil.createCurrencyByCode(text).getCurrencyCode();
        String usdRate = exchangeRatesService.getExchangeRate(baseCurrency, targetCurrency);
        if (usdRate != null) {
            String message = String.format("Rate for %s to %s is %s", baseCurrency, targetCurrency, usdRate);
            if (messageService.sendCustomMessage(createMessageWithButtonsRemove(chatId, message))) {
                chatStorageRepository.removeChatState(chatId);
                chatStorageRepository.removeChatVariable(chatId);
                logger.info("Exchange rate for {} to {} for chat id {} answered", baseCurrency, targetCurrency, chatId);
            }
        } else {
            String message = String.format("I couldn't find an exchange rate for %s to %s", baseCurrency, targetCurrency);
            messageService.sendCustomMessage(createMessageWithButtonsRemove(chatId, message));
            chatStorageRepository.removeChatState(chatId);
            chatStorageRepository.removeChatVariable(chatId);
            logger.info("Exchange rate for {} to {} for chat id {} can not be found", baseCurrency, targetCurrency, chatId);
        }
    }

    private void answerOnInvalidInput(String chatId) {
        String message = "You have entered the wrong currency code, please try again";
        messageService.sendMessage(chatId, message);
        logger.info("User of chat id {} typed wrong currency code", chatId);
    }

    private void logChatState(String chatId, String state) {
        logger.info("Chat with id {} now has a new state : {}", chatId, state);
    }
}
