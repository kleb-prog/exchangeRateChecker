package com.lebedev.exchangeRate.repository;

import com.lebedev.exchangeRate.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c " +
            "LEFT JOIN FETCH c.chatExchangePairs cep " +
            "LEFT JOIN FETCH cep.exchangePair " +
            "WHERE c.chatId = :chatId")
    Optional<Chat> findChatByIdWithDetails(@Param("chatId") Long chatId);
}
