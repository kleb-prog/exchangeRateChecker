package com.lebedev.exchangeRate;

import com.lebedev.exchangeRate.exception.LoggingUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExchangeRateApplication {

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
		SpringApplication.run(ExchangeRateApplication.class, args);
	}

}
