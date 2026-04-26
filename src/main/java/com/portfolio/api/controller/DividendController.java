package com.portfolio.api.controller;

import com.portfolio.api.dto.DividendRequest;
import com.portfolio.api.dto.DividendSummaryDto;
import com.portfolio.api.service.DividendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/portfolios/{id}/dividends")
@RequiredArgsConstructor
public class DividendController {

    private final DividendService dividendService;

    @PostMapping
    public void addDividend(@PathVariable UUID id, @Valid @RequestBody DividendRequest req) {
        dividendService.processDividend(id, req);
    }

    @GetMapping
    public List<DividendSummaryDto> getDividends(@PathVariable UUID id) {
        return dividendService.getDividendSummary(id);
    }
}
