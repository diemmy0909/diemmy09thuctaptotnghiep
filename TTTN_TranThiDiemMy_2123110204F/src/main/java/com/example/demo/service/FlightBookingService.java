package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.FlightBookingRepository;
import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FlightBookingService {

    private final FlightBookingRepository flightBookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public BigDecimal calculateTotal(Flight flight, int passengers, int extraBaggageKg) {
        BigDecimal baseTotal = flight.getPrice().multiply(BigDecimal.valueOf(passengers));
        // Giả sử giá hành lý mua thêm là 10.000 VNĐ cho mỗi kg (ví dụ)
        BigDecimal baggagePrice = BigDecimal.valueOf(extraBaggageKg * 10000L);
        return baseTotal.add(baggagePrice);
    }

    @Transactional
    public FlightBooking createBooking(Long flightId, Long userId, int passengers, String seatNumbers, int extraBaggageKg) {
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        User customer = userRepository.findById(userId).orElseThrow();

        FlightBooking booking = FlightBooking.builder()
                .bookingCode("FBK" + System.currentTimeMillis())
                .customer(customer)
                .flight(flight)
                .passengers(passengers)
                .seatNumbers(seatNumbers)
                .extraBaggageKg(extraBaggageKg)
                .totalAmount(calculateTotal(flight, passengers, extraBaggageKg))
                .status(BookingStatus.PENDING)
                .build();

        return flightBookingRepository.save(booking);
    }
}
