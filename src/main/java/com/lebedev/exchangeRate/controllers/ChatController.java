package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.ChatDTO;
import com.lebedev.exchangeRate.dto.ChatDetailsDTO;
import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ExchangePair;
import com.lebedev.exchangeRate.service.ChatService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chats")
    public ResponseEntity<?> getChatList() {
        List<ChatDTO> chats = chatService.getChatList().stream()
                .map(chat -> {
                    ChatDTO chatDTO = new ChatDTO();
                    BeanUtils.copyProperties(chat, chatDTO);
                    return chatDTO;
                }).toList();
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/user-exchange-pairs/{chatId}")
    public ResponseEntity<?> getChatDetails(@PathVariable Long chatId) {
        Optional<Chat> chatOptional = chatService.getChatWithExchangePairs(chatId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            List<ChatDetailsDTO> details = chat.getChatExchangePairs().stream()
                    .map(cep -> {
                        ExchangePair pair = cep.getExchangePair();
                        return new ChatDetailsDTO(
                                pair.getBaseCurrency().getDisplayName(Locale.ENGLISH),
                                pair.getTargetCurrency().getDisplayName(Locale.ENGLISH),
                                cep.getThreshold(),
                                cep.getCreatedAt());
                    }).toList();
            return ResponseEntity.ok(details);
        }
        return ResponseEntity.badRequest().body("Chat is not found");
    }
}
