package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ExchangePair;
import com.lebedev.exchangeRate.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final SubscriptionService clientService;

    // TODO return DTO
    public SubscriptionController(SubscriptionService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/chats")
    public Chat createChat(@RequestParam Long id, @RequestParam String firstName, @RequestParam String lastName) {
        return clientService.createChat(id, firstName, lastName);
    }

    @PostMapping("/exchangePairs")
    public ExchangePair createExchangePair(@RequestParam String baseCurrency, @RequestParam String targetCurrency) {
        return clientService.createExchangePair(baseCurrency, targetCurrency);
    }

    @GetMapping("/exchangePairs")
    public List<ExchangePair> getExchangePair() {
        return clientService.getExchangePairs();
    }

    @PostMapping("/chats/{chatId}/exchangePairs/{pairId}")
    public ChatExchangePair addExchangePairToChat(@PathVariable Long chatId,
                                                  @PathVariable Long pairId,
                                                  @RequestParam BigDecimal threshold) {
        return clientService.addExchangePairToChat(chatId, pairId, threshold);
    }

    @GetMapping("/exchangePairs/withChatsGrouped")
    public Map<ExchangePair, List<ChatExchangePair>> getAllExchangePairsWithChatsGrouped() {
        return clientService.getAllExchangePairsWithChatsGrouped();
    }
}
