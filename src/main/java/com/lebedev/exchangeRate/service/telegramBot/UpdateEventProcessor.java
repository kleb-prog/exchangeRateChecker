package com.lebedev.exchangeRate.service.telegramBot;

import com.lebedev.exchangeRate.repository.ChatStorageRepository;
import com.lebedev.exchangeRate.service.SubscriptionService;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import com.lebedev.exchangeRate.service.telegramBot.api.EventProcessor;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import com.lebedev.exchangeRate.service.telegramBot.handlers.*;
import com.lebedev.exchangeRate.util.TelegramHandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UpdateEventProcessor implements EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UpdateEventProcessor.class);

    private final List<UpdateHandler> handlers;
    private final UpdateHandler defaultHandler;

    private final ChatStorageRepository chatStorageRepository;
    private final TelegramMessageService messageService;
    private final ExchangeRatesService exchangeRatesService;
    private final SubscriptionService subscriptionService;

    public UpdateEventProcessor(ChatStorageRepository chatStorageRepository,
                                TelegramMessageService messageService,
                                ExchangeRatesService exchangeRatesService,
                                SubscriptionService subscriptionService) {
        this.chatStorageRepository = chatStorageRepository;
        this.messageService = messageService;
        this.exchangeRatesService = exchangeRatesService;
        this.subscriptionService = subscriptionService;
        this.handlers = buildHandlerList();
        this.defaultHandler = new DefaultHandler(messageService);
    }

    /**
     * All handlers will process the update but only one handler should recognise update and then return reaction,
     * all others should return null
     *
     * @param update object from telegram api with update information
     */
    @Override
    public void process(Update update) {
        UpdateReaction updateReaction = handlers.stream()
                .map(handler -> handler.handle(update))
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), reactions -> {
                    if (reactions.size() != 1) {
                        logInvalidReactionQuantity(reactions.size(), update);
                        return !reactions.isEmpty() ? reactions.get(0) : defaultHandler.handle(update);
                    }
                    return reactions.get(0);
                }));

        updateReaction.execute();
    }

    private static void logInvalidReactionQuantity(int reactionsCount, Update update) {
        logger.warn("Result of update processing is not expected, number of reactions is: {} for chat id: {}",
                reactionsCount, TelegramHandlerUtil.findChatId(update));
    }

    private List<UpdateHandler> buildHandlerList() {
        List<UpdateHandler> handlerList = new ArrayList<>();

        handlerList.add(new StartCommandHandler(subscriptionService, messageService));
        handlerList.add(new SubscriptionsCommandHandler(chatStorageRepository, messageService, subscriptionService));
        handlerList.add(new InfoSubscriptionCommandHandler(messageService, subscriptionService));
        handlerList.add(new AddSubscriptionCommandHandler(chatStorageRepository, messageService, subscriptionService));
        handlerList.add(new RemoveSubscriptionCommandHandler(messageService, subscriptionService));
        handlerList.add(new InstantCurrencyCommandHandler(chatStorageRepository, messageService, exchangeRatesService));
        handlerList.add(new StopCommandHandler(subscriptionService, messageService));

        return handlerList;
    }
}
