package com.portfolio.api;

import com.portfolio.api.dto.PortfolioDto;
import com.portfolio.api.dto.TransactionRequest;
import com.portfolio.api.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PortfolioIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @BeforeEach
    void setup() {
        portfolioRepository.deleteAll();
    }

    @Test
    void endToEndFlow() {
        // 1. Create Portfolio
        PortfolioDto createDto = new PortfolioDto();
        createDto.setClientName("John Doe");
        createDto.setRiskProfile("Medium");
        createDto.setCashBalance(new BigDecimal("5000.00"));

        ResponseEntity<PortfolioDto> createResponse = restTemplate.postForEntity("/v1/portfolios", createDto, PortfolioDto.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody().getId());
        UUID id = createResponse.getBody().getId();

        // 2. Buy Transaction
        TransactionRequest buyReq = new TransactionRequest();
        buyReq.setSymbol("TSLA");
        buyReq.setQuantity(new BigDecimal("10"));
        buyReq.setPrice(new BigDecimal("200"));

        ResponseEntity<Void> buyRes = restTemplate.postForEntity("/v1/portfolios/" + id + "/transactions/buy", buyReq, Void.class);
        assertEquals(HttpStatus.OK, buyRes.getStatusCode());

        // 3. Verify Cache Balance Deducted
        ResponseEntity<PortfolioDto> getRes = restTemplate.getForEntity("/v1/portfolios/" + id, PortfolioDto.class);
        assertEquals(new BigDecimal("3000.0000"), getRes.getBody().getCashBalance());
    }
}
