package com.portfolio.api.service;

import com.portfolio.api.dto.DividendRequest;
import com.portfolio.api.dto.TransactionRequest;
import com.portfolio.api.entity.Holding;
import com.portfolio.api.entity.Portfolio;
import com.portfolio.api.repository.DividendRepository;
import com.portfolio.api.repository.HoldingRepository;
import com.portfolio.api.repository.PortfolioRepository;
import com.portfolio.api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private HoldingRepository holdingRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private DividendRepository dividendRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @InjectMocks
    private DividendService dividendService;

    private Portfolio portfolio;
    private UUID pId;

    @BeforeEach
    void setUp() {
        pId = UUID.randomUUID();
        portfolio = new Portfolio(pId, "Test Client", "High", new BigDecimal("1000.0000"));
    }

    @Test
    void testBuyWeightedAverageCalculation_Success() {
        TransactionRequest req = new TransactionRequest();
        req.setSymbol("AAPL");
        req.setQuantity(new BigDecimal("10.0000"));
        req.setPrice(new BigDecimal("150.0000"));

        Holding existingHolding = new Holding(UUID.randomUUID(), portfolio, "AAPL", new BigDecimal("10.0000"), new BigDecimal("100.0000"));

        when(portfolioRepository.findById(pId)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndSymbol(pId, "AAPL")).thenReturn(Optional.of(existingHolding));

        portfolioService.buy(pId, req);

        ArgumentCaptor<Holding> holdingCaptor = ArgumentCaptor.forClass(Holding.class);
        verify(holdingRepository).save(holdingCaptor.capture());

        Holding saved = holdingCaptor.getValue();
        // new avg = (10*100 + 10*150) / 20 = 2500 / 20 = 125.0000
        assertEquals(new BigDecimal("125.0000"), saved.getAverageCost());
        assertEquals(new BigDecimal("20.0000"), saved.getQuantity());
    }

    @Test
    void testSell_FailIfQuantityExceedsHolding() {
        TransactionRequest req = new TransactionRequest();
        req.setSymbol("AAPL");
        req.setQuantity(new BigDecimal("15.0000"));
        req.setPrice(new BigDecimal("160.0000"));

        Holding existingHolding = new Holding(UUID.randomUUID(), portfolio, "AAPL", new BigDecimal("10.0000"), new BigDecimal("100.0000"));

        when(portfolioRepository.findById(pId)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndSymbol(pId, "AAPL")).thenReturn(Optional.of(existingHolding));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.sell(pId, req);
        });

        assertEquals("Insufficient holding quantity to sell", ex.getMessage());
    }

    @Test
    void testDividendCashUpdate_Success() {
        // Mock to inject dividendService dependencies properly since they share them
        dividendService = new DividendService(portfolioRepository, holdingRepository, dividendRepository);

        DividendRequest req = new DividendRequest();
        req.setSymbol("AAPL");
        req.setPerShareAmount(new BigDecimal("1.5000"));
        req.setRecordDate(LocalDate.now());

        Holding existingHolding = new Holding(UUID.randomUUID(), portfolio, "AAPL", new BigDecimal("10.0000"), new BigDecimal("100.0000"));

        when(portfolioRepository.findById(pId)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndSymbol(pId, "AAPL")).thenReturn(Optional.of(existingHolding));

        dividendService.processDividend(pId, req);

        // payout = 10 * 1.50 = 15.0000, new cash balance = 1000 + 15 = 1015.0000
        assertEquals(new BigDecimal("1015.0000"), portfolio.getCashBalance());
        verify(portfolioRepository).save(portfolio);
        verify(dividendRepository).save(any());
    }
}
