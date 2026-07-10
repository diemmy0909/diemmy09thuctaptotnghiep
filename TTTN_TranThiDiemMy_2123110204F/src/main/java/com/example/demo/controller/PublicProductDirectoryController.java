package com.example.demo.controller;

import com.example.demo.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tat-ca-san-pham")
@RequiredArgsConstructor
public class PublicProductDirectoryController {

    private final HotelRepository hotelRepository;
    private final com.example.demo.repository.RoomRepository roomRepository;

    @GetMapping
    public String productDirectoryPage(@RequestParam(defaultValue = "1") int page, Model model) {
        var allHotels = hotelRepository.findAll();
        
        int pageSize = 10;
        int totalItems = allHotels.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
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
                BigDecimal minPrice = rooms.stream()
                        .filter(r -> r.getStatus() == com.example.demo.entity.RoomStatus.AVAILABLE)
                        .map(com.example.demo.entity.Room::getPrice)
                        .min(java.util.Comparator.naturalOrder())
                        .orElse(null);
                if (minPrice != null) {
                    hotelPrices.put(h.getId(), minPrice);
                }
            }
        }
                
        model.addAttribute("hotels", pagedHotels);
        model.addAttribute("hotelPrices", hotelPrices);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "public/all-products";
    }
}
