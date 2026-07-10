package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteRestController {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final FavoriteHotelRepository favoriteHotelRepository;
    private final FlightRepository flightRepository;
    private final FavoriteFlightRepository favoriteFlightRepository;
    private final TicketRepository ticketRepository;
    private final FavoriteTicketRepository favoriteTicketRepository;

    @PostMapping("/hotels/{id}")
    public ResponseEntity<?> toggleFavoriteHotel(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập"));
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Hotel hotel = hotelRepository.findById(id).orElseThrow();

        var existing = favoriteHotelRepository.findByUserIdAndHotelId(user.getId(), hotel.getId());
        Map<String, Object> response = new HashMap<>();
        
        if (existing.isPresent()) {
            favoriteHotelRepository.delete(existing.get());
            response.put("status", "removed");
        } else {
            favoriteHotelRepository.save(FavoriteHotel.builder().user(user).hotel(hotel).build());
            response.put("status", "added");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/flights/{id}")
    public ResponseEntity<?> toggleFavoriteFlight(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập"));
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Flight flight = flightRepository.findById(id).orElseThrow();

        var existing = favoriteFlightRepository.findByUserIdAndFlightId(user.getId(), flight.getId());
        Map<String, Object> response = new HashMap<>();
        
        if (existing.isPresent()) {
            favoriteFlightRepository.delete(existing.get());
            response.put("status", "removed");
        } else {
            favoriteFlightRepository.save(FavoriteFlight.builder().user(user).flight(flight).build());
            response.put("status", "added");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets/{id}")
    public ResponseEntity<?> toggleFavoriteTicket(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập"));
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Ticket ticket = ticketRepository.findById(id).orElseThrow();

        var existing = favoriteTicketRepository.findByUserIdAndTicketId(user.getId(), ticket.getId());
        Map<String, Object> response = new HashMap<>();
        
        if (existing.isPresent()) {
            favoriteTicketRepository.delete(existing.get());
            response.put("status", "removed");
        } else {
            favoriteTicketRepository.save(FavoriteTicket.builder().user(user).ticket(ticket).build());
            response.put("status", "added");
        }
        return ResponseEntity.ok(response);
    }
}
