package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.ChatDTO;
import com.lebedev.exchangeRate.dto.ChatDetailsDTO;
import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.mapper.ChatDetailsMapper;
import com.lebedev.exchangeRate.mapper.ChatMapper;
import com.lebedev.exchangeRate.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final ChatMapper chatMapper;
    private final ChatDetailsMapper chatDetailsMapper;

    public ChatController(ChatService chatService, ChatMapper chatMapper, ChatDetailsMapper chatDetailsMapper) {
        this.chatService = chatService;
        this.chatMapper = chatMapper;
        this.chatDetailsMapper = chatDetailsMapper;
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatDTO>> getChatList() {
        List<ChatDTO> chats = chatService.getChatList().stream()
                .map(chatMapper::toDto)
                .toList();
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/user-exchange-pairs/{chatId}")
    public ResponseEntity<List<ChatDetailsDTO>> getChatDetails(@PathVariable Long chatId) {
        Optional<Chat> chatOptional = chatService.getChatWithExchangePairs(chatId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            List<ChatDetailsDTO> details = chat.getChatExchangePairs().stream()
                    .map(chatDetailsMapper::toDto)
                    .toList();
            return ResponseEntity.ok(details);
        }
        return ResponseEntity.notFound().build();
    }
}
