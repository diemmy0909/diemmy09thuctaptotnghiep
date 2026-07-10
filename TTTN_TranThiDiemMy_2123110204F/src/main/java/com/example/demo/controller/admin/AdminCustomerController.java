package com.example.demo.controller.admin;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Quản lý khách hàng");
        model.addAttribute("activeMenu", "customers");
        model.addAttribute("customers", userRepository.findByRole(Role.CUSTOMER));
        return "admin/customers/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        User customer = userRepository.findById(id).orElseThrow();
        model.addAttribute("pageTitle", customer.getFullName());
        model.addAttribute("activeMenu", "customers");
        model.addAttribute("customer", customer);
        model.addAttribute("bookings", bookingRepository.findByCustomerIdOrderByCreatedAtDesc(id));
        return "admin/customers/detail";
    }

    @PostMapping("/{id}/toggle-lock")
    public String toggleLock(@PathVariable Long id, RedirectAttributes ra) {
        User user = userRepository.findById(id).orElseThrow();
        user.setLocked(!user.isLocked());
        userRepository.save(user);
        ra.addFlashAttribute("success", user.isLocked() ? "Đã khóa tài khoản" : "Đã mở khóa tài khoản");
        return "redirect:/admin/customers";
    }
}
