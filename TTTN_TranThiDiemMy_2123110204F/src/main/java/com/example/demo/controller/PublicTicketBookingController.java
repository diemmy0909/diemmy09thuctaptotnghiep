package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TicketBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class PublicTicketBookingController {

    private final TicketBookingService ticketBookingService;
    private final UserRepository userRepository;

    @GetMapping("/dat-ve-tour")
    public String submitBooking(@RequestParam Long ticketId,
                                @RequestParam LocalDate date,
                                @RequestParam Integer adultCount,
                                @RequestParam Integer childCount,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes ra) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        
        var booking = ticketBookingService.createBooking(ticketId, user.getId(), date, adultCount, childCount);
        
        return "redirect:/thanh-toan-tour/" + booking.getId();
    }
}
