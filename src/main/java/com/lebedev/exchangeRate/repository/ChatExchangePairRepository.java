package com.lebedev.exchangeRate.repository;

import com.lebedev.exchangeRate.entity.ChatExchangePair;
import com.lebedev.exchangeRate.entity.ChatExchangePairId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatExchangePairRepository extends JpaRepository<ChatExchangePair, ChatExchangePairId> {

    @Query("SELECT cep FROM ChatExchangePair cep " +
            "JOIN FETCH cep.chat " +
            "JOIN FETCH cep.exchangePair")
    List<ChatExchangePair> findAllWithDetails();
}

