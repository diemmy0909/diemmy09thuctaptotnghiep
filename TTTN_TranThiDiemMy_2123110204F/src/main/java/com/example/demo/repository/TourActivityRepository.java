package com.example.demo.repository;

import com.example.demo.entity.TourActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourActivityRepository extends JpaRepository<TourActivity, Long> {

    List<TourActivity> findTop4ByOrderByIdDesc();
}
