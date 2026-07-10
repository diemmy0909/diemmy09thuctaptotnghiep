package com.example.demo.controller;

import com.example.demo.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/khuyen-mai")
@RequiredArgsConstructor
public class PublicPromotionController {

    private final PromotionRepository promotionRepository;

    @GetMapping
    public String promotionsPage(Model model) {
        LocalDate today = LocalDate.now();
        var activePromotions = promotionRepository.findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
                .stream().sorted(java.util.Comparator.comparing(p -> p.getDiscountPercent() != null ? p.getDiscountPercent() : 0)).toList();
        model.addAttribute("promotions", activePromotions);
        model.addAttribute("activeMenu", "promotions");
        return "public/promotions";
    }
}
