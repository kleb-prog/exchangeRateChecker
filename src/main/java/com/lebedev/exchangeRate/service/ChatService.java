package com.lebedev.exchangeRate.service;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<Chat> getChatLIst() {
        return chatRepository.findAll();
    }
}
