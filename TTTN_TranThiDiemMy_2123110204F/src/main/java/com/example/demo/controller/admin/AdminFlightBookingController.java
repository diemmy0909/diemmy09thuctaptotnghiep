package com.example.demo.controller.admin;

import com.example.demo.entity.FlightBooking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.repository.FlightBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/flight-bookings")
@RequiredArgsConstructor
public class AdminFlightBookingController {

    private final FlightBookingRepository flightBookingRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", flightBookingRepository.findAll());
        return "admin/flight-bookings/list";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
        FlightBooking booking = flightBookingRepository.findById(id).orElseThrow();
        booking.setStatus(status);
        flightBookingRepository.save(booking);
        return "redirect:/admin/flight-bookings";
    }
}
