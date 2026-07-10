package com.example.demo.controller.admin;

import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketCategory;
import com.example.demo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.service.FileStorageService;

import java.io.IOException;

@Controller
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {

    private final TicketRepository ticketRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tickets", ticketRepository.findAll());
        return "admin/tickets/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("categories", TicketCategory.values());
        return "admin/tickets/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", ticketRepository.findById(id).orElseThrow());
        model.addAttribute("categories", TicketCategory.values());
        return "admin/tickets/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Ticket ticket, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imgUrl = fileStorageService.store(imageFile, "tickets");
                ticket.setImageUrl(imgUrl);
            } catch (IOException e) {
                // Ignore or handle
            }
        }
        ticketRepository.save(ticket);
        return "redirect:/admin/tickets";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        ticketRepository.deleteById(id);
        return "redirect:/admin/tickets";
    }
}
