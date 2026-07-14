package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.repository.FlightRepository;
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
    private final UserRepository userRepository;
    private final com.example.demo.repository.MealRepository mealRepository;

    @GetMapping
    public String flightPage(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String tripType,
            @RequestParam(required = false) String returnDate,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String airline,
            @RequestParam(required = false) String timeOfDay,
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        LocalDate searchDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        var allFlights = flightSearchService.search(from, to, searchDate, minPrice, maxPrice, airline, timeOfDay);
        
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
                

        model.addAttribute("flights", pagedFlights);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("date", date);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("tripType", tripType);
        model.addAttribute("returnDate", returnDate);
        model.addAttribute("airline", airline);
        model.addAttribute("timeOfDay", timeOfDay);
        model.addAttribute("hasSearched", from != null && to != null && date != null);
        
        // Lấy danh sách hãng bay duy nhất cho bộ lọc
        model.addAttribute("airlines", flightRepository.findAll().stream().map(com.example.demo.entity.Flight::getAirline).distinct().toList());
        model.addAttribute("activeMenu", "flights");
        
        return "public/flights";
    }

    @GetMapping("/{id}")
    public String flightDetail(@PathVariable Long id, 
                               @RequestParam(required = false) String tripType,
                               @RequestParam(required = false) String returnDate,
                               @RequestParam(required = false) String returnFrom,
                               @RequestParam(required = false) String returnTo,
                               Model model) {
        com.example.demo.entity.Flight flight = flightRepository.findById(id).orElseThrow();
        
        List<com.example.demo.entity.Meal> meals = mealRepository.findAll();
        
        model.addAttribute("flight", flight);
        model.addAttribute("meals", meals);
        model.addAttribute("tripType", tripType);
        model.addAttribute("returnDate", returnDate);
        model.addAttribute("returnFrom", returnFrom);
        model.addAttribute("returnTo", returnTo);
        model.addAttribute("pageTitle", flight.getAirline() + " - " + flight.getFlightNumber());
        return "public/flight-detail";
    }

    @GetMapping("/chuyen-ve")
    public String returnFlightPage(
            @RequestParam("outboundFlightId") Long outboundFlightId,
            @RequestParam("outboundSeatClass") String outboundSeatClass,
            @RequestParam(value = "outboundMealOption", required = false) String outboundMealOption,
            @RequestParam("returnFrom") String returnFrom,
            @RequestParam("returnTo") String returnTo,
            @RequestParam("returnDate") String returnDate,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model) {
            
        LocalDate searchDate = (returnDate != null && !returnDate.isEmpty()) ? LocalDate.parse(returnDate) : null;
        var allFlights = flightSearchService.search(returnFrom, returnTo, searchDate, null, null, null, null);
        
        int pageSize = 5;
        int totalItems = allFlights.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        var pagedFlights = allFlights.subList(start, end);
        
        model.addAttribute("flights", pagedFlights);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        model.addAttribute("from", returnFrom);
        model.addAttribute("to", returnTo);
        model.addAttribute("date", returnDate);
        model.addAttribute("tripType", "ROUND_TRIP_RETURN");
        
        // Pass outbound parameters down
        model.addAttribute("outboundFlightId", outboundFlightId);
        model.addAttribute("outboundSeatClass", outboundSeatClass);
        model.addAttribute("outboundMealOption", outboundMealOption);
        
        model.addAttribute("pageTitle", "Chọn chuyến bay về");
        return "public/return-flights";
    }
}
