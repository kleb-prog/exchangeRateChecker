package com.lebedev.exchangeRate.service.exchangeProvider;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ExchangeRateApiClient {

    @GetExchange("/v6/{apiKey}/pair/{base}/{target}")
    ExchangeRateResponse getPair(@PathVariable String apiKey,
                                 @PathVariable String base,
                                 @PathVariable String target);
}
