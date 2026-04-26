package com.portfolio.api.repository;

import com.portfolio.api.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
}
