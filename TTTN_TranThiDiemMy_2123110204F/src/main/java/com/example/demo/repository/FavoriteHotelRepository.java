package com.example.demo.repository;

import com.example.demo.entity.FavoriteHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteHotelRepository extends JpaRepository<FavoriteHotel, Long> {
    List<FavoriteHotel> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<FavoriteHotel> findByUserIdAndHotelId(Long userId, Long hotelId);
    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);
}
