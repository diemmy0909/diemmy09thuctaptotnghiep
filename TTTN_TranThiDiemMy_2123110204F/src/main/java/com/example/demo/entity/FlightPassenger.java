package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flight_passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightPassenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_booking_id", nullable = false)
    private FlightBooking flightBooking;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String idCard; // CCCD, Hộ chiếu, hoặc Giấy khai sinh

    @Column(nullable = false)
    private String passengerType; // ADULT, CHILD, INFANT
}
