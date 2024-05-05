package com.lebedev.exchangeRate.service.exchangeProvider;

import com.lebedev.exchangeRate.configuration.ApplicationConfiguration;
import com.lebedev.exchangeRate.repository.RatesCacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRequestService.class);

    private final RatesCacheRepository ratesCache;
    private final RestTemplate restTemplate;
    private final String exchangeApiURLTemplate;
    private final String exchangeApiKey;

    public ExchangeRequestService(RatesCacheRepository ratesCache, ApplicationConfiguration configuration) {
        this.ratesCache = ratesCache;
        restTemplate = new RestTemplate();
        exchangeApiURLTemplate = configuration.getExchangeApiURLTemplate();
        exchangeApiKey = configuration.getExchangeApiKey();
    }

    public String getRatesJson(String base, String target) {
        String cachedRate = ratesCache.getCacheRate(base, target);
        if (cachedRate != null) {
            return cachedRate;
        }
        if (exchangeApiKey == null) {
            logger.error("API key is not provided");
            return null;
        }
        try {
            String formattedUrl = String.format(exchangeApiURLTemplate, exchangeApiKey, base, target);
            String result = restTemplate.getForObject(formattedUrl, String.class);
            logger.info("Request completed {}", formattedUrl);

            ratesCache.setCacheRate(base, target, result);
            return result;
        } catch (RestClientException e) {
            logger.error("Failed to execute request", e);
            return null;
        }
    }
}
