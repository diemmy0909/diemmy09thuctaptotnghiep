package com.example.demo.repository;

import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByBookingRoomHotelPartnerId(Long partnerId);
}
