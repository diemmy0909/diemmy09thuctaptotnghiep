package com.example.demo.controller;

import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.FlightBookingRepository;
import com.example.demo.repository.TicketBookingRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TicketBookingRepository ticketBookingRepository;
    private final FlightBookingRepository flightBookingRepository;

    @GetMapping("/ho-so")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()));
        return "public/profile";
    }

    @PostMapping("/ho-so")
    public String update(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam String fullName,
                         @RequestParam String phone,
                         RedirectAttributes ra) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        user.setFullName(fullName);
        user.setPhone(phone);
        userRepository.save(user);
        ra.addFlashAttribute("success", "Đã cập nhật thông tin");
        return "redirect:/ho-so";
    }

    @GetMapping("/lich-su-dat-phong")
    public String bookingHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()));
        return "public/booking/history";
    }

    @PostMapping("/lich-su-dat-phong/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var booking = bookingRepository.findById(id).orElseThrow();
        if (!booking.getCustomer().getId().equals(user.getId())) {
            ra.addFlashAttribute("error", "Không có quyền hủy booking này.");
            return "redirect:/lich-su-dat-phong";
        }
        if (booking.getStatus() == com.example.demo.entity.BookingStatus.PENDING || booking.getStatus() == com.example.demo.entity.BookingStatus.CONFIRMED) {
            booking.setStatus(com.example.demo.entity.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            ra.addFlashAttribute("success", "Đã hủy đặt phòng thành công.");
        } else {
            ra.addFlashAttribute("error", "Không thể hủy phòng ở trạng thái hiện tại.");
        }
        return "redirect:/lich-su-dat-phong";
    }

    @GetMapping("/lich-su-dat-ve")
    public String flightBookingHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("flightBookings", flightBookingRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()));
        return "public/flight-booking/history";
    }

    @PostMapping("/lich-su-dat-ve/{id}/cancel")
    public String cancelFlightBooking(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var booking = flightBookingRepository.findById(id).orElseThrow();
        if (!booking.getCustomer().getId().equals(user.getId())) {
            ra.addFlashAttribute("error", "Không có quyền hủy vé này.");
            return "redirect:/lich-su-dat-ve";
        }
        if (booking.getStatus() == com.example.demo.entity.BookingStatus.PENDING || booking.getStatus() == com.example.demo.entity.BookingStatus.CONFIRMED) {
            booking.setStatus(com.example.demo.entity.BookingStatus.CANCELLED);
            flightBookingRepository.save(booking);
            ra.addFlashAttribute("success", "Đã hủy vé máy bay thành công.");
        } else {
            ra.addFlashAttribute("error", "Không thể hủy vé ở trạng thái hiện tại.");
        }
        return "redirect:/lich-su-dat-ve";
    }

    @GetMapping("/lich-su-dat-ve-tour")
    public String ticketBookingHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("ticketBookings", ticketBookingRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()));
        return "public/tickets/history";
    }

    @PostMapping("/lich-su-dat-ve-tour/{id}/cancel")
    public String cancelTicketBooking(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var booking = ticketBookingRepository.findById(id).orElseThrow();
        if (!booking.getCustomer().getId().equals(user.getId())) {
            ra.addFlashAttribute("error", "Không có quyền hủy vé này.");
            return "redirect:/lich-su-dat-ve-tour";
        }
        if (booking.getStatus() == com.example.demo.entity.BookingStatus.PENDING || booking.getStatus() == com.example.demo.entity.BookingStatus.CONFIRMED) {
            booking.setStatus(com.example.demo.entity.BookingStatus.CANCELLED);
            ticketBookingRepository.save(booking);
            ra.addFlashAttribute("success", "Đã hủy đặt vé thành công.");
        } else {
            ra.addFlashAttribute("error", "Không thể hủy vé ở trạng thái hiện tại.");
        }
        return "redirect:/lich-su-dat-ve-tour";
    }
}
