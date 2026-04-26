package com.portfolio.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "dividend")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dividend {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false, length = 50)
    private String symbol;

    @Column(name = "per_share_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal perShareAmount;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;
}
