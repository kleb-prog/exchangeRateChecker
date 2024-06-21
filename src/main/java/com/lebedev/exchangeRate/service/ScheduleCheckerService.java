package com.lebedev.exchangeRate.service;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ExchangePair;
import com.lebedev.exchangeRate.repository.HistoryRatesRepository;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleCheckerService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleCheckerService.class);

    private final TelegramMessageService messageService;
    private final ExchangeRatesService currencyService;
    private final HistoryRatesRepository historyRatesRepository;
    private final SubscriptionService subscriptionService;

    public ScheduleCheckerService(TelegramMessageService messageService,
                                  ExchangeRatesService currencyService,
                                  HistoryRatesRepository historyRatesRepository,
                                  SubscriptionService subscriptionService) {
        this.messageService = messageService;
        this.currencyService = currencyService;
        this.historyRatesRepository = historyRatesRepository;
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(cron = "${exchangeCheckCron}", zone = "${cronTimeZone}")
    public void checkCurrencyChangesHourly() {
        Map<ExchangePair, List<ChatExchangePair>> pairToChatsMap = subscriptionService.getAllExchangePairsWithChatsGrouped();
        for (Map.Entry<ExchangePair, List<ChatExchangePair>> pairToChatsEntity : pairToChatsMap.entrySet()) {
            try {
                checkCurrencyPair(pairToChatsEntity);
            } catch (Exception e) {
                logger.error(String.format("Failed to check exchange rate for %s to %s pair",
                        pairToChatsEntity.getKey().getBaseCurrency(), pairToChatsEntity.getKey().getTargetCurrency()), e);
            }
        }
    }

    private void checkCurrencyPair(Map.Entry<ExchangePair, List<ChatExchangePair>> pairToChatsEntity) {
        ExchangePair pair = pairToChatsEntity.getKey();
        String baseCurrency = pair.getBaseCurrency().getCurrencyCode();
        String targetCurrency = pair.getTargetCurrency().getCurrencyCode();

        String exchangeRate = currencyService.getExchangeRate(baseCurrency, targetCurrency);
        logger.info("Scheduled request fired, {} to {} : {}", baseCurrency, targetCurrency, exchangeRate);

        double delta = compareWithPreviousRate(baseCurrency, targetCurrency, exchangeRate);
        // Notify only when currency changes
        if (delta != 0) {
            String status = getRateStatus(delta);
            String message = String.format("New rate for %s to %s is %s. " +
                    "Exchange rate is %s", baseCurrency, targetCurrency, exchangeRate, status);
            notifySubscribers(pairToChatsEntity.getValue(), delta, message);
        }
        historyRatesRepository.saveExchangeRate(baseCurrency, targetCurrency, exchangeRate);
    }

    private void notifySubscribers(List<ChatExchangePair> chatExchangePairs, double delta, String message) {
        for (ChatExchangePair chatExchangePair : chatExchangePairs) {
            Chat chat = chatExchangePair.getChat();
            BigDecimal threshold = chatExchangePair.getThreshold();
            // Notify when change exceeds threshold
            if (BigDecimal.valueOf(delta).compareTo(threshold) >= 0) {
                messageService.sendMessage(chat.getChatId().toString(), message);
            }
        }
    }

    private double compareWithPreviousRate(String base, String target, String currentRate) {
        String previousRate = historyRatesRepository.getPreviousChangeRate(base, target);
        if (previousRate == null) {
            return 0;
        }

        if (!previousRate.equals(currentRate)) {
            Double prev = Double.parseDouble(previousRate);
            Double current = Double.parseDouble(currentRate);
            return prev - current;
        }

        return 0;
    }

    private String getRateStatus(double compared) {
        if (compared < 0) {
            return "rising \uD83D\uDCC8";
        } else if (compared > 0) {
            return "going down \uD83D\uDCC9";
        } else {
            return "same";
        }
    }
}
