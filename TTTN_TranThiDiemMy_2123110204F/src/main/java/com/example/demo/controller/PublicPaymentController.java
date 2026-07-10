package com.example.demo.controller;

import com.example.demo.entity.PaymentMethod;
import com.example.demo.repository.BookingRepository;
import com.example.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.FlightBookingRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.math.BigDecimal;

@Controller
@RequestMapping("/thanh-toan")
@RequiredArgsConstructor
public class PublicPaymentController {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final FlightBookingRepository flightBookingRepository;
    private final com.example.demo.repository.PromotionRepository promotionRepository;

    @GetMapping("/{bookingId}")
    public String paymentPage(@PathVariable Long bookingId, 
                              @RequestParam(required = false) String promoCode,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        var booking = bookingRepository.findById(bookingId).orElseThrow();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        
        // 1. Combo Tiết kiệm: Khách hàng có chuyến bay nào trong tương lai không?
        boolean hasFlight = flightBookingRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()).stream()
            .anyMatch(f -> !f.getFlight().getDepartureTime().toLocalDate().isBefore(java.time.LocalDate.now()));
            
        BigDecimal comboDiscount = BigDecimal.ZERO;
        if (hasFlight) {
            comboDiscount = booking.getTotalAmount().multiply(new BigDecimal("0.15"));
        }
        
        BigDecimal amountAfterCombo = booking.getTotalAmount().subtract(comboDiscount);
        BigDecimal promoDiscount = BigDecimal.ZERO;
        String validPromoCode = null;
        String promoError = null;

        if (promoCode != null && !promoCode.trim().isEmpty()) {
            var promoOpt = promotionRepository.findByCode(promoCode.trim().toUpperCase());
            if (promoOpt.isPresent()) {
                var promo = promoOpt.get();
                java.time.LocalDate today = java.time.LocalDate.now();
                if (promo.isActive() && !today.isBefore(promo.getStartDate()) && !today.isAfter(promo.getEndDate())) {
                    if (promo.getDiscountPercent() != null && promo.getDiscountPercent() > 0) {
                        promoDiscount = amountAfterCombo.multiply(new BigDecimal(promo.getDiscountPercent())).divide(new BigDecimal("100"));
                    } else if (promo.getDiscountAmount() != null && promo.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                        promoDiscount = promo.getDiscountAmount();
                    }
                    // Ensure discount doesn't exceed amount
                    if (promoDiscount.compareTo(amountAfterCombo) > 0) {
                        promoDiscount = amountAfterCombo;
                    }
                    validPromoCode = promo.getCode();
                } else {
                    promoError = "Mã khuyến mãi đã hết hạn hoặc không hợp lệ.";
                }
            } else {
                promoError = "Mã khuyến mãi không tồn tại.";
            }
        }
        
        BigDecimal finalAmount = amountAfterCombo.subtract(promoDiscount);
        
        model.addAttribute("booking", booking);
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("rewardPoints", user.getRewardPoints());
        model.addAttribute("hasFlight", hasFlight);
        model.addAttribute("comboDiscount", comboDiscount);
        model.addAttribute("promoCode", validPromoCode);
        model.addAttribute("promoDiscount", promoDiscount);
        model.addAttribute("promoError", promoError);
        model.addAttribute("finalAmount", finalAmount);
        
        return "public/payment/form";
    }

    @PostMapping("/{bookingId}")
    public String process(@PathVariable Long bookingId,
                          @RequestParam PaymentMethod method,
                          @RequestParam(required = false, defaultValue = "false") boolean usePoints,
                          @RequestParam(required = false) String promoCode,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes ra) {
        var booking = bookingRepository.findById(bookingId).orElseThrow();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        
        // Combo discount calculation
        boolean hasFlight = flightBookingRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()).stream()
            .anyMatch(f -> !f.getFlight().getDepartureTime().toLocalDate().isBefore(java.time.LocalDate.now()));
        BigDecimal comboDiscount = hasFlight ? booking.getTotalAmount().multiply(new BigDecimal("0.15")) : BigDecimal.ZERO;
        BigDecimal amountToPay = booking.getTotalAmount().subtract(comboDiscount);
        
        // Promo calculation
        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (promoCode != null && !promoCode.trim().isEmpty()) {
            var promoOpt = promotionRepository.findByCode(promoCode.trim().toUpperCase());
            if (promoOpt.isPresent()) {
                var promo = promoOpt.get();
                java.time.LocalDate today = java.time.LocalDate.now();
                if (promo.isActive() && !today.isBefore(promo.getStartDate()) && !today.isAfter(promo.getEndDate())) {
                    if (promo.getDiscountPercent() != null && promo.getDiscountPercent() > 0) {
                        promoDiscount = amountToPay.multiply(new BigDecimal(promo.getDiscountPercent())).divide(new BigDecimal("100"));
                    } else if (promo.getDiscountAmount() != null) {
                        promoDiscount = promo.getDiscountAmount();
                    }
                    if (promoDiscount.compareTo(amountToPay) > 0) promoDiscount = amountToPay;
                }
            }
        }
        amountToPay = amountToPay.subtract(promoDiscount);
        
        // Points usage
        int pointsUsed = 0;
        int currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
        if (usePoints && currentPoints > 0) {
            pointsUsed = Math.min(currentPoints, amountToPay.intValue());
            amountToPay = amountToPay.subtract(new BigDecimal(pointsUsed));
            user.setRewardPoints(currentPoints - pointsUsed);
        }
        
        // Update booking amount if discounts applied (for simplicity in this demo)
        if (comboDiscount.compareTo(BigDecimal.ZERO) > 0 || pointsUsed > 0 || promoDiscount.compareTo(BigDecimal.ZERO) > 0) {
            booking.setTotalAmount(amountToPay);
            bookingRepository.save(booking);
        }
        
        var payment = bookingService.createPayment(bookingId, method);
        
        // Earn points (1 point per 1000 VND paid)
        int pointsEarned = amountToPay.divide(new BigDecimal("1000")).intValue();
        currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
        user.setRewardPoints(currentPoints + pointsEarned);
        userRepository.save(user);
        
        if (method == PaymentMethod.CASH || method == PaymentMethod.BANK_TRANSFER) {
            bookingService.confirmPayment(payment.getId());
            ra.addFlashAttribute("success", "Đặt phòng & thanh toán thành công! Bạn được cộng " + pointsEarned + " điểm thưởng.");
            return "redirect:/lich-su-dat-phong";
        } else {
            ra.addFlashAttribute("success", "Giao dịch " + method + " đã được tạo. Bạn sẽ nhận " + pointsEarned + " điểm thưởng sau khi xác nhận.");
            return "redirect:/thanh-toan/qr/" + payment.getId();
        }
    }
    
    @GetMapping("/qr/{paymentId}")
    public String showQrCode(@PathVariable Long paymentId, Model model) {
        model.addAttribute("paymentId", paymentId);
        return "public/payment/qr";
    }
}
