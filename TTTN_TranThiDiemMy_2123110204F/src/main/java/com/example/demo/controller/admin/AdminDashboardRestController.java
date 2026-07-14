package com.example.demo.controller.admin;

import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/revenue")
@RequiredArgsConstructor
public class AdminDashboardRestController {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @GetMapping
    public Map<String, Object> getRevenueData(@AuthenticationPrincipal UserDetails userDetails) {
        boolean isPartner = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTNER"));
        
        List<Payment> payments;
        if (isPartner) {
            User partner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            payments = paymentRepository.findByBookingRoomHotelPartnerId(partner.getId());
        } else {
            payments = paymentRepository.findAll();
        }

        // Filter only completed payments
        List<Payment> completedPayments = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getPaidAt() != null)
                .toList();

        // Aggregate by month (for the current year)
        Map<String, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
        // Initialize months
        for (int i = 1; i <= 12; i++) {
            monthlyRevenue.put("Tháng " + i, BigDecimal.ZERO);
        }

        BigDecimal totalRevenue = BigDecimal.ZERO;

        Map<String, BigDecimal> hotelRevenueMap = new LinkedHashMap<>();
        Map<String, BigDecimal> flightRevenueMap = new LinkedHashMap<>();
        Map<String, BigDecimal> ticketRevenueMap = new LinkedHashMap<>();

        for (Payment p : completedPayments) {
            int month = p.getPaidAt().getMonthValue();
            String monthKey = "Tháng " + month;
            BigDecimal currentAmount = monthlyRevenue.get(monthKey);
            monthlyRevenue.put(monthKey, currentAmount.add(p.getAmount()));
            totalRevenue = totalRevenue.add(p.getAmount());
            
            if (p.getBooking() != null && p.getBooking().getRoom() != null && p.getBooking().getRoom().getHotel() != null) {
                String hotelName = p.getBooking().getRoom().getHotel().getName();
                hotelRevenueMap.merge(hotelName, p.getAmount(), BigDecimal::add);
            }
            if (p.getFlightBooking() != null && p.getFlightBooking().getFlight() != null) {
                String airline = p.getFlightBooking().getFlight().getAirline();
                flightRevenueMap.merge(airline, p.getAmount(), BigDecimal::add);
            }
            if (p.getTicketBooking() != null && p.getTicketBooking().getTicket() != null) {
                String ticketTitle = p.getTicketBooking().getTicket().getTitle();
                ticketRevenueMap.merge(ticketTitle, p.getAmount(), BigDecimal::add);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("labels", monthlyRevenue.keySet());
        response.put("data", monthlyRevenue.values());
        
        List<Map<String, Object>> hotelRevenueList = hotelRevenueMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(e -> Map.of("hotelName", (Object) e.getKey(), "revenue", (Object) e.getValue()))
                .toList();
        response.put("hotelRevenue", hotelRevenueList);
        
        List<Map<String, Object>> flightRevenueList = flightRevenueMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(e -> Map.of("airline", (Object) e.getKey(), "revenue", (Object) e.getValue()))
                .toList();
        response.put("flightRevenue", flightRevenueList);
        
        List<Map<String, Object>> ticketRevenueList = ticketRevenueMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(e -> Map.of("ticketTitle", (Object) e.getKey(), "revenue", (Object) e.getValue()))
                .toList();
        response.put("ticketRevenue", ticketRevenueList);
        
        if (isPartner) {
            // Đối tác nhận 90%
            response.put("totalRevenue", totalRevenue.multiply(new BigDecimal("0.90")));
            response.put("commission", totalRevenue.multiply(new BigDecimal("0.10")));
        } else {
            // Sàn nhận 10%
            response.put("totalRevenue", totalRevenue);
            response.put("commission", totalRevenue.multiply(new BigDecimal("0.10")));
        }

        return response;
    }
}
