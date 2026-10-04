package com.lebedev.exchangeRate;

import com.lebedev.exchangeRate.exception.LoggingUncaughtExceptionHandler;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRateApiClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication
@EnableScheduling
@ImportHttpServices(group = "exchange-rate-api", types = ExchangeRateApiClient.class)
public class ExchangeRateApplication {

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
		SpringApplication.run(ExchangeRateApplication.class, args);
	}

}
