package com.example.demo.service;

import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketBooking;
import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.NotificationType;
import com.example.demo.entity.User;
import com.example.demo.repository.TicketBookingRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketBookingService {

    private final TicketBookingRepository ticketBookingRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

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

    @Transactional
    public Payment createPayment(Long ticketBookingId, PaymentMethod method) {
        TicketBooking booking = ticketBookingRepository.findById(ticketBookingId).orElseThrow();
        Payment payment = Payment.builder()
                .paymentCode("PAY" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .ticketBooking(booking)
                .amount(booking.getTotalAmount())
                .method(method)
                .status(PaymentStatus.PENDING)
                .build();
        return paymentRepository.save(payment);
    }

    @Transactional
    public void confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(java.time.LocalDateTime.now());
        paymentRepository.save(payment);
        
        TicketBooking booking = payment.getTicketBooking();
        booking.setStatus(BookingStatus.CONFIRMED);
        ticketBookingRepository.save(booking);
        
        notificationService.createNotification(
                booking.getCustomer(),
                "Xác nhận thanh toán vé tham quan",
                "Đơn vé tham quan " + booking.getBookingCode() + " của bạn đã được thanh toán thành công.",
                NotificationType.BOOKING_CONFIRMED
        );
    }
}
