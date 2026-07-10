package com.example.demo.service;

import com.example.demo.entity.Flight;
import com.example.demo.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightSearchService {

    private final FlightRepository flightRepository;

    public List<Flight> search(String from, String to, LocalDate date,
                               BigDecimal minPrice, BigDecimal maxPrice,
                               String airline, String timeOfDay) {
        
        List<Flight> flights;
        if (from != null && to != null && date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            flights = flightRepository.searchFlights(from, to, startOfDay, endOfDay);
        } else {
            flights = flightRepository.findAll();
        }

        return flights.stream()
                .filter(f -> minPrice == null || f.getPrice().compareTo(minPrice) >= 0)
                .filter(f -> maxPrice == null || f.getPrice().compareTo(maxPrice) <= 0)
                .filter(f -> airline == null || airline.isBlank() || f.getAirline().equalsIgnoreCase(airline))
                .filter(f -> filterByTimeOfDay(f.getDepartureTime(), timeOfDay))
                .sorted(Comparator.comparing(Flight::getPrice))
                .toList();
    }

    private boolean filterByTimeOfDay(LocalDateTime departureTime, String timeOfDay) {
        if (timeOfDay == null || timeOfDay.isBlank()) return true;
        int hour = departureTime.getHour();
        return switch (timeOfDay) {
            case "morning" -> hour >= 5 && hour < 12;
            case "afternoon" -> hour >= 12 && hour < 18;
            case "evening" -> hour >= 18 && hour < 22;
            case "night" -> hour >= 22 || hour < 5;
            default -> true;
        };
    }
}
