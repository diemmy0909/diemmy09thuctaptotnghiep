package com.example.demo.repository;

import com.example.demo.entity.Hotel;
import com.example.demo.entity.User;
import com.example.demo.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserOrderByCreatedAtDesc(User user);
    Optional<Wishlist> findByUserAndHotel(User user, Hotel hotel);
    boolean existsByUserAndHotel(User user, Hotel hotel);
}
