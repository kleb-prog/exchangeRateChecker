package com.lebedev.exchangeRate.service.telegramBot;

import com.lebedev.exchangeRate.repository.StoredDataService;
import com.lebedev.exchangeRate.service.CurrencyRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Service
public class CurrentCurrencyCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(CurrentCurrencyCommandHandler.class);

    public static final String CHOOSE_CURRENCY_STATE_USD = "chooseCurrencyStateUsd";

    private final StoredDataService storedDataService;
    private final TelegramMessageService messageService;
    private final CurrencyRatesService currencyRatesService;


    public CurrentCurrencyCommandHandler(StoredDataService storedDataService,
                                         TelegramMessageService messageService,
                                         CurrencyRatesService currencyRatesService) {
        this.storedDataService = storedDataService;
        this.messageService = messageService;
        this.currencyRatesService = currencyRatesService;
    }

    public void askToChooseCurrency(String chatId) {
        String message = "Please tell me which currency you would like to see, enter 3 letter code (e.g. EUR)";
        if (messageService.sendMessage(chatId, message)) {
            storedDataService.setChatState(chatId, CHOOSE_CURRENCY_STATE_USD);
            logger.info("Chat with id {} are ask to show current currency", chatId);
        }
    }

    public void answerWithCurrentCurrency(String chatId, Update update) {
        if (update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.length() == 3) {
                String currency = text.toUpperCase(Locale.ROOT);
                String usdRate = currencyRatesService.getUsdRate(currency);
                if (usdRate != null) {
                    String message = String.format("Rate for USD to %s is %s", currency, usdRate);
                    if (messageService.sendMessage(chatId, message)) {
                        storedDataService.removeChatState(chatId);
                        logger.info("Currency rate of {} for chat id {} answered", currency, chatId);
                    }
                } else {
                    String message = String.format("I couldn't find a currency %s", currency);
                    messageService.sendMessage(chatId, message);
                    storedDataService.removeChatState(chatId);
                    logger.info("Currency rate of {} for chat id {} can not be found", currency, chatId);
                }
            } else {
                String message = String.format("Incorrect currency %s", text);
                messageService.sendMessage(chatId, message);
                storedDataService.removeChatState(chatId);
                logger.info("User of chat id {} typed wrong value", chatId);
            }
        }
    }
}
