package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.FlightBookingRepository;
import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.demo.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlightBookingService {

    private final FlightBookingRepository flightBookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

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

    @Transactional
    public Payment createPayment(Long flightBookingId, PaymentMethod method) {
        FlightBooking booking = flightBookingRepository.findById(flightBookingId).orElseThrow();
        Payment payment = Payment.builder()
                .paymentCode("PAY" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .flightBooking(booking)
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
        
        FlightBooking booking = payment.getFlightBooking();
        booking.setStatus(BookingStatus.CONFIRMED);
        flightBookingRepository.save(booking);
        
        notificationService.createNotification(
                booking.getCustomer(),
                "Xác nhận thanh toán vé máy bay",
                "Đơn vé máy bay " + booking.getBookingCode() + " của bạn đã được thanh toán thành công.",
                NotificationType.BOOKING_CONFIRMED
        );
    }
}
