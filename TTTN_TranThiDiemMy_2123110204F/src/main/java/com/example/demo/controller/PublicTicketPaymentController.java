package com.example.demo.controller;

import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.User;
import com.example.demo.repository.TicketBookingRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TicketBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/thanh-toan-tour")
@RequiredArgsConstructor
public class PublicTicketPaymentController {

    private final TicketBookingRepository ticketBookingRepository;
    private final TicketBookingService ticketBookingService;
    private final UserRepository userRepository;

    @GetMapping("/{bookingId}")
    public String paymentPage(@PathVariable Long bookingId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        var booking = ticketBookingRepository.findById(bookingId).orElseThrow();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        
        model.addAttribute("booking", booking);
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("rewardPoints", user.getRewardPoints() != null ? user.getRewardPoints() : 0);
        model.addAttribute("finalAmount", booking.getTotalAmount());
        
        return "public/payment/ticket-form";
    }

    @PostMapping("/{bookingId}")
    public String process(@PathVariable Long bookingId,
                          @RequestParam PaymentMethod method,
                          @RequestParam(required = false, defaultValue = "false") boolean usePoints,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes ra) {
        var booking = ticketBookingRepository.findById(bookingId).orElseThrow();
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
            ticketBookingRepository.save(booking);
        }
        
        var payment = ticketBookingService.createPayment(bookingId, method);
        
        // Earn points (1 point per 1000 VND paid)
        int pointsEarned = amountToPay.divide(new BigDecimal("1000")).intValue();
        currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
        user.setRewardPoints(currentPoints + pointsEarned);
        userRepository.save(user);
        
        if (method == PaymentMethod.CASH) {
            ticketBookingService.confirmPayment(payment.getId());
            ra.addFlashAttribute("success", "Đặt vé tham quan & thanh toán thành công! Bạn được cộng " + pointsEarned + " điểm thưởng.");
            return "redirect:/lich-su-dat-ve-tour";
        } else {
            ra.addFlashAttribute("success", "Giao dịch " + method + " đã được tạo. Bạn sẽ nhận " + pointsEarned + " điểm thưởng sau khi xác nhận.");
            return "redirect:/thanh-toan-tour/qr/" + payment.getId();
        }
    }
    
    @GetMapping("/qr/{paymentId}")
    public String showQrCode(@PathVariable Long paymentId, Model model) {
        model.addAttribute("paymentId", paymentId);
        return "public/payment/ticket-qr";
    }
}
