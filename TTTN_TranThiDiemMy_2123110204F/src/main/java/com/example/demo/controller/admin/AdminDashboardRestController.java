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

        for (Payment p : completedPayments) {
            int month = p.getPaidAt().getMonthValue();
            String monthKey = "Tháng " + month;
            BigDecimal currentAmount = monthlyRevenue.get(monthKey);
            monthlyRevenue.put(monthKey, currentAmount.add(p.getAmount()));
            totalRevenue = totalRevenue.add(p.getAmount());
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("labels", monthlyRevenue.keySet());
        response.put("data", monthlyRevenue.values());
        
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
