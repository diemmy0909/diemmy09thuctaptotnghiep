package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer maxGuests;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoomStatus status = RoomStatus.AVAILABLE;

    private String imageUrl;

    @Builder.Default
    @Column(name = "free_cancellation")
    private Boolean freeCancellation = false;

    @Builder.Default
    @Column(name = "breakfast_included")
    private Boolean breakfastIncluded = false;

    @Builder.Default
    @Column(name = "prepay_required")
    private Boolean prepayRequired = false;
}
