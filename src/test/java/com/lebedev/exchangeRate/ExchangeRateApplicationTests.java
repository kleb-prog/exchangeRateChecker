package com.lebedev.exchangeRate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.longpolling.starter.TelegramBotInitializer;

@SpringBootTest
class ExchangeRateApplicationTests {

	@MockBean
	private TelegramBotInitializer telegramBotInitializer;

	@Test
	void contextLoads() {
	}

}
