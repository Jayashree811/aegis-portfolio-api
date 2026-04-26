package com.portfolio.api.repository;

import com.portfolio.api.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding, UUID> {
    Optional<Holding> findByPortfolioIdAndSymbol(UUID portfolioId, String symbol);
    List<Holding> findByPortfolioId(UUID portfolioId);
}
