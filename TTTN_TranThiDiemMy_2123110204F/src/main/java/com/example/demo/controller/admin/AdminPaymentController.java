package com.example.demo.controller.admin;

import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("pageTitle", "Quản lý thanh toán");
        model.addAttribute("activeMenu", "payments");
        
        boolean isPartner = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTNER"));
        if (isPartner) {
            User partner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            model.addAttribute("payments", paymentRepository.findByBookingRoomHotelPartnerId(partner.getId()));
        } else {
            model.addAttribute("payments", paymentRepository.findAll());
        }
        
        return "admin/payments/list";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, RedirectAttributes ra) {
        Payment payment = paymentRepository.findById(id).orElseThrow();
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        ra.addFlashAttribute("success", "Đã xác nhận thanh toán");
        return "redirect:/admin/payments";
    }

    @PostMapping("/{id}/refund")
    public String refund(@PathVariable Long id, RedirectAttributes ra) {
        Payment payment = paymentRepository.findById(id).orElseThrow();
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        ra.addFlashAttribute("success", "Đã hoàn tiền");
        return "redirect:/admin/payments";
    }
}
