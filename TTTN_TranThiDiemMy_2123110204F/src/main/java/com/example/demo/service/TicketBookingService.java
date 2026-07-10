package com.example.demo.service;

import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketBooking;
import com.example.demo.entity.User;
import com.example.demo.repository.TicketBookingRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TicketBookingService {

    private final TicketBookingRepository ticketBookingRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public BigDecimal calculateTotal(Ticket ticket, int adultCount, int childCount) {
        BigDecimal adultTotal = ticket.getAdultPrice().multiply(BigDecimal.valueOf(adultCount));
        BigDecimal childTotal = ticket.getChildPrice().multiply(BigDecimal.valueOf(childCount));
        return adultTotal.add(childTotal);
    }

    @Transactional
    public TicketBooking createBooking(Long ticketId, Long userId, LocalDate date, int adultCount, int childCount) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        User customer = userRepository.findById(userId).orElseThrow();

        TicketBooking booking = TicketBooking.builder()
                .bookingCode("TCK" + System.currentTimeMillis())
                .customer(customer)
                .ticket(ticket)
                .participationDate(date)
                .adultCount(adultCount)
                .childCount(childCount)
                .totalAmount(calculateTotal(ticket, adultCount, childCount))
                .status(BookingStatus.PENDING)
                .build();

        return ticketBookingRepository.save(booking);
    }
}
