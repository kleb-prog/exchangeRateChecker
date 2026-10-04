package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.Status;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class CurrencyController {

    private final ExchangeRatesService currencyService;

    @GetMapping("/ratesForPair")
    public Status getExchangeRateForPair(@RequestParam String base, @RequestParam String target) {
        String exchangeRate = currencyService.getExchangeRate(base, target);
        log.info("{} to {} rate requested {}", base, target, exchangeRate);
        return new Status(exchangeRate != null ? "success" : "error", exchangeRate);
    }
}
