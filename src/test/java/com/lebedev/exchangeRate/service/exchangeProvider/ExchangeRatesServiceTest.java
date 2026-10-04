package com.lebedev.exchangeRate.service.exchangeProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRatesServiceTest {

    @Mock
    private ExchangeRequestService exchangeRequestService;

    @Test
    void returnsConversionRateFromSuccessfulResponse() {
        when(exchangeRequestService.getRate("EUR", "GBP"))
                .thenReturn(new ExchangeRateResponse(
                        "success", null, null, null, null, null, null,
                        "EUR", "GBP", new BigDecimal("0.8412"), null));

        ExchangeRatesService service = new ExchangeRatesService(exchangeRequestService);

        assertEquals("0.8412", service.getExchangeRate("EUR", "GBP"));
    }

    @Test
    void returnsNullForErrorResponse() {
        when(exchangeRequestService.getRate("EUR", "GBP"))
                .thenReturn(new ExchangeRateResponse(
                        "error", null, null, null, null, null, null,
                        null, null, null, "invalid-key"));

        ExchangeRatesService service = new ExchangeRatesService(exchangeRequestService);

        assertNull(service.getExchangeRate("EUR", "GBP"));
    }

    @Test
    void returnsNullWhenResponseIsNull() {
        when(exchangeRequestService.getRate("EUR", "GBP")).thenReturn(null);

        ExchangeRatesService service = new ExchangeRatesService(exchangeRequestService);

        assertNull(service.getExchangeRate("EUR", "GBP"));
    }

    @Test
    void returnsNullWhenSuccessfulResponseHasNoConversionRate() {
        when(exchangeRequestService.getRate("EUR", "GBP"))
                .thenReturn(new ExchangeRateResponse(
                        "success", null, null, null, null, null, null,
                        "EUR", "GBP", null, null));

        ExchangeRatesService service = new ExchangeRatesService(exchangeRequestService);

        assertNull(service.getExchangeRate("EUR", "GBP"));
    }
}
