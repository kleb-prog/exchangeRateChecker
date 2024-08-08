package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.ChatDTO;
import com.lebedev.exchangeRate.service.ChatService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//Todo remove
@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chats")
    public ResponseEntity<?> getChatList() {
        List<ChatDTO> chats = chatService.getChatLIst().stream()
                .map(chat -> {
                    ChatDTO chatDTO = new ChatDTO();
                    BeanUtils.copyProperties(chat, chatDTO);
                    return chatDTO;
                }).toList();
        return ResponseEntity.ok().body(chats);
    }
}
