package com.lebedev.exchangeRate.service.telegramBot;

import com.lebedev.exchangeRate.configuration.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramBotService implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    private final ApplicationConfiguration configuration;
    private final UpdateEventProcessor updateEventProcessor;

    public TelegramBotService(ApplicationConfiguration configuration,
                              UpdateEventProcessor updateEventProcessor) {
        this.configuration = configuration;
        this.updateEventProcessor = updateEventProcessor;
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
        updateEventProcessor.process(update);
    }

    public void sendExchangeRateChanged(String message) {
       updateEventProcessor.notifyAllChats(message);
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        logger.info("Registered bot running state is: {}", botSession.isRunning());
    }
}
