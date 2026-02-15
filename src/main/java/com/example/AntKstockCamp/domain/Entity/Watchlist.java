package com.example.AntKstockCamp.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "watchlist")
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, name = "asset_type")
    // 'stock', 'etf'
    private String assetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_symbol", referencedColumnName = "symbol", nullable = false)
    private Ticker ticker;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isWatching = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime removedAt;
}
