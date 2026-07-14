package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(nullable = false)
    private Integer adults;

    @Column(nullable = false)
    private Integer children;

    @Column(nullable = false)
    private Integer infants;

    @OneToMany(mappedBy = "flightBooking", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<FlightPassenger> flightPassengers;

    @Column
    private String seatNumbers;

    @Column
    @Builder.Default
    private Integer extraBaggageKg = 0;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column
    private String seatClass;

    @Column
    private String mealOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_flight_id")
    private Flight returnFlight;

    @Column
    private String returnSeatNumbers;

    @Column
    private String returnSeatClass;

    @Column
    private String returnMealOption;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
