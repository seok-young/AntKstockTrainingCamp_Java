package com.example.AntKstockCamp.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "portfolio")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticker_symbol", referencedColumnName = "symbol", nullable = false)
    private Ticker ticker;

    @OneToOne
    @JoinColumn(name = "recommendation_id", referencedColumnName = "id", nullable = false)
    private Recommendation recommendation;

    private Integer quantity;
    private Float buyPrice;
    private LocalDateTime buyDate;
    private Float sellPrice;
    private LocalDateTime sellDate;
    private Integer isActive = 1;
}
