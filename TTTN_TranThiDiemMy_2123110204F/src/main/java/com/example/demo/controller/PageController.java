package com.example.demo.controller;

import com.example.demo.entity.ContactMessage;
import com.example.demo.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final ContactMessageRepository contactMessageRepository;

    @GetMapping("/gioi-thieu")
    public String about() {
        return "public/about";
    }

    @GetMapping("/lien-he")
    public String contact() {
        return "public/contact";
    }

    @PostMapping("/lien-he")
    public String submitContact(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam String message,
                                RedirectAttributes ra) {
        contactMessageRepository.save(ContactMessage.builder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .message(message)
                .build());
        ra.addFlashAttribute("success", "Cảm ơn bạn! Chúng tôi sẽ liên hệ sớm.");
        return "redirect:/lien-he";
    }
}
