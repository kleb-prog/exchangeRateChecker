package com.lebedev.exchangeRate;

import com.lebedev.exchangeRate.entity.Chat;
import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ChatExchangePairId;
import com.lebedev.exchangeRate.entity.ExchangePair;
import com.lebedev.exchangeRate.repository.HistoryRatesRepository;
import com.lebedev.exchangeRate.service.ScheduleCheckerService;
import com.lebedev.exchangeRate.service.SubscriptionService;
import com.lebedev.exchangeRate.service.exchangeProvider.ExchangeRatesService;
import com.lebedev.exchangeRate.service.telegramBot.TelegramMessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleCheckerServiceTest {

    @InjectMocks
    private ScheduleCheckerService scheduleCheckerService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ExchangeRatesService exchangeRatesService;
    @Mock
    private HistoryRatesRepository historyRatesRepository;
    @Mock
    private TelegramMessageService messageService;

    @Test
    void testCheckCurrencyChangesHourlyWhenRateIsRising() {
        // Prepare
        String currencyRateNew = "0.9333";
        String currencyRatePrevious = "0.9222";
        prepareServices(currencyRateNew, currencyRatePrevious);
        // Execute
        scheduleCheckerService.checkCurrencyChangesHourly();
        // Verify
        ArgumentCaptor<String> chatIdArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageArg = ArgumentCaptor.forClass(String.class);

        verify(messageService, times(1))
                .sendMessage(chatIdArg.capture(), messageArg.capture());
        Assertions.assertEquals("1", chatIdArg.getValue());
        Assertions.assertEquals("New rate for USD to EUR is 0.9333. Exchange rate is rising \uD83D\uDCC8", messageArg.getValue());
    }

    @Test
    void testCheckCurrencyChangesHourlyWhenRateIsGoingDown() {
        // Prepare
        String currencyRateNew = "0.9111";
        String currencyRatePrevious = "0.9444";
        prepareServices(currencyRateNew, currencyRatePrevious);
        // Execute
        scheduleCheckerService.checkCurrencyChangesHourly();
        // Verify
        ArgumentCaptor<String> chatIdArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageArg = ArgumentCaptor.forClass(String.class);

        verify(messageService, times(1))
                .sendMessage(chatIdArg.capture(), messageArg.capture());
        Assertions.assertEquals("1", chatIdArg.getValue());
        Assertions.assertEquals("New rate for USD to EUR is 0.9111. Exchange rate is going down \uD83D\uDCC9", messageArg.getValue());
    }

    @Test
    void testCheckCurrencyChangesHourlyWhenRateIsSame() {
        // Prepare
        String currencyRateNew = "0.9000";
        String currencyRatePrevious = "0.9000";
        prepareServices(currencyRateNew, currencyRatePrevious);
        // Execute
        scheduleCheckerService.checkCurrencyChangesHourly();
        // Verify
        verify(messageService, times(0))
                .sendMessage(anyString(), anyString());
    }

    private void prepareServices(String currencyRateNew, String currencyRatePrevious) {
        when(subscriptionService.getAllExchangePairsWithChatsGrouped())
                .thenReturn(createChatExchangePair());
        when(exchangeRatesService.getExchangeRate(anyString(), anyString()))
                .thenReturn(currencyRateNew);
        when(historyRatesRepository.getPreviousChangeRate(anyString(), anyString()))
                .thenReturn(currencyRatePrevious);
    }

    private Map<ExchangePair, List<ChatExchangePair>> createChatExchangePair() {
        Chat chat = new Chat();
        chat.setChatId(1L);
        chat.setFirstName("TestName");
        chat.setLastName("TestLastName");
        chat.setCreatedAt(new Date());

        ExchangePair exchangePair = new ExchangePair();
        exchangePair.setPairId(1L);
        exchangePair.setBaseCurrency("USD");
        exchangePair.setTargetCurrency("EUR");
        exchangePair.setCreatedAt(new Date());

        ChatExchangePair chatExchangePair = new ChatExchangePair();
        chatExchangePair.setId(new ChatExchangePairId(chat.getChatId(), exchangePair.getPairId()));
        chatExchangePair.setChat(chat);
        chatExchangePair.setExchangePair(exchangePair);
        chatExchangePair.setThreshold(BigDecimal.valueOf(0D));
        chatExchangePair.setCreatedAt(new Date());

        chat.setChatExchangePairs(Collections.singleton(chatExchangePair));
        exchangePair.setChatExchangePairs(Collections.singleton(chatExchangePair));

        Map<ExchangePair, List<ChatExchangePair>> result = new HashMap<>();
        result.put(exchangePair, Collections.singletonList(chatExchangePair));
        return result;
    }
}
