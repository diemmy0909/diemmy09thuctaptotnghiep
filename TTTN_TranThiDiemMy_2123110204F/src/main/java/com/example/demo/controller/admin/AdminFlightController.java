package com.example.demo.controller.admin;

import com.example.demo.entity.Flight;
import com.example.demo.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/flights")
@RequiredArgsConstructor
public class AdminFlightController {

    private final FlightRepository flightRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 10;
        org.springframework.data.domain.Page<Flight> flightPage = flightRepository.findAll(
                org.springframework.data.domain.PageRequest.of(page - 1, pageSize, org.springframework.data.domain.Sort.by("departureTime").descending())
        );
        model.addAttribute("flights", flightPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", flightPage.getTotalPages());
        model.addAttribute("activeMenu", "flights");
        model.addAttribute("pageTitle", "Quản lý Chuyến bay");
        return "admin/flights/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("flight", new Flight());
        model.addAttribute("activeMenu", "flights");
        model.addAttribute("pageTitle", "Thêm chuyến bay");
        return "admin/flights/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Flight flight, RedirectAttributes redirectAttributes) {
        flightRepository.save(flight);
        redirectAttributes.addFlashAttribute("success", "Thêm chuyến bay thành công!");
        return "redirect:/admin/flights";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Flight flight = flightRepository.findById(id).orElseThrow();
        model.addAttribute("flight", flight);
        model.addAttribute("activeMenu", "flights");
        model.addAttribute("pageTitle", "Sửa chuyến bay");
        return "admin/flights/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Flight flight, RedirectAttributes redirectAttributes) {
        flight.setId(id);
        flightRepository.save(flight);
        redirectAttributes.addFlashAttribute("success", "Cập nhật chuyến bay thành công!");
        return "redirect:/admin/flights";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        flightRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Xóa chuyến bay thành công!");
        return "redirect:/admin/flights";
    }
}
