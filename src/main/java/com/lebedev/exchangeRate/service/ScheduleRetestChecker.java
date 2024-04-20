package com.lebedev.exchangeRate.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ScheduleRetestChecker {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleRetestChecker.class);

    private static final String USD_CURRENCY = "USD";
    private static final String RUB_CURRENCY = "RUB";

    @Value("${exchangeApiURLTemplate}")
    private String exchangeApiURLTemplate;

    @Value("${exchangeApiKey}")
    private String exchangeApiKey;

    @Scheduled(cron = "@hourly")
    public void setExchangeApiURLScheduled() {
        String rubRate = checkUSDToRUBRate();
        logger.info("Scheduled request fired USD to RUB {}", rubRate);
    }


    @Nullable
    public String checkUSDToRUBRate() {
        String ratesJson = getRatesJson();
        if (ratesJson != null) {
            try {
                JsonObject json = JsonParser.parseString(ratesJson).getAsJsonObject();

                String result = json.get("result").getAsString();
                if ("success".equals(result)) {
                    return json.get("conversion_rates").getAsJsonObject()
                            .getAsJsonPrimitive(RUB_CURRENCY)
                            .getAsString();
                } else {
                    logger.debug("Rates response has error");
                }
            } catch (JsonSyntaxException e) {
                logger.error("Failed to parse json", e);
            }
        } else {
            logger.debug("Rates response is null");
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
            return restTemplate.getForObject(formattedUrl, String.class);
        } catch (RestClientException e) {
            logger.error("Failed to get rates", e);
            return null;
        }
    }
}
