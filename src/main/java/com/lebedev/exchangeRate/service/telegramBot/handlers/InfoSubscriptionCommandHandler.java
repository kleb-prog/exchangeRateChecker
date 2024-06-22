package com.lebedev.exchangeRate.service.telegramBot.handlers;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ExchangePair;
import com.lebedev.exchangeRate.service.SubscriptionService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateHandler;
import com.lebedev.exchangeRate.service.telegramBot.api.UpdateReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import static com.lebedev.exchangeRate.service.telegramBot.handlers.RemoveSubscriptionCommandHandler.REMOVE_SUBSCRIPTION_COMMAND;
import static com.lebedev.exchangeRate.util.TelegramHandlerUtil.createClearInlineButtonsMarkup;

public class InfoSubscriptionCommandHandler implements UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(InfoSubscriptionCommandHandler.class);

    public static final String INFO_COMMAND = "/infosubscription";
    public static final String SEPARATOR = ";";

    private final TelegramMessageService messageService;
    private final SubscriptionService subscriptionService;

    public InfoSubscriptionCommandHandler(TelegramMessageService messageService,
                                          SubscriptionService subscriptionService) {
        this.messageService = messageService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public UpdateReaction handle(Update update) {
        if (!update.hasCallbackQuery()) {
            return null;
        }

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        if (!data.startsWith(INFO_COMMAND)) {
            return null;
        }

        String[] params = data.split(SEPARATOR);
        if (params.length != 2) {
            return null;
        }
        String exchangePairId = params[1];
        return () -> answerWithSubscriptionInfo(callbackQuery, exchangePairId);
    }

    private void answerWithSubscriptionInfo(CallbackQuery callbackQuery, String exchangePairId) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        try {
            SendMessage messageWithButton = getInfoMessageWithButton(chatId, exchangePairId);
            messageService.sendEditMessageReplyMarkup(createClearInlineButtonsMarkup(chatId, messageId));
            messageService.sendCustomMessage(messageWithButton);
            logger.info("Reply to chat {} with subscription information, exchangePairId is {}", chatId, exchangePairId);
        } catch (Exception e) {
            String message = "Something went wrong, please try again";
            messageService.sendMessage(chatId.toString(), message);
            logger.error("Subscription creation failed", e);
        }
    }

    private SendMessage getInfoMessageWithButton(Long chatId, String exchangePairId) {
        InlineKeyboardRow row = new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Remove this subscription")
                .callbackData(REMOVE_SUBSCRIPTION_COMMAND + SEPARATOR + exchangePairId)
                .build());
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboardRow(row).build();

        return SendMessage.builder()
                .chatId(chatId)
                .text(getInfoMessageText(chatId, exchangePairId))
                .replyMarkup(inlineKeyboardMarkup)
                .parseMode(ParseMode.HTML)
                .build();
    }

    private String getInfoMessageText(Long chatId, String exchangePairId) {
        Chat chat = subscriptionService.findChat(chatId).orElseThrow();

        long pairId = Long.parseLong(exchangePairId);
        ChatExchangePair chatExchangePair = chat.getChatExchangePairs().stream()
                .filter(cep -> cep.getExchangePair().getPairId().equals(pairId))
                .findFirst()
                .orElseThrow();
        ExchangePair exchangePair = chatExchangePair.getExchangePair();

        String baseCurrency = exchangePair.getBaseCurrency().getDisplayName(Locale.ENGLISH);
        String targetCurrency = exchangePair.getTargetCurrency().getDisplayName(Locale.ENGLISH);
        BigDecimal threshold = chatExchangePair.getThreshold();
        Date createdAt = chatExchangePair.getCreatedAt();

        return String.format("<b>Exchange Pair information:</b>\n" +
                "Base currency is <b>%s</b>\n" +
                "Target currency is <b>%s</b>\n" +
                "Delta limit is <b>%s</b>\n" +
                "Subscribed on <b>%tF %4$tR</b>", baseCurrency, targetCurrency, threshold, createdAt);
    }
}
