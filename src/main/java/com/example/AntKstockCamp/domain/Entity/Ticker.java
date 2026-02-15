package com.example.AntKstockCamp.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "ticker")
public class Ticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    private String symbolOrigin;

    @Column(length = 20, unique = true)
    private String symbol;

    @Column(length = 100)
    private String nameKor;

    @Column(length = 20)
    private String assetType;

    @Column(length = 50)
    private String marketType;

    @Column(length = 20)
    private String dateListing;

    @Column
    private Long totalShares;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

}
