package com.example.demo.controller.admin;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("pageTitle", "Quản lý đặt phòng");
        model.addAttribute("activeMenu", "bookings");
        
        boolean isPartner = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTNER"));
        if (isPartner) {
            User partner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            model.addAttribute("bookings", bookingRepository.findByRoomHotelPartnerId(partner.getId()));
        } else {
            model.addAttribute("bookings", bookingRepository.findAll());
        }
        
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/bookings/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        model.addAttribute("pageTitle", "Chi tiết đặt phòng " + booking.getBookingCode());
        model.addAttribute("activeMenu", "bookings");
        model.addAttribute("booking", booking);
        return "admin/bookings/detail";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, RedirectAttributes ra) {
        updateStatus(id, BookingStatus.CONFIRMED, ra, "Đã xác nhận đặt phòng");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        updateStatus(id, BookingStatus.CANCELLED, ra, "Đã hủy booking");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/{id}/checkin")
    public String checkIn(@PathVariable Long id, RedirectAttributes ra) {
        updateStatus(id, BookingStatus.CHECKED_IN, ra, "Đã check-in");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/{id}/checkout")
    public String checkOut(@PathVariable Long id, RedirectAttributes ra) {
        updateStatus(id, BookingStatus.CHECKED_OUT, ra, "Đã check-out");
        return "redirect:/admin/bookings";
    }

    private void updateStatus(Long id, BookingStatus status, RedirectAttributes ra, String message) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setStatus(status);
        bookingRepository.save(booking);
        ra.addFlashAttribute("success", message);
    }
}
