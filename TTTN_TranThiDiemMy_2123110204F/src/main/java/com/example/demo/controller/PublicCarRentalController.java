package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/thue-xe")
@RequiredArgsConstructor
public class PublicCarRentalController {

    private final CarRepository carRepository;

    @GetMapping
    public String carRentalPage(Model model) {
        model.addAttribute("cars", carRepository.findAll());
        model.addAttribute("activeMenu", "car_rentals");
        return "public/car-rentals";
    }
}
