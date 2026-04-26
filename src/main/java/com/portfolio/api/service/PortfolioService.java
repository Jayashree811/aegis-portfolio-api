package com.portfolio.api.service;

import com.portfolio.api.dto.HoldingDto;
import com.portfolio.api.dto.PortfolioDto;
import com.portfolio.api.dto.TransactionRequest;
import com.portfolio.api.entity.Holding;
import com.portfolio.api.entity.Portfolio;
import com.portfolio.api.entity.Transaction;
import com.portfolio.api.entity.TransactionType;
import com.portfolio.api.repository.HoldingRepository;
import com.portfolio.api.repository.PortfolioRepository;
import com.portfolio.api.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public PortfolioDto createPortfolio(PortfolioDto dto) {
        Portfolio p = Portfolio.builder()
                .clientName(dto.getClientName())
                .riskProfile(dto.getRiskProfile())
                .cashBalance(dto.getCashBalance() == null ? BigDecimal.ZERO : dto.getCashBalance())
                .build();
        p = portfolioRepository.save(p);
        dto.setId(p.getId());
        dto.setCashBalance(p.getCashBalance());
        return dto;
    }

    public PortfolioDto getPortfolio(UUID id) {
        Portfolio p = portfolioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id " + id));
        PortfolioDto dto = new PortfolioDto();
        dto.setId(p.getId());
        dto.setClientName(p.getClientName());
        dto.setRiskProfile(p.getRiskProfile());
        dto.setCashBalance(p.getCashBalance());
        return dto;
    }

    @Transactional
    public void buy(UUID portfolioId, TransactionRequest req) {
        Portfolio p = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));
        
        BigDecimal cost = req.getQuantity().multiply(req.getPrice());
        if (p.getCashBalance().compareTo(cost) < 0) {
            throw new IllegalArgumentException("Insufficient cash balance");
        }
        p.setCashBalance(p.getCashBalance().subtract(cost));
        portfolioRepository.save(p);

        Holding holding = holdingRepository.findByPortfolioIdAndSymbol(portfolioId, req.getSymbol())
                .orElse(Holding.builder()
                        .portfolio(p)
                        .symbol(req.getSymbol())
                        .quantity(BigDecimal.ZERO)
                        .averageCost(BigDecimal.ZERO)
                        .build());

        BigDecimal oldQty = holding.getQuantity();
        BigDecimal oldAvg = holding.getAverageCost();
        BigDecimal buyQty = req.getQuantity();
        BigDecimal buyPrice = req.getPrice();
        BigDecimal totalQty = oldQty.add(buyQty);

        BigDecimal newAvg = oldQty.multiply(oldAvg).add(buyQty.multiply(buyPrice))
                .divide(totalQty, 4, RoundingMode.HALF_UP);

        holding.setQuantity(totalQty);
        holding.setAverageCost(newAvg);
        holdingRepository.save(holding);

        Transaction txn = Transaction.builder()
                .portfolio(p)
                .type(TransactionType.BUY)
                .symbol(req.getSymbol())
                .quantity(req.getQuantity())
                .price(req.getPrice())
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(txn);
    }

    @Transactional
    public void sell(UUID portfolioId, TransactionRequest req) {
        Portfolio p = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));

        Holding holding = holdingRepository.findByPortfolioIdAndSymbol(portfolioId, req.getSymbol())
                .orElseThrow(() -> new IllegalArgumentException("No holding found for symbol " + req.getSymbol()));

        if (holding.getQuantity().compareTo(req.getQuantity()) < 0) {
            throw new IllegalArgumentException("Insufficient holding quantity to sell");
        }

        BigDecimal revenue = req.getQuantity().multiply(req.getPrice());
        p.setCashBalance(p.getCashBalance().add(revenue));
        portfolioRepository.save(p);

        holding.setQuantity(holding.getQuantity().subtract(req.getQuantity()));
        if (holding.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        Transaction txn = Transaction.builder()
                .portfolio(p)
                .type(TransactionType.SELL)
                .symbol(req.getSymbol())
                .quantity(req.getQuantity())
                .price(req.getPrice())
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(txn);
    }

    public List<HoldingDto> getHoldings(UUID portfolioId) {
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found");
        }
        return holdingRepository.findByPortfolioId(portfolioId).stream()
                .map(h -> new HoldingDto(h.getId(), h.getSymbol(), h.getQuantity(), h.getAverageCost()))
                .collect(Collectors.toList());
    }
}
