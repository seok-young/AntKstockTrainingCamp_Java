package com.example.AntKstockCamp.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recommendation", uniqueConstraints = {
        @UniqueConstraint(name = "_ticker_signal_uc", columnNames = {"ticker_symbol", "base_date", "signal_type"})
})
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_symbol", referencedColumnName = "symbol", nullable = false)
    private Ticker ticker;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", referencedColumnName = "id", nullable = false)
    private Analysis analysis;

    @Column(length = 10, nullable = false)
    private String signalType;

    @Column(length = 50)
    private String strategyName = "BASIC";

    private LocalDate baseDate;
    private Float price;
    private Boolean isSent = false;

    @CreationTimestamp
    private LocalDateTime createAt;
}