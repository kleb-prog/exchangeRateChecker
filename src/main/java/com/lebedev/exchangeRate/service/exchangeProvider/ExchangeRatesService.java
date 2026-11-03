package com.lebedev.exchangeRate.service.exchangeProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeRatesService {

    private final ExchangeRequestService exchangeRequestService;

    public String getExchangeRate(String base, String target) {
        ExchangeRateResponse response = exchangeRequestService.getRate(base, target);
        if (response == null) {
            log.debug("Rates response is null for {} to {}", base, target);
            return null;
        }

        if (!"success".equals(response.result()) || response.conversionRate() == null) {
            log.debug("Rates response has error {}", response.errorType());
            return null;
        }

        return response.conversionRate().toPlainString();
    }
}
