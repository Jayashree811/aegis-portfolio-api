package com.portfolio.api.repository;

import com.portfolio.api.entity.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface DividendRepository extends JpaRepository<Dividend, UUID> {
    List<Dividend> findByPortfolioId(UUID portfolioId);
}
