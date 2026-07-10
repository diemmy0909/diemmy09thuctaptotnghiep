package com.example.demo.repository;

import com.example.demo.entity.TicketBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketBookingRepository extends JpaRepository<TicketBooking, Long> {
    List<TicketBooking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<TicketBooking> findByStatusAndParticipationDate(com.example.demo.entity.BookingStatus status, java.time.LocalDate participationDate);
}
