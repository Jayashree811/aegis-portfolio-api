package com.portfolio.api.controller;

import com.portfolio.api.dto.HoldingDto;
import com.portfolio.api.dto.PortfolioDto;
import com.portfolio.api.dto.TransactionRequest;
import com.portfolio.api.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioDto createPortfolio(@Valid @RequestBody PortfolioDto dto) {
        return portfolioService.createPortfolio(dto);
    }

    @GetMapping("/{id}")
    public PortfolioDto getPortfolio(@PathVariable UUID id) {
        return portfolioService.getPortfolio(id);
    }

    @PostMapping("/{id}/transactions/buy")
    public void buyTransaction(@PathVariable UUID id, @Valid @RequestBody TransactionRequest req) {
        portfolioService.buy(id, req);
    }

    @PostMapping("/{id}/transactions/sell")
    public void sellTransaction(@PathVariable UUID id, @Valid @RequestBody TransactionRequest req) {
        portfolioService.sell(id, req);
    }

    @GetMapping("/{id}/holdings")
    public List<HoldingDto> getHoldings(@PathVariable UUID id) {
        return portfolioService.getHoldings(id);
    }
}
