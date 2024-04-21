package com.lebedev.exchangeRate.service;

import com.lebedev.exchangeRate.service.telegramBot.TelegramBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.lebedev.exchangeRate.service.CurrencyRatesService.RUB_CURRENCY;

@Service
public class ScheduleWorkService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleWorkService.class);

    private final TelegramBotService telegramBotService;
    private final CurrencyRatesService currencyService;
    private final StoredDataService dataService;

    public ScheduleWorkService(TelegramBotService telegramBotService,
                               CurrencyRatesService currencyService,
                               StoredDataService dataService) {
        this.telegramBotService = telegramBotService;
        this.currencyService = currencyService;
        this.dataService = dataService;
    }

    @Scheduled(cron = "@hourly")
    public void checkUsdToRubCurrencyChanges() {
        String rubRate = currencyService.getUsdRate(RUB_CURRENCY);
        logger.info("Scheduled request fired, USD to RUB {}", rubRate);

        if (isUsdRateChanged(RUB_CURRENCY, rubRate)) {
            telegramBotService.sendMessageToAllChats(String.format("New rate for USD to RUB is %s", rubRate));
            dataService.saveUsdRate(RUB_CURRENCY, rubRate);
        }
    }

    private boolean isUsdRateChanged(String currencyName, String currentRate) {
        String previousRate = dataService.getStoredUsdRate(currencyName);
        if (previousRate == null) {
            return true;
        }

        return !previousRate.equals(currentRate);
    }
}
