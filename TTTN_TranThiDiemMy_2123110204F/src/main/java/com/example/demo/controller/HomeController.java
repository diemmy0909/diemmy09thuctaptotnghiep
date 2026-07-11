package com.example.demo.controller;

import com.example.demo.entity.RoomStatus;
import com.example.demo.repository.*;
import com.example.demo.service.HotelSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.demo.entity.User;
import com.example.demo.entity.TourActivity;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final PromotionRepository promotionRepository;
    private final TourActivityRepository tourActivityRepository;
    private final TicketRepository ticketRepository;
    private final HotelSearchService hotelSearchService;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        var hotels = hotelRepository.findAll();
        var featuredHotels = hotels.stream()
                .sorted(Comparator.comparing(com.example.demo.entity.Hotel::getId).reversed())
                .limit(8).toList();
                
        // Lấy giá thấp nhất của mỗi khách sạn
        Map<Long, BigDecimal> hotelPrices = new HashMap<>();
        for (var h : featuredHotels) {
            var rooms = roomRepository.findByHotelId(h.getId());
            if (!rooms.isEmpty()) {
                BigDecimal minPrice = rooms.stream()
                        .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                        .map(com.example.demo.entity.Room::getPrice)
                        .min(Comparator.naturalOrder())
                        .orElse(null);
                if (minPrice != null) {
                    hotelPrices.put(h.getId(), minPrice);
                }
            }
        }
        
        model.addAttribute("featuredHotels", featuredHotels);
        model.addAttribute("hotelPrices", hotelPrices);
        model.addAttribute("promotions", promotionRepository
                .findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate.now(), LocalDate.now())
                .stream().sorted(Comparator.comparing(p -> p.getDiscountPercent() != null ? p.getDiscountPercent() : 0)).toList());
        model.addAttribute("tourActivities", tourActivityRepository.findTop4ByOrderByIdDesc());
        model.addAttribute("tickets", ticketRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 4, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"))
        ).getContent());
        return "public/home";
    }

    @GetMapping("/tim-kiem")
    public String searchRedirect(@RequestParam(required = false) String location,
                                 @RequestParam(required = false) String checkIn,
                                 @RequestParam(required = false) String checkOut,
                                 @RequestParam(required = false) Integer guests) {
        StringBuilder url = new StringBuilder("/khach-san?");
        if (location != null && !location.isEmpty()) {
            url.append("location=").append(URLEncoder.encode(location, StandardCharsets.UTF_8));
        }
        if (checkIn != null) url.append("&checkIn=").append(checkIn);
        if (checkOut != null) url.append("&checkOut=").append(checkOut);
        if (guests != null) url.append("&guests=").append(guests);
        return "redirect:" + url.toString();
    }

    @GetMapping("/hoat-dong-vui-choi")
    public String tourActivities(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("pageTitle", "Các Điểm Du Lịch Đáng Trải Nghiệm");
        model.addAttribute("activityPage", tourActivityRepository.findAll(org.springframework.data.domain.PageRequest.of(page, 8)));
        
        return "public/tour-activities";
    }

    @GetMapping("/hoat-dong-vui-choi/{id}")
    public String tourActivityDetail(@org.springframework.web.bind.annotation.PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        com.example.demo.entity.TourActivity activity = tourActivityRepository.findById(id).orElse(null);
        if (activity == null) {
            return "redirect:/hoat-dong-vui-choi";
        }
        model.addAttribute("pageTitle", activity.getTitle());
        model.addAttribute("activity", activity);
        
        return "public/tour-activity-detail";
    }
}
