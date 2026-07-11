package com.example.demo.controller;

import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketCategory;
import com.example.demo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/ve-tham-quan")
@RequiredArgsConstructor
public class PublicTicketController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String list(@RequestParam(required = false) TicketCategory category, 
                       @RequestParam(defaultValue = "1") int page,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        List<Ticket> allTickets;
        if (category != null) {
            allTickets = ticketRepository.findByCategory(category);
        } else {
            allTickets = ticketRepository.findAll();
        }

        int pageSize = 6;
        int totalItems = allTickets.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        
        List<Ticket> pagedTickets = start <= totalItems && start >= 0 
                ? allTickets.subList(start, end) 
                : new ArrayList<>();


        model.addAttribute("tickets", pagedTickets);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("categories", TicketCategory.values());
        model.addAttribute("selectedCategory", category);
        return "public/tickets/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        

        model.addAttribute("ticket", ticket);
        return "public/tickets/detail";
    }
}
