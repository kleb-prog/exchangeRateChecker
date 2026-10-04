package com.lebedev.exchangeRate.service.exchangeProvider;

import com.lebedev.exchangeRate.configuration.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class ExchangeRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRequestService.class);

    private final ExchangeRateApiClient exchangeRateApiClient;
    private final String exchangeApiKey;

    public ExchangeRequestService(ExchangeRateApiClient exchangeRateApiClient,
                                  ApplicationConfiguration configuration) {
        this.exchangeRateApiClient = exchangeRateApiClient;
        exchangeApiKey = configuration.getExchangeApiKey();
    }

    @Cacheable(
            cacheNames = "exchangeRates",
            key = "#base.toUpperCase(T(java.util.Locale).ROOT) + 'To' + #target.toUpperCase(T(java.util.Locale).ROOT)"
    )
    public ExchangeRateResponse getRate(String base, String target) {
        if (exchangeApiKey == null) {
            logger.error("API key is not provided");
            return null;
        }
        try {
            ExchangeRateResponse result = exchangeRateApiClient.getPair(exchangeApiKey, base, target);
            logger.info("Exchange rate request completed for {} to {}", base, target);
            return result;
        } catch (RestClientException e) {
            logger.error("Failed to execute request", e);
            return null;
        }
    }
}
