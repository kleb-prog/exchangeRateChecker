package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.Message;
import com.lebedev.exchangeRate.service.MessageBroadcastService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageBroadcastService messageBroadcastService;

    public MessageController(MessageBroadcastService messageBroadcastService) {
        this.messageBroadcastService = messageBroadcastService;
    }

    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@Valid @RequestBody Message message) {
        messageBroadcastService.sendToAll(message.message());
        return ResponseEntity.ok("Sent successfully");
    }
}
