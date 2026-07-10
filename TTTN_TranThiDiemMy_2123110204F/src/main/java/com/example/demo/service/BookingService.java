package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public BigDecimal calculateTotal(Room room, LocalDate checkIn, LocalDate checkOut, int numberOfRooms) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < 1) nights = 1;
        return room.getPrice().multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(numberOfRooms));
    }

    @Transactional
    public Booking createBooking(Long roomId, Long userId, LocalDate checkIn, LocalDate checkOut, int numberOfRooms) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        User customer = userRepository.findById(userId).orElseThrow();

        Booking booking = Booking.builder()
                .bookingCode("BK" + System.currentTimeMillis())
                .customer(customer)
                .room(room)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .numberOfRooms(numberOfRooms)
                .totalAmount(calculateTotal(room, checkIn, checkOut, numberOfRooms))
                .status(BookingStatus.PENDING)
                .build();

        return bookingRepository.save(booking);
    }

    @Transactional
    public Payment createPayment(Long bookingId, PaymentMethod method) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Payment payment = Payment.builder()
                .paymentCode("PAY" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .booking(booking)
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
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        
        notificationService.createNotification(
                booking.getCustomer(),
                "Xác nhận đặt phòng thành công",
                "Đơn đặt phòng " + booking.getBookingCode() + " của bạn đã được xác nhận. Vui lòng kiểm tra lịch sử đặt phòng.",
                NotificationType.BOOKING_CONFIRMED
        );
    }
}
