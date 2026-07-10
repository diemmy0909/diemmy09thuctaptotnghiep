package com.example.demo.repository;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Booking> findByStatus(BookingStatus status);
    long countByStatus(BookingStatus status);
    List<Booking> findByStatusAndCheckInDate(BookingStatus status, java.time.LocalDate checkInDate);
    List<Booking> findByRoomHotelPartnerId(Long partnerId);
}
