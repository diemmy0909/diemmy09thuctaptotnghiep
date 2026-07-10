package com.example.demo.repository;

import com.example.demo.entity.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightBookingRepository extends JpaRepository<FlightBooking, Long> {
    List<FlightBooking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
