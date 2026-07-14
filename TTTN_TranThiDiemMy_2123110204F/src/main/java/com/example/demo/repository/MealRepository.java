package com.example.demo.repository;

import com.example.demo.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByActiveTrueOrderByNameAsc();
    Optional<Meal> findByName(String name);
}
