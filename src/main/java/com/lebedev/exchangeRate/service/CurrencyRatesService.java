package com.lebedev.exchangeRate.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.lebedev.exchangeRate.configuration.SpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyRatesService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRatesService.class);

    public static final String USD_CURRENCY = "USD";
    public static final String RUB_CURRENCY = "RUB";

    private final String exchangeApiURLTemplate;
    private final String exchangeApiKey;

    public CurrencyRatesService(SpringConfiguration configuration) {
        exchangeApiURLTemplate = configuration.getExchangeApiURLTemplate();
        exchangeApiKey = configuration.getExchangeApiKey();
    }

    @Nullable
    public String getUsdRate(String requiredCurrency) {
        String ratesJson = getRatesJson();
        if (ratesJson == null) {
            logger.debug("Rates response is null");
            return null;
        }

        try {
            JsonObject json = JsonParser.parseString(ratesJson).getAsJsonObject();

            String result = json.get("result").getAsString();
            if (!"success".equals(result)) {
                logger.debug("Rates response has error");
                return null;
            }

            JsonPrimitive rateForCurrency = json.get("conversion_rates")
                    .getAsJsonObject()
                    .getAsJsonPrimitive(requiredCurrency);
            return rateForCurrency != null ? rateForCurrency.getAsString() : null;

        } catch (JsonSyntaxException e) {
            logger.error("Failed to parse json", e);
        }

        return null;
    }

    private String getRatesJson() {
        try {
            if (exchangeApiKey == null) {
                logger.error("You should provide your API key in the private.properties file");
                return null;
            }
            RestTemplate restTemplate = new RestTemplate();
            String formattedUrl = String.format(exchangeApiURLTemplate, exchangeApiKey, USD_CURRENCY);
            String result = restTemplate.getForObject(formattedUrl, String.class);
            logger.info("Request sent to the end point {}", formattedUrl);
            return result;
        } catch (RestClientException e) {
            logger.error("Failed to get rates", e);
            return null;
        }
    }
}
