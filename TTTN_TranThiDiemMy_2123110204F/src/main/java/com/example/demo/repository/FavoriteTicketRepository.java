package com.example.demo.repository;

import com.example.demo.entity.FavoriteTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteTicketRepository extends JpaRepository<FavoriteTicket, Long> {
    List<FavoriteTicket> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<FavoriteTicket> findByUserIdAndTicketId(Long userId, Long ticketId);
    boolean existsByUserIdAndTicketId(Long userId, Long ticketId);
}
