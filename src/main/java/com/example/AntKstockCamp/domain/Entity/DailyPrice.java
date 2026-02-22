package com.example.AntKstockCamp.domain.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SuperBuilder
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "daily_price", uniqueConstraints = {
        @UniqueConstraint(name = "_ticker_date_uc", columnNames = {"ticker_symbol", "date"})
})
public class DailyPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_symbol", referencedColumnName = "symbol", nullable = false)
    private Ticker ticker;

    @Column(nullable = false)
    private LocalDate date;

    private Float openPrice;
    private Float highPrice;
    private Float lowPrice;
    private Float closePrice;
    private Long trdeQty;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}