package com.lebedev.exchangeRate.service.exchangeProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ExchangeRatesService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRatesService.class);

    public static final String USD_CURRENCY = "USD";
    public static final String RUB_CURRENCY = "RUB";
    public static final Set<String> SUPPORTED_CURRENCIES = Set.of("AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN", "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTN", "BWP", "BYN", "BZD", "CAD", "CDF", "CHF", "CLP", "CNY", "COP", "CRC", "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR", "FJD", "FKP", "FOK", "GBP", "GEL", "GGP", "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS", "IMP", "INR", "IQD", "IRR", "ISK", "JEP", "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KID", "KMF", "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LYD", "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP", "MRU", "MUR", "MVR", "MWK", "MXN", "MYR", "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG", "SEK", "SGD", "SHP", "SLE", "SOS", "SRD", "SSP", "STN", "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP", "TRY", "TTD", "TVD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VES", "VND", "VUV", "WST", "XAF", "XCD", "XDR", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL");

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
