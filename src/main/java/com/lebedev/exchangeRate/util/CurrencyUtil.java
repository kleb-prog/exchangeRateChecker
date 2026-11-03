package com.lebedev.exchangeRate.util;

import com.lebedev.exchangeRate.currency.CurrencyCatalog;
import java.util.Currency;
import java.util.Locale;

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

        if (CurrencyCatalog.supports(currency.getCurrencyCode())) {
            return currency;
        } else {
            throw new RuntimeException(String.format("Currency code %s is not supported", currencyCode));
        }
    }
}
