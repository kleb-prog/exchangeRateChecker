package com.lebedev.exchangeRate.service.telegramBot;

import com.lebedev.exchangeRate.repository.ChatStorageRepository;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import com.lebedev.exchangeRate.service.telegramBot.api.EventProcessor;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import com.lebedev.exchangeRate.service.telegramBot.handlers.InstantCurrencyCommandHandler;
import com.lebedev.exchangeRate.service.telegramBot.handlers.DefaultHandler;
import com.lebedev.exchangeRate.service.telegramBot.handlers.StartCommandHandler;
import com.lebedev.exchangeRate.service.telegramBot.handlers.StopCommandHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UpdateEventProcessor implements EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UpdateEventProcessor.class);

    private final List<UpdateHandler> handlers;

    private final ChatStorageRepository chatStorageRepository;
    private final TelegramMessageService messageService;
    private final ExchangeRatesService exchangeRatesService;

    public UpdateEventProcessor(ChatStorageRepository chatStorageRepository,
                                TelegramMessageService messageService,
                                ExchangeRatesService exchangeRatesService) {
        this.chatStorageRepository = chatStorageRepository;
        this.messageService = messageService;
        this.exchangeRatesService = exchangeRatesService;
        this.handlers = buildHandlerList();
    }

    @Override
    public void process(Update update) {
        List<UpdateReaction> updateReactions = handlers.stream()
                .map(handler -> handler.handle(update))
                .filter(Objects::nonNull)
                .toList();

        updateReactions.stream()
                .findFirst()
                .ifPresentOrElse(UpdateReaction::execute, logEmptyReaction(update));
    }

    private static @NotNull Runnable logEmptyReaction(Update update) {
        return () -> logger.warn("No proper reaction is found for chat {}", update.getMessage().getChatId());
    }

    @Override
    public void notifyAllChats(String message) {
        messageService.sendMessageBulk(chatStorageRepository.getAllChatIds(), message);
    }

    private List<UpdateHandler> buildHandlerList() {
        List<UpdateHandler> handlerList = new ArrayList<>();

        handlerList.add(new StartCommandHandler(chatStorageRepository, messageService));
        handlerList.add(new InstantCurrencyCommandHandler(chatStorageRepository, messageService, exchangeRatesService));
        handlerList.add(new StopCommandHandler(chatStorageRepository, messageService));
        // Should always be the last
        handlerList.add(new DefaultHandler(messageService));

        return handlerList;
    }
}
