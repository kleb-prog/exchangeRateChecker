package com.lebedev.exchangeRate.service.telegramBot;

import com.lebedev.exchangeRate.configuration.SpringConfiguration;
import com.lebedev.exchangeRate.repository.StoredDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.lebedev.exchangeRate.service.telegramBot.CurrentCurrencyCommandHandler.CHOOSE_CURRENCY_STATE_USD;

@Service
public class TelegramBotService implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    private final SpringConfiguration configuration;
    private final StoredDataService dataService;
    private final CurrentCurrencyCommandHandler currencyCommandHandler;
    private final TelegramMessageService messageService;

    public TelegramBotService(SpringConfiguration configuration,
                              StoredDataService dataService,
                              CurrentCurrencyCommandHandler currencyCommandHandler,
                              TelegramMessageService messageService) {
        this.configuration = configuration;
        this.dataService = dataService;
        this.currencyCommandHandler = currencyCommandHandler;
        this.messageService = messageService;
    }

    @Override
    public String getBotToken() {
        return configuration.getTelegramToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            String chatState = dataService.getChatState(chatId);
            if (chatState != null) {
                if (chatState.equals(CHOOSE_CURRENCY_STATE_USD)) {
                    currencyCommandHandler.answerWithCurrentCurrency(chatId, update);
                    return;
                }
            }

            if (text.startsWith("/start")) {
                dataService.saveChatId(chatId);
                messageService.sendMessage(chatId, "Hi, I will let you know when the currency is changed! For now it's USD to RUB.");
            } else if (text.startsWith("/instantcurrencyusd")) {
                currencyCommandHandler.askToChooseCurrency(chatId);
            } else if (text.startsWith("/stop")) {
                dataService.removeChatId(chatId);
                messageService.sendMessage(chatId, "Ok, I will not bother you.");
            } else {
                messageService.sendMessage(chatId, "I can't do that. Please choose a command from the menu");
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        logger.info("Registered bot running state is: {}", botSession.isRunning());
    }
}
