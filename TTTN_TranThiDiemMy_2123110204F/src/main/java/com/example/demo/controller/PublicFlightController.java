package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.FavoriteFlightRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FlightSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/chuyen-bay")
@RequiredArgsConstructor
public class PublicFlightController {

    private final FlightSearchService flightSearchService;
    private final FlightRepository flightRepository;
    private final FavoriteFlightRepository favoriteFlightRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String flightPage(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String airline,
            @RequestParam(required = false) String timeOfDay,
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        var allFlights = flightSearchService.search(from, to, date, minPrice, maxPrice, airline, timeOfDay);
        
        int pageSize = 5;
        int totalItems = allFlights.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        
        List<com.example.demo.entity.Flight> pagedFlights = start <= totalItems && start >= 0 
                ? allFlights.subList(start, end) 
                : java.util.Collections.emptyList();
                
        List<Long> favoriteFlightIds = new java.util.ArrayList<>();
        if (userDetails != null) {
            userRepository.findByEmail(userDetails.getUsername()).ifPresent(user -> {
                favoriteFlightIds.addAll(favoriteFlightRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                        .stream().map(f -> f.getFlight().getId()).toList());
            });
        }
        model.addAttribute("favoriteFlightIds", favoriteFlightIds);
                
        model.addAttribute("flights", pagedFlights);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("date", date);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("airline", airline);
        model.addAttribute("timeOfDay", timeOfDay);
        model.addAttribute("hasSearched", from != null && to != null && date != null);
        
        // Lấy danh sách hãng bay duy nhất cho bộ lọc
        model.addAttribute("airlines", flightRepository.findAll().stream().map(com.example.demo.entity.Flight::getAirline).distinct().toList());
        
        return "public/flights";
    }
}
