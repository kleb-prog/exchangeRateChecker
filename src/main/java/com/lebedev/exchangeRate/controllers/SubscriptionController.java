package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.dto.AddExchangePairRequest;
import com.lebedev.exchangeRate.dto.ChatDTO;
import com.lebedev.exchangeRate.dto.ChatExchangePairDTO;
import com.lebedev.exchangeRate.dto.CreateChatRequest;
import com.lebedev.exchangeRate.dto.CreateExchangePairRequest;
import com.lebedev.exchangeRate.dto.ExchangePairDTO;
import com.lebedev.exchangeRate.dto.ExchangePairSubscriptionsDTO;
import com.lebedev.exchangeRate.mapper.ChatMapper;
import com.lebedev.exchangeRate.mapper.SubscriptionMapper;
import com.lebedev.exchangeRate.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final ChatMapper chatMapper;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionController(SubscriptionService subscriptionService, ChatMapper chatMapper,
                                   SubscriptionMapper subscriptionMapper) {
        this.subscriptionService = subscriptionService;
        this.chatMapper = chatMapper;
        this.subscriptionMapper = subscriptionMapper;
    }

    @PostMapping("/chats")
    public ChatDTO createChat(@Valid @RequestBody CreateChatRequest request) {
        return chatMapper.toDto(subscriptionService.createChat(request.id(), request.firstName(), request.lastName()));
    }

    @PostMapping("/exchangePairs")
    public ExchangePairDTO createExchangePair(@Valid @RequestBody CreateExchangePairRequest request) {
        return subscriptionMapper.toDto(
                subscriptionService.createExchangePair(request.baseCurrency(), request.targetCurrency()));
    }

    @GetMapping("/exchangePairs")
    public List<ExchangePairDTO> getExchangePairs() {
        return subscriptionService.getExchangePairs().stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }

    @PostMapping("/chats/{chatId}/exchangePairs/{pairId}")
    public ChatExchangePairDTO addExchangePairToChat(@PathVariable Long chatId,
                                                     @PathVariable Long pairId,
                                                     @Valid @RequestBody AddExchangePairRequest request) {
        return subscriptionMapper.toDto(
                subscriptionService.addExchangePairToChat(chatId, pairId, request.threshold()));
    }

    @GetMapping("/exchangePairs/withChatsGrouped")
    public List<ExchangePairSubscriptionsDTO> getAllExchangePairsWithChatsGrouped() {
        return subscriptionService.getAllExchangePairsWithChatsGrouped().entrySet().stream()
                .map(entry -> new ExchangePairSubscriptionsDTO(
                        subscriptionMapper.toDto(entry.getKey()),
                        entry.getValue().stream().map(subscriptionMapper::toDto).toList()))
                .toList();
    }
}
