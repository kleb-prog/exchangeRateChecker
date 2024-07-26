package com.lebedev.exchangeRate.util;

import java.util.Currency;
import java.util.Locale;

import static com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService.SUPPORTED_CURRENCIES;

public final class CurrencyUtil {

    private CurrencyUtil() {
    }

    /**
     * Creates and validates currency object
     */
    public static Currency createCurrencyByCode(String currencyCode) {
        Currency currency;
        try {
            currency = Currency.getInstance(currencyCode.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Currency code %s is not valid", currencyCode), e);
        }

        if (SUPPORTED_CURRENCIES.contains(currency.getCurrencyCode())) {
            return currency;
        } else {
            throw new RuntimeException(String.format("Currency code %s is not supported", currencyCode));
        }
    }
}
