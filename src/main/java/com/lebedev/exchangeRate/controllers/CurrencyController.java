package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.Status;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    ExchangeRatesService currencyService;

    public CurrencyController(ExchangeRatesService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("api/v1/ratesForPair")
    public Status getExchangeRateForPair(@RequestParam String base, @RequestParam String target) {
        String exchangeRate = currencyService.getExchangeRate(base, target);
        logger.info("{} to {} rate requested {}", base, target, exchangeRate);
        return new Status(exchangeRate != null ? "success" : "error", exchangeRate);
    }
}
