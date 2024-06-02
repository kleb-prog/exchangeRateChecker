package com.lebedev.exchangeRate.service;

import com.lebedev.exchangeRate.repository.HistoryRatesRepository;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService.RUB_CURRENCY;
import static com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService.USD_CURRENCY;

@Service
public class ScheduleWorkService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleWorkService.class);

    private final TelegramBotService botService;
    private final ExchangeRatesService currencyService;
    private final HistoryRatesRepository historyRatesRepository;

    public ScheduleWorkService(TelegramBotService botService,
                               ExchangeRatesService currencyService,
                               HistoryRatesRepository historyRatesRepository) {
        this.botService = botService;
        this.currencyService = currencyService;
        this.historyRatesRepository = historyRatesRepository;
    }

    @Scheduled(cron = "@hourly")
    public void checkUsdToRubCurrencyChanges() {
        String usdToRubRate = currencyService.getExchangeRate(USD_CURRENCY, RUB_CURRENCY);
        logger.info("Scheduled request fired, USD to RUB {}", usdToRubRate);

        int compared = compareWithPreviousRate(usdToRubRate);
        if (compared != 0) {
            String status = getRateStatus(compared);
            botService.sendExchangeRateChanged(String.format("New rate for USD to RUB is %s. " +
                    "Exchange rate is %s", usdToRubRate, status));
        }
        historyRatesRepository.saveExchangeRate(USD_CURRENCY, RUB_CURRENCY, usdToRubRate);
    }

    private int compareWithPreviousRate(String currentRate) {
        String previousRate = historyRatesRepository.getPreviousChangeRate(USD_CURRENCY, ExchangeRatesService.RUB_CURRENCY);
        if (previousRate == null) {
            return 0;
        }

        if (!previousRate.equals(currentRate)) {
            Double prev = Double.parseDouble(previousRate);
            Double cur = Double.parseDouble(currentRate);
            return prev.compareTo(cur);
        }

        return 0;
    }

    private String getRateStatus(int compared) {
        if (compared < 0) {
            return "rising \uD83D\uDCC8";
        } else if (compared > 0) {
            return "going down \uD83D\uDCC9";
        } else {
            return "same";
        }
    }
}
