package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.Status;
import com.lebedev.exchangeRate.service.CurrencyRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lebedev.exchangeRate.service.CurrencyRatesService.RUB_CURRENCY;

@RestController
public class RestEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(RestEndpoint.class);

    CurrencyRatesService currencyService;

    public RestEndpoint(CurrencyRatesService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("api/v1/rates/usd/rub")
    public Status checkUsdToRubRate() {
        String rubRate = currencyService.getUsdRate(RUB_CURRENCY);
        logger.info("USD to RUB rate requested {}", rubRate);
        return new Status(rubRate != null ? "success" : "error", rubRate);
    }
}
