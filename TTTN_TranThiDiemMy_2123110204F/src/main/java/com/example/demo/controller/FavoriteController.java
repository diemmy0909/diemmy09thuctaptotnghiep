package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.FavoriteFlightRepository;
import com.example.demo.repository.FavoriteHotelRepository;
import com.example.demo.repository.FavoriteTicketRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/yeu-thich")
@RequiredArgsConstructor
public class FavoriteController {

    private final UserRepository userRepository;
    private final FavoriteHotelRepository favoriteHotelRepository;
    private final FavoriteFlightRepository favoriteFlightRepository;
    private final FavoriteTicketRepository favoriteTicketRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/dang-nhap";
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        
        model.addAttribute("favoriteHotels", favoriteHotelRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
        model.addAttribute("favoriteFlights", favoriteFlightRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
        model.addAttribute("favoriteTickets", favoriteTicketRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
        
        return "public/favorites";
    }
}
