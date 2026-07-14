package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String airline;

    @Column(nullable = true)
    private String airlineLogo; // URL to the airline logo

    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String departureCity;

    @Column(nullable = true)
    private String departureAirport; // e.g., Tân Sơn Nhất (SGN)

    @Column(nullable = false)
    private String arrivalCity;

    @Column(nullable = true)
    private String arrivalAirport; // e.g., Nội Bài (HAN)

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = true)
    private BigDecimal businessPrice;

    @Column(nullable = true)
    private Integer availableBusinessSeats;

    @Column(nullable = true)
    private String aircraftModel; // e.g., Airbus A321

    @Column(nullable = true)
    @Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, DELAYED, CANCELLED, COMPLETED
}
