package com.lebedev.exchangeRate.service.telegramBot.api;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface EventProcessor {
    void process(Update update);
}
