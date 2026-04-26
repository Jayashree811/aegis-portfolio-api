package com.portfolio.api.repository;

import com.portfolio.api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByPortfolioId(UUID portfolioId);
}
