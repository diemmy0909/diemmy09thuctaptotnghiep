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
    private final com.example.demo.repository.MealRepository mealRepository;

    public BigDecimal calculateTotal(Flight flight, int adults, int children, int infants, int extraBaggageKg, String seatClass, String mealOption) {
        BigDecimal ticketPrice = "BUSINESS".equalsIgnoreCase(seatClass) && flight.getBusinessPrice() != null 
                ? flight.getBusinessPrice() : flight.getPrice();
        BigDecimal baseAdultTotal = ticketPrice.multiply(BigDecimal.valueOf(adults));
        BigDecimal baseChildTotal = ticketPrice.multiply(BigDecimal.valueOf(children)).multiply(new BigDecimal("0.75"));
        BigDecimal baseInfantTotal = ticketPrice.multiply(BigDecimal.valueOf(infants)).multiply(new BigDecimal("0.10"));
        BigDecimal baseTotal = baseAdultTotal.add(baseChildTotal).add(baseInfantTotal);
        // Giả sử giá hành lý mua thêm là 10.000 VNĐ cho mỗi kg (ví dụ)
        BigDecimal baggagePrice = BigDecimal.valueOf(extraBaggageKg * 10000L);
        
        BigDecimal mealPrice = BigDecimal.ZERO;
        if (mealOption != null && !mealOption.isEmpty()) {
            var mealOpt = mealRepository.findByName(mealOption);
            if (mealOpt.isPresent()) {
                mealPrice = mealOpt.get().getPrice();
            }
        }
        
        mealPrice = mealPrice.multiply(BigDecimal.valueOf(adults + children));
        
        return baseTotal.add(baggagePrice).add(mealPrice);
    }

    @Transactional
    public FlightBooking createBooking(Long flightId, Long userId, int adults, int children, int infants, 
                                       String[] passengerTypes, String[] passengerNames, String[] passengerIdCards,
                                       int extraBaggageKg, String seatClass, String mealOption) {
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        User customer = userRepository.findById(userId).orElseThrow();

        FlightBooking booking = FlightBooking.builder()
                .bookingCode("FBK" + System.currentTimeMillis())
                .customer(customer)
                .flight(flight)
                .adults(adults)
                .children(children)
                .infants(infants)
                .extraBaggageKg(extraBaggageKg)
                .seatClass(seatClass)
                .mealOption(mealOption)
                .totalAmount(calculateTotal(flight, adults, children, infants, extraBaggageKg, seatClass, mealOption))
                .status(BookingStatus.PENDING)
                .build();
                
        // Create passengers
        java.util.List<FlightPassenger> flightPassengers = new java.util.ArrayList<>();
        if (passengerTypes != null) {
            for (int i = 0; i < passengerTypes.length; i++) {
                FlightPassenger fp = FlightPassenger.builder()
                    .flightBooking(booking)
                    .passengerType(passengerTypes[i])
                    .fullName(passengerNames[i])
                    .idCard(passengerIdCards[i])
                    .build();
                flightPassengers.add(fp);
            }
        }
        booking.setFlightPassengers(flightPassengers);

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
