package com.example.AntKstockCamp.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "analysis", uniqueConstraints = {
        @UniqueConstraint(name = "_ticker_date_uc", columnNames = {"ticker_symbol", "date"})
})
@org.hibernate.annotations.Check(
        constraints = "close_Price >= 0 AND ma5 >= 0 AND ma20 >= 0 AND ma60 >= 0 AND ma120 >= 0 AND rsi >= 0 AND rsi <=100"
)

public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticker_symbol", referencedColumnName = "symbol", nullable = false)
    private Ticker ticker;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "close_price")
    private Float closePrice;
    private Float ma5;
    private Float ma20;
    private Float ma60;
    private Float ma120;
    private Float macd;
    private Float macdSignal;
    private Float macdHist;
    private Float rsi;
    private Float bbMiddle;
    private Float bbUpper;
    private Float bbLower;
}
