package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ExchangePair;
import com.lebedev.exchangeRate.repository.ChatStorageRepository;
import com.lebedev.exchangeRate.service.SubscriptionService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.*;

import static com.lebedev.exchangeRate.service.telegramBot.handlers.AddSubscriptionCommandHandler.ADD_SUBSCRIPTION_COMMAND;
import static com.lebedev.exchangeRate.service.telegramBot.handlers.InfoSubscriptionCommandHandler.INFO_COMMAND;
import static com.lebedev.exchangeRate.service.telegramBot.handlers.InfoSubscriptionCommandHandler.SEPARATOR;

public class SubscriptionsCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionsCommandHandler.class);

    public static final String SUBSCRIPTIONS_COMMAND = "/subscriptions";

    private final ChatStorageRepository chatStorageRepository;
    private final TelegramMessageService messageService;
    private final SubscriptionService subscriptionService;

    public SubscriptionsCommandHandler(ChatStorageRepository chatStorageRepository,
                                       TelegramMessageService messageService, 
                                       SubscriptionService subscriptionService) {
        this.chatStorageRepository = chatStorageRepository;
        this.messageService = messageService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (SUBSCRIPTIONS_COMMAND.equals(text)) {
                Long chatId = update.getMessage().getChatId();
                Set<ChatExchangePair> pairs = getChatExchangePairs(chatId, update);
                return () -> showSubscriptionMenu(chatId.toString(), pairs);
            }
        }

        return null;
    }

    private void showSubscriptionMenu(String chatId, Set<ChatExchangePair> pairs) {
        SendMessage subscriptionList = createSubscriptionButtonsList(chatId, pairs);
        logger.info("Chat with id {} answered with subscription options menu", chatId);
        if (messageService.sendCustomMessage(subscriptionList)) {
            // Clear the old state of the chat
            chatStorageRepository.removeChatState(chatId);
        }
    }

    private Set<ChatExchangePair> getChatExchangePairs(Long chatId, Update update) {
        Optional<Chat> chatOptional = subscriptionService.findChat(chatId);
        Chat chatObject = chatOptional.orElseGet(() -> {
            String firstName = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();
            return subscriptionService
                    .createChat(chatId, firstName != null ? firstName : "", lastName != null ? lastName : "");
        });
        Set<ChatExchangePair> chatExchangePairs = chatObject.getChatExchangePairs();
        return chatExchangePairs != null ? chatExchangePairs : Collections.emptySet();
    }

    private SendMessage createSubscriptionButtonsList(String chatId, Set<ChatExchangePair> pairs) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (ChatExchangePair chatExchangePair : pairs) {
            ExchangePair exchangePair = chatExchangePair.getExchangePair();
            InlineKeyboardRow row = new InlineKeyboardRow(InlineKeyboardButton.builder()
                    .text(String.format("%s to %s", exchangePair.getBaseCurrency(), exchangePair.getTargetCurrency()))
                    .callbackData(INFO_COMMAND + SEPARATOR + exchangePair.getPairId().toString())
                    .build());
            rows.add(row);
        }
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Add subscription")
                .callbackData(ADD_SUBSCRIPTION_COMMAND)
                .build()));

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
        return SendMessage.builder()
                .chatId(chatId)
                .text(String.format("You have %s subscriptions", pairs.size()))
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }
}
