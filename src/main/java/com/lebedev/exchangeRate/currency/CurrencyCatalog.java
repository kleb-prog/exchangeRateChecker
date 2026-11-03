package com.lebedev.exchangeRate.currency;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Catalog of currency codes supported by the ExchangeRate-API provider.
 *
 * @see <a href="https://www.exchangerate-api.com/docs/supported-currencies">
 * ExchangeRate-API supported currencies</a>
 */
public final class CurrencyCatalog {

    private static final String CURRENCIES_RESOURCE = "currencies/exchange-rate-api.json";
    private static final Set<String> SUPPORTED_CURRENCIES = loadCurrencies();

    private CurrencyCatalog() {
    }

    public static Set<String> supportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }

    public static boolean supports(String currencyCode) {
        return currencyCode != null
                && SUPPORTED_CURRENCIES.contains(currencyCode.toUpperCase(Locale.ROOT));
    }

    private static Set<String> loadCurrencies() {
        try (InputStream inputStream = new ClassPathResource(CURRENCIES_RESOURCE).getInputStream()) {
            var currencies = new ObjectMapper().readValue(inputStream, new TypeReference<Set<String>>() {
            });
            return Collections.unmodifiableSet(new LinkedHashSet<>(currencies));
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Unable to load supported currencies from " + CURRENCIES_RESOURCE);
        }
    }
}
