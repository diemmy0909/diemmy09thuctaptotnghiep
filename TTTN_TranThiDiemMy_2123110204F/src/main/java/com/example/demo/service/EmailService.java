package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã OTP khôi phục mật khẩu - " + "Aluminum Travel");
        message.setText("Xin chào,\n\n" +
                "Bạn đã yêu cầu khôi phục mật khẩu. Dưới đây là mã OTP của bạn:\n\n" +
                "Mã OTP: " + otp + "\n\n" +
                "Vui lòng không chia sẻ mã này cho bất kỳ ai. Mã OTP có hiệu lực trong 10 phút.\n\n" +
                "Trân trọng,\nĐội ngũ hỗ trợ.");
        
        System.out.println("==========================================");
        System.out.println("MÃ OTP CỦA BẠN LÀ: " + otp);
        System.out.println("==========================================");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log error or ignore if mail is not configured yet
            System.err.println("Cannot send email (Mail might not be configured): " + e.getMessage());
        }
    }
}
