package com.example.demo.controller;

import com.example.demo.entity.Hotel;
import com.example.demo.entity.User;
import com.example.demo.entity.Wishlist;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    @GetMapping("/yeu-thich-old")
    public String viewWishlist(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        List<Wishlist> wishlists = wishlistRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("pageTitle", "Khách sạn yêu thích");
        model.addAttribute("wishlists", wishlists);
        return "public/wishlist";
    }

    @PostMapping("/api/wishlist/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleWishlist(@RequestBody Map<String, Long> payload, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập"));
        }
        Long hotelId = payload.get("hotelId");
        if (hotelId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu hotelId"));
        }

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);

        if (hotel == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Khách sạn không tồn tại"));
        }

        var existing = wishlistRepository.findByUserAndHotel(user, hotel);
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return ResponseEntity.ok(Map.of("isSaved", false));
        } else {
            Wishlist w = new Wishlist();
            w.setUser(user);
            w.setHotel(hotel);
            wishlistRepository.save(w);
            return ResponseEntity.ok(Map.of("isSaved", true));
        }
    }
}
