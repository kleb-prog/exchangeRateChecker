package com.lebedev.exchangeRate.repository;

import com.lebedev.exchangeRate.entity.ExchangePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.Optional;

@Repository
public interface ExchangePairRepository extends JpaRepository<ExchangePair, Long> {

    Optional<ExchangePair> findByBaseCurrencyAndTargetCurrency(Currency baseCurrency, Currency targetCurrency);
}
