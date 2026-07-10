package com.example.demo.controller.admin;

import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Role;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Tổng quan");
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("hotelCount", hotelRepository.count());
        model.addAttribute("roomCount", roomRepository.count());
        model.addAttribute("bookingCount", bookingRepository.count());
        model.addAttribute("customerCount", userRepository.findByRole(Role.CUSTOMER).size());
        model.addAttribute("pendingBookings", bookingRepository.countByStatus(BookingStatus.PENDING));
        model.addAttribute("pendingReviews", reviewRepository.findByApprovedFalseOrderByCreatedAtDesc().size());
        model.addAttribute("paymentCount", paymentRepository.count());
        return "admin/dashboard";
    }
}
