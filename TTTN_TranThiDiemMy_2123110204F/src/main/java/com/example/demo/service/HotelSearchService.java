package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HotelSearchService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;


    public List<Hotel> search(String location, Integer minStars, Integer maxStars,
                               BigDecimal minPrice, BigDecimal maxPrice,
                               Long amenityId, RoomType roomType, Double maxDistance, Integer minRating) {
        List<Hotel> hotels = hotelRepository.findAll();

        return hotels.stream()
                .filter(h -> location == null || location.isBlank()
                        || (h.getAddress() != null && h.getAddress().toLowerCase().contains(location.toLowerCase()))
                        || h.getName().toLowerCase().contains(location.toLowerCase()))
                .filter(h -> minStars == null || (h.getStarRating() != null && h.getStarRating() >= minStars))
                .filter(h -> maxStars == null || (h.getStarRating() != null && h.getStarRating() <= maxStars))
                .filter(h -> amenityId == null || h.getAmenities().stream().anyMatch(a -> a.getId().equals(amenityId)))
                .filter(h -> maxDistance == null || (h.getDistanceToCenter() != null && h.getDistanceToCenter() <= maxDistance))

                .filter(h -> matchesPriceAndRoomType(h, minPrice, maxPrice, roomType))
                .sorted(Comparator.comparing(Hotel::getStarRating, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private boolean matchesPriceAndRoomType(Hotel hotel, BigDecimal minPrice, BigDecimal maxPrice, RoomType roomType) {
        List<Room> rooms = roomRepository.findByHotelId(hotel.getId());
        Stream<Room> stream = rooms.stream().filter(r -> r.getStatus() == RoomStatus.AVAILABLE);
        if (roomType != null) stream = stream.filter(r -> r.getRoomType() == roomType);
        if (minPrice != null) stream = stream.filter(r -> r.getPrice().compareTo(minPrice) >= 0);
        if (maxPrice != null) stream = stream.filter(r -> r.getPrice().compareTo(maxPrice) <= 0);
        if (minPrice != null || maxPrice != null || roomType != null) {
            return stream.findAny().isPresent();
        }
        return true;
    }

    public BigDecimal getMinPrice(Hotel hotel) {
        return roomRepository.findByHotelId(hotel.getId()).stream()
                .map(Room::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
