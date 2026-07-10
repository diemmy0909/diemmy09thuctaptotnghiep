package com.example.demo.controller;

import com.example.demo.entity.RoomType;
import com.example.demo.repository.AmenityRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.FavoriteHotelRepository;
import com.example.demo.entity.User;
import com.example.demo.service.HotelSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/khach-san")
@RequiredArgsConstructor
public class PublicHotelController {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;
    private final AmenityRepository amenityRepository;
    private final HotelSearchService hotelSearchService;
    private final UserRepository userRepository;
    private final FavoriteHotelRepository favoriteHotelRepository;

    @GetMapping
    public String list(@RequestParam(required = false) String location,
                       @RequestParam(required = false) Integer minStars,
                       @RequestParam(required = false) Integer maxStars,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       @RequestParam(required = false) Long amenityId,
                       @RequestParam(required = false) RoomType roomType,
                       @RequestParam(required = false) String checkIn,
                       @RequestParam(required = false) String checkOut,
                       @RequestParam(required = false) Integer guests,
                       @RequestParam(required = false) Double maxDistance,
                       @RequestParam(required = false) Integer minRating,
                       @RequestParam(defaultValue = "1") int page,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        var allHotels = hotelSearchService.search(location, minStars, maxStars, minPrice, maxPrice, amenityId, roomType, maxDistance, minRating);
        
        int pageSize = 9;
        int totalItems = allHotels.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        // Đảm bảo page hợp lệ
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        
        List<com.example.demo.entity.Hotel> pagedHotels = start <= totalItems && start >= 0 
                ? allHotels.subList(start, end) 
                : Collections.emptyList();
                
        // Lấy giá thấp nhất của mỗi khách sạn
        Map<Long, BigDecimal> hotelPrices = new HashMap<>();
        for (var h : pagedHotels) {
            var rooms = roomRepository.findByHotelId(h.getId());
            if (!rooms.isEmpty()) {
                BigDecimal minP = rooms.stream()
                        .filter(r -> r.getStatus() == com.example.demo.entity.RoomStatus.AVAILABLE)
                        .map(com.example.demo.entity.Room::getPrice)
                        .min(java.util.Comparator.naturalOrder())
                        .orElse(null);
                if (minP != null) {
                    hotelPrices.put(h.getId(), minP);
                }
            }
        }
                
        model.addAttribute("hotels", pagedHotels);
        model.addAttribute("hotelPrices", hotelPrices);
        
        List<Long> favoriteHotelIds = new java.util.ArrayList<>();
        if (userDetails != null) {
            userRepository.findByEmail(userDetails.getUsername()).ifPresent(user -> {
                favoriteHotelIds.addAll(favoriteHotelRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                        .stream().map(f -> f.getHotel().getId()).toList());
            });
        }
        model.addAttribute("favoriteHotelIds", favoriteHotelIds);
        
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("amenities", amenityRepository.findAll());
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("location", location);
        model.addAttribute("minStars", minStars);
        model.addAttribute("maxStars", maxStars);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("amenityId", amenityId);
        model.addAttribute("roomType", roomType);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("guests", guests);
        model.addAttribute("maxDistance", maxDistance);
        model.addAttribute("minRating", minRating);
        model.addAttribute("hotelSearchService", hotelSearchService);
        return "public/hotels/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var hotel = hotelRepository.findById(id).orElseThrow();
        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", roomRepository.findByHotelId(id));
        model.addAttribute("reviews", reviewRepository.findByHotelIdAndApprovedTrueOrderByCreatedAtDesc(id));
        model.addAttribute("minPrice", hotelSearchService.getMinPrice(hotel));
        return "public/hotels/detail";
    }
}
