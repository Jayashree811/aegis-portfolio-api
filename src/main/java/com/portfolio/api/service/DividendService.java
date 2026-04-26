package com.portfolio.api.service;

import com.portfolio.api.dto.DividendRequest;
import com.portfolio.api.dto.DividendSummaryDto;
import com.portfolio.api.entity.Dividend;
import com.portfolio.api.entity.Holding;
import com.portfolio.api.entity.Portfolio;
import com.portfolio.api.repository.DividendRepository;
import com.portfolio.api.repository.HoldingRepository;
import com.portfolio.api.repository.PortfolioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DividendService {
    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final DividendRepository dividendRepository;

    @Transactional
    public void processDividend(UUID portfolioId, DividendRequest req) {
        Portfolio p = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));

        Holding holding = holdingRepository.findByPortfolioIdAndSymbol(portfolioId, req.getSymbol())
                .orElseThrow(() -> new IllegalArgumentException("No holding found for symbol " + req.getSymbol()));

        BigDecimal payout = holding.getQuantity().multiply(req.getPerShareAmount());
        p.setCashBalance(p.getCashBalance().add(payout));
        portfolioRepository.save(p);

        Dividend d = Dividend.builder()
                .portfolio(p)
                .symbol(req.getSymbol())
                .perShareAmount(req.getPerShareAmount())
                .totalAmount(payout)
                .recordDate(req.getRecordDate())
                .build();
        dividendRepository.save(d);
    }

    public List<DividendSummaryDto> getDividendSummary(UUID portfolioId) {
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found");
        }
        List<Dividend> dividends = dividendRepository.findByPortfolioId(portfolioId);
        
        Map<String, BigDecimal> collected = dividends.stream()
                .collect(Collectors.groupingBy(
                        Dividend::getSymbol,
                        Collectors.reducing(BigDecimal.ZERO, 
                                Dividend::getTotalAmount, 
                                BigDecimal::add)
                ));

        return collected.entrySet().stream()
                .map(e -> new DividendSummaryDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
