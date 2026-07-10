package com.example.demo.repository;

import com.example.demo.entity.FavoriteFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteFlightRepository extends JpaRepository<FavoriteFlight, Long> {
    List<FavoriteFlight> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<FavoriteFlight> findByUserIdAndFlightId(Long userId, Long flightId);
    boolean existsByUserIdAndFlightId(Long userId, Long flightId);
}
