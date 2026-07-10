package com.example.demo.controller.admin;

import com.example.demo.entity.Amenity;
import com.example.demo.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/amenities")
@RequiredArgsConstructor
public class AdminAmenityController {

    private final AmenityRepository amenityRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Quản lý tiện nghi");
        model.addAttribute("activeMenu", "amenities");
        model.addAttribute("amenities", amenityRepository.findAll());
        return "admin/amenities/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("pageTitle", "Thêm tiện nghi");
        model.addAttribute("activeMenu", "amenities");
        model.addAttribute("amenity", new Amenity());
        return "admin/amenities/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Sửa tiện nghi");
        model.addAttribute("activeMenu", "amenities");
        model.addAttribute("amenity", amenityRepository.findById(id).orElseThrow());
        return "admin/amenities/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Amenity amenity, RedirectAttributes ra) {
        amenityRepository.save(amenity);
        ra.addFlashAttribute("success", "Đã lưu tiện nghi");
        return "redirect:/admin/amenities";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        amenityRepository.deleteById(id);
        ra.addFlashAttribute("success", "Đã xóa tiện nghi");
        return "redirect:/admin/amenities";
    }
}
