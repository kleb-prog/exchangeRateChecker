package com.lebedev.exchangeRate.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ApplicationStartup {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @Value("${application.version}")
    private String appVersion;

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationVersion() {
        logger.info("Application version: {}", appVersion);
    }
}

