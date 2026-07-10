package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/uu-dai")
public class PublicPromoController {

    @GetMapping
    public String promosPage(Model model) {
        return "public/promos";
    }
}
