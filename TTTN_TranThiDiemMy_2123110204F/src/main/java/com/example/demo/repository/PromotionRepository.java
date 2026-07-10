package com.example.demo.repository;

import com.example.demo.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    boolean existsByCode(String code);
    java.util.Optional<Promotion> findByCode(String code);
    List<Promotion> findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            java.time.LocalDate today, java.time.LocalDate today2);
}
