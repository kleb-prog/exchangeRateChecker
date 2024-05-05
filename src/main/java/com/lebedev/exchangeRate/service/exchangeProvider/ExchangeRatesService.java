package com.lebedev.exchangeRate.service.exchangeProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRatesService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRatesService.class);

    public static final String USD_CURRENCY = "USD";
    public static final String RUB_CURRENCY = "RUB";

    private final ExchangeRequestService exchangeRequestService;

    public ExchangeRatesService(ExchangeRequestService exchangeRequestService) {
        this.exchangeRequestService = exchangeRequestService;
    }

    public String getExchangeRate(String base, String target) {
        String usdRatesJson = exchangeRequestService.getRatesJson(base, target);
        if (usdRatesJson == null) {
            logger.debug("Rates response is null for {} to {}", base, target);
            return null;
        }

        try {
            JsonObject json = JsonParser.parseString(usdRatesJson).getAsJsonObject();
            String result = json.get("result").getAsString();
            if ("error".equals(result)) {
                logger.debug("Rates response has error {}", json.get("error-type").getAsString());
                return null;
            }

            return json.get("conversion_rate").getAsString();
        } catch (JsonSyntaxException e) {
            logger.error("Failed to parse json", e);
        }

        return null;
    }
}
