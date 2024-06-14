package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.repository.ChatStorageRepository;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Locale;

import static com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService.SUPPORTED_CURRENCIES;

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
        if (!update.getMessage().hasText()) {
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
                if (isAnswerContainsInvalidCurrencyCode(update)) {
                    return () -> answerOnInvalidInput(chatId);
                }
                saveAnswer(chatId, text);
                return () -> askToChooseTargetCurrency(chatId);
            }
            case CHOOSE_CURRENCY_STATE_TARGET -> {
                if (isAnswerContainsInvalidCurrencyCode(update)) {
                    return () -> answerOnInvalidInput(chatId);
                }
                return () -> answerWithCurrentExchangeRate(chatId, update);
            }
            default -> {
                return null;
            }
        }
    }

    private boolean isAnswerContainsInvalidCurrencyCode(Update update) {
        String text = update.getMessage().getText();
        return text.length() != 3 || !SUPPORTED_CURRENCIES.contains(text);
    }

    private void saveAnswer(String chatId, String text) {
        chatStorageRepository.setChatVariable(chatId, text);
    }

    private void askToChooseBaseCurrency(String chatId) {
        int supportedCurrencies = SUPPORTED_CURRENCIES.size();
        String message = "Please tell me which currency you would like to see, enter 3 letter code (e.g. EUR). " +
                "Supports " + supportedCurrencies + " currencies.";
        logger.info("Chat with id {} asks to show current exchange rate, requested base currency", chatId);
        if (messageService.sendCustomMessage(createMessageWithButtons(chatId, message))) {
            chatStorageRepository.setChatState(chatId, CHOOSE_CURRENCY_STATE_BASE);
            logChatState(CHOOSE_CURRENCY_STATE_BASE);
        }
    }

    private void askToChooseTargetCurrency(String chatId) {
        String message = "Please tell me the target currency you would like to see";
        logger.info("Chat with id {} answer with base currency, requested target currency", chatId);
        if (messageService.sendCustomMessage(createMessageWithButtons(chatId, message))) {
            chatStorageRepository.setChatState(chatId, CHOOSE_CURRENCY_STATE_TARGET);
            logChatState(CHOOSE_CURRENCY_STATE_TARGET);
        }
    }

    private void answerWithCurrentExchangeRate(String chatId, Update update) {
        String variable = chatStorageRepository.getChatVariable(chatId);
        String baseCurrency = variable.toUpperCase(Locale.ROOT);
        String text = update.getMessage().getText();
        String targetCurrency = text.toUpperCase(Locale.ROOT);
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

    private static SendMessage createMessageWithButtons(String chatId, String messageText) {
        KeyboardRow buttonsRow1 = new KeyboardRow("USD", "EUR", "GBP");
        KeyboardRow buttonsRow2 = new KeyboardRow("RUB", "JPY", "AUD");
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .keyboardRow(buttonsRow1)
                .keyboardRow(buttonsRow2)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(keyboardMarkup)
                .build();
    }

    private static SendMessage createMessageWithButtonsRemove(String chatId, String messageText) {
        ReplyKeyboardRemove replyKeyboardRemove = ReplyKeyboardRemove.builder().removeKeyboard(true).build();
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(replyKeyboardRemove)
                .build();
    }

    private void answerOnInvalidInput(String chatId) {
        String message = "You have entered the wrong currency code, please try again";
        messageService.sendMessage(chatId, message);
        logger.info("User of chat id {} typed wrong currency code", chatId);
    }

    private void logChatState(String state) {
        logger.info("Chat with id now has a new state : {}", state);
    }
}
