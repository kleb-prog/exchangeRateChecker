package com.lebedev.exchangeRate.service;

import com.lebedev.exchangeRate.repository.OldRatesRepository;
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
    private final OldRatesRepository oldRatesRepository;

    public ScheduleWorkService(TelegramBotService botService,
                               ExchangeRatesService currencyService,
                               OldRatesRepository oldRatesRepository) {
        this.botService = botService;
        this.currencyService = currencyService;
        this.oldRatesRepository = oldRatesRepository;
    }

    @Scheduled(cron = "@hourly")
    public void checkUsdToRubCurrencyChanges() {
        String usdToRubRate = currencyService.getExchangeRate(USD_CURRENCY, RUB_CURRENCY);
        logger.info("Scheduled request fired, USD to RUB {}", usdToRubRate);

        if (isUsdRateChanged(usdToRubRate)) {
            botService.sendExchangeRateChanged(String.format("New rate for USD to RUB is %s", usdToRubRate));
            oldRatesRepository.saveExchangeRate(USD_CURRENCY, RUB_CURRENCY, usdToRubRate);
        }
    }

    private boolean isUsdRateChanged(String currentRate) {
        String previousRate = oldRatesRepository.getOldChangeRate(USD_CURRENCY, ExchangeRatesService.RUB_CURRENCY);
        if (previousRate == null) {
            return true;
        }

        return !previousRate.equals(currentRate);
    }
}
