package com.lebedev.exchangeRate.service;

import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageBroadcastService {

    private final ChatService chatService;
    private final TelegramMessageService messageService;

    public void sendToAll(String message) {
        chatService.getChatList()
                .forEach(chat -> messageService.sendMessage(chat.getChatId().toString(), message));
    }
}
