package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PublicAuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.example.demo.service.EmailService emailService;

    @GetMapping("/dang-ky")
    public String registerForm() {
        return "public/auth/register";
    }

    @PostMapping("/dang-ky")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes ra) {
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
            return "redirect:/dang-ky";
        }
        if (userRepository.existsByEmail(email)) {
            ra.addFlashAttribute("error", "Email đã được sử dụng");
            return "redirect:/dang-ky";
        }
        userRepository.save(User.builder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .role(Role.CUSTOMER)
                .build());
        ra.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/dang-nhap";
    }

    @GetMapping("/dang-nhap")
    public String login(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "public/auth/login";
    }

    @GetMapping("/quen-mat-khau")
    public String forgotPasswordForm() {
        return "public/auth/forgot-password";
    }

    @PostMapping("/quen-mat-khau")
    public String sendOtp(@RequestParam String email, jakarta.servlet.http.HttpSession session, RedirectAttributes ra) {
        if (!userRepository.existsByEmail(email)) {
            ra.addFlashAttribute("error", "Email không tồn tại trong hệ thống");
            return "redirect:/quen-mat-khau";
        }
        
        // Generate 6-digit OTP
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        session.setAttribute("otpEmail", email);
        session.setAttribute("otpCode", otp);
        
        emailService.sendOtpEmail(email, otp);
        
        ra.addFlashAttribute("success", "Mã OTP đã được gửi đến email của bạn");
        return "redirect:/xac-nhan-otp";
    }

    @GetMapping("/xac-nhan-otp")
    public String verifyOtpForm(jakarta.servlet.http.HttpSession session, Model model) {
        if (session.getAttribute("otpEmail") == null) {
            return "redirect:/quen-mat-khau";
        }
        return "public/auth/verify-otp";
    }

    @PostMapping("/xac-nhan-otp")
    public String verifyOtp(@RequestParam String otp, jakarta.servlet.http.HttpSession session, RedirectAttributes ra) {
        String sessionOtp = (String) session.getAttribute("otpCode");
        
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            session.setAttribute("otpVerified", true);
            return "redirect:/dat-lai-mat-khau";
        }
        
        ra.addFlashAttribute("error", "Mã OTP không hợp lệ");
        return "redirect:/xac-nhan-otp";
    }

    @GetMapping("/dat-lai-mat-khau")
    public String resetPasswordForm(jakarta.servlet.http.HttpSession session, Model model) {
        if (session.getAttribute("otpVerified") == null || !(Boolean) session.getAttribute("otpVerified")) {
            return "redirect:/quen-mat-khau";
        }
        return "public/auth/reset-password";
    }

    @PostMapping("/dat-lai-mat-khau")
    public String resetPassword(@RequestParam String newPassword, 
                                @RequestParam String confirmPassword, 
                                jakarta.servlet.http.HttpSession session, 
                                RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
            return "redirect:/dat-lai-mat-khau";
        }
        
        String email = (String) session.getAttribute("otpEmail");
        if (email != null) {
            User user = userRepository.findByEmail(email).orElseThrow();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            session.removeAttribute("otpEmail");
            session.removeAttribute("otpCode");
            session.removeAttribute("otpVerified");
            
            ra.addFlashAttribute("success", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập.");
            return "redirect:/dang-nhap";
        }
        
        return "redirect:/quen-mat-khau";
    }
}
