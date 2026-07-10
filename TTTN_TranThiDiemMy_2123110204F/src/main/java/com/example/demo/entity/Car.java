package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // SUV, Sedan, MPV, v.v.

    @Column(nullable = false)
    private String transmission; // Tự động / Số sàn

    @Column(nullable = false)
    private Integer seats;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerDay;

    @Column(length = 500)
    private String imageUrl;

    private String provider; // Nhà cung cấp (AVIS, Hertz, v.v.)
}
