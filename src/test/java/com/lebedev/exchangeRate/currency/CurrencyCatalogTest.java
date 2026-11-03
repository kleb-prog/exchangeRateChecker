package com.lebedev.exchangeRate.currency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyCatalogTest {

    @Test
    void loadsSupportedCurrencyCodesFromResource() {
        assertTrue(CurrencyCatalog.supports("USD"));
        assertTrue(CurrencyCatalog.supports("EUR"));
        assertTrue(CurrencyCatalog.supports("clf"));
        assertTrue(CurrencyCatalog.supports("cnh"));
        assertFalse(CurrencyCatalog.supports("KPW"));
        assertFalse(CurrencyCatalog.supports(null));
    }
}
