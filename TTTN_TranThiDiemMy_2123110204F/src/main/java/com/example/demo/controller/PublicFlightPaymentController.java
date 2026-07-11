package com.example.demo.controller;

import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.User;
import com.example.demo.repository.FlightBookingRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FlightBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/thanh-toan-may-bay")
@RequiredArgsConstructor
public class PublicFlightPaymentController {

    private final FlightBookingRepository flightBookingRepository;
    private final FlightBookingService flightBookingService;
    private final UserRepository userRepository;

    @GetMapping("/{bookingId}")
    public String paymentPage(@PathVariable Long bookingId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        var booking = flightBookingRepository.findById(bookingId).orElseThrow();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        
        model.addAttribute("booking", booking);
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("rewardPoints", user.getRewardPoints() != null ? user.getRewardPoints() : 0);
        model.addAttribute("finalAmount", booking.getTotalAmount());
        
        return "public/payment/flight-form";
    }

    @PostMapping("/{bookingId}")
    public String process(@PathVariable Long bookingId,
                          @RequestParam PaymentMethod method,
                          @RequestParam(required = false, defaultValue = "false") boolean usePoints,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes ra) {
        var booking = flightBookingRepository.findById(bookingId).orElseThrow();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        
        BigDecimal amountToPay = booking.getTotalAmount();
        
        // Points usage
        int pointsUsed = 0;
        int currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
        if (usePoints && currentPoints > 0) {
            pointsUsed = Math.min(currentPoints, amountToPay.intValue());
            amountToPay = amountToPay.subtract(new BigDecimal(pointsUsed));
            user.setRewardPoints(currentPoints - pointsUsed);
        }
        
        if (pointsUsed > 0) {
            booking.setTotalAmount(amountToPay);
            flightBookingRepository.save(booking);
        }
        
        var payment = flightBookingService.createPayment(bookingId, method);
        
        // Earn points (1 point per 1000 VND paid)
        int pointsEarned = amountToPay.divide(new BigDecimal("1000")).intValue();
        currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
        user.setRewardPoints(currentPoints + pointsEarned);
        userRepository.save(user);
        
        if (method == PaymentMethod.CASH) {
            flightBookingService.confirmPayment(payment.getId());
            ra.addFlashAttribute("success", "Đặt vé máy bay & thanh toán thành công! Bạn được cộng " + pointsEarned + " điểm thưởng.");
            return "redirect:/lich-su-dat-ve";
        } else {
            ra.addFlashAttribute("success", "Giao dịch " + method + " đã được tạo. Bạn sẽ nhận " + pointsEarned + " điểm thưởng sau khi xác nhận.");
            return "redirect:/thanh-toan-may-bay/qr/" + payment.getId();
        }
    }
    
    @GetMapping("/qr/{paymentId}")
    public String showQrCode(@PathVariable Long paymentId, Model model) {
        model.addAttribute("paymentId", paymentId);
        return "public/payment/flight-qr";
    }
}
