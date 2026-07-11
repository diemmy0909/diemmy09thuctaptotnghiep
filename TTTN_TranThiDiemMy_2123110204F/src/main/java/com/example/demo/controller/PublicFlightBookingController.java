package com.example.demo.controller;

import com.example.demo.entity.Flight;
import com.example.demo.entity.User;
import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FlightBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dat-ve-may-bay")
@RequiredArgsConstructor
public class PublicFlightBookingController {

    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final FlightBookingService flightBookingService;

    @GetMapping
    public String form(@RequestParam Long flightId,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        User user = null;
        if (userDetails != null) {
            user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        model.addAttribute("flight", flight);
        model.addAttribute("user", user);
        model.addAttribute("passengers", 1);
        model.addAttribute("extraBaggageKg", 0);
        model.addAttribute("totalAmount", flight.getPrice());
        return "public/flight-booking/form";
    }

    @PostMapping
    public String submit(@RequestParam Long flightId,
                         @RequestParam Integer passengers,
                         @RequestParam(required = false) String seatNumbers,
                         @RequestParam Integer extraBaggageKg,
                         @RequestParam String fullName,
                         @RequestParam String phone,
                         @RequestParam String email,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes ra) {
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        // Cập nhật thông tin cá nhân nếu cần
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setEmail(email);
        userRepository.save(user);

        var booking = flightBookingService.createBooking(flightId, user.getId(), passengers, seatNumbers, extraBaggageKg);
        ra.addFlashAttribute("success", "Đặt vé máy bay thành công! Mã: " + booking.getBookingCode());
        
        // Cập nhật số ghế trống
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        if(flight.getAvailableSeats() >= passengers) {
            flight.setAvailableSeats(flight.getAvailableSeats() - passengers);
            flightRepository.save(flight);
        }

        // Chuyển hướng tới cổng thanh toán
        return "redirect:/thanh-toan-may-bay/" + booking.getId();
    }
}
