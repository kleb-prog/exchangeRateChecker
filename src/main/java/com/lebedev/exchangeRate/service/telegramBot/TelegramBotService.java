package com.lebedev.exchangeRate.service.telegramBot;

import com.lebedev.exchangeRate.configuration.SpringConfiguration;
import com.lebedev.exchangeRate.service.StoredDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

@Service
public class TelegramBotService implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    private final SpringConfiguration configuration;
    private final StoredDataService dataService;
    private final TelegramClient telegramClient;

    public TelegramBotService(SpringConfiguration configuration, StoredDataService dataService) {
        this.configuration = configuration;
        this.dataService = dataService;
        telegramClient = new OkHttpTelegramClient(getBotToken());
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

            if ("/start".equals(text)) {
                dataService.saveChatId(chatId);
                sendMessage(chatId, "Hi, I will let you know when the currency is changed! For now it's USD to RUB.");
            } else if ("/stop".equals(text)) {
                dataService.removeChatId(chatId);
                sendMessage(chatId, "Ok, I will not bother you.");
            }
        }
    }

    public void sendMessage(String chatId, String message) {
        SendMessage messageObject = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
        try {
            telegramClient.execute(messageObject);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message", e);
        }
    }

    public void sendMessageToAllChats(String message) {
        Set<String> chats = dataService.getAllChatIds();
        chats.forEach(chatId -> sendMessage(chatId, message));
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        logger.info("Registered bot running state is: {}", botSession.isRunning());
    }
}
