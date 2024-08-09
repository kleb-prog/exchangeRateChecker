package com.lebedev.exchangeRate.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lebedev.exchangeRate.dto.MessageDTO;
import com.lebedev.exchangeRate.service.ChatService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MessageController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int MAX_MESSAGE_LENGTH = 4096;

    private final TelegramMessageService messageService;
    private final ChatService chatService;

    public MessageController(TelegramMessageService messageService, ChatService chatService) {
        this.messageService = messageService;
        this.chatService = chatService;
    }

    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody String message) {
        String messageToSend;
        try {
            MessageDTO messageDTO = new Gson().fromJson(message, MessageDTO.class);
            messageToSend = messageDTO.getMessage();
        } catch (JsonSyntaxException e) {
            logger.error("Error while parsing message", e);
            return ResponseEntity.badRequest().body("Message is invalid");
        }

        if (messageToSend.length() > MAX_MESSAGE_LENGTH) {
            return ResponseEntity.badRequest().body("Message is too long. Current maximum length is 4096 UTF8 characters");
        } else if (messageToSend.isBlank()) {
            return ResponseEntity.badRequest().body("Message is empty");
        }

        try {
            chatService.getChatLIst()
                    .forEach(chat -> messageService.sendMessage(chat.getChatId().toString(), messageToSend));
            return ResponseEntity.ok("Sent successfully");
        } catch (Throwable e) {
            logger.error("Error while sending message", e);
            return ResponseEntity.internalServerError().body("Send failed");
        }
    }
}
