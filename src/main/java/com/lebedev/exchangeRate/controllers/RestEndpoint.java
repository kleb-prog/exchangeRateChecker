package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.Status;
import com.lebedev.exchangeRate.service.ScheduleRetestChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(RestEndpoint.class);

    @Autowired
    private ScheduleRetestChecker ratesChecker;

    @GetMapping("api/v1/rates/usd/rub")
    public Status checkUsdToRubRate() {
        String rubRate = ratesChecker.checkUSDToRUBRate();
        logger.info("USD to RUB rate requested {}", rubRate);
        return new Status(rubRate != null ? "success" : "error", rubRate);
    }
}
