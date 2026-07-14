package com.example.demo.controller.admin;

import com.example.demo.entity.Meal;
import com.example.demo.repository.MealRepository;
import com.example.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/meals")
@RequiredArgsConstructor
public class AdminMealController {

    private final MealRepository mealRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("meals", mealRepository.findAll());
        model.addAttribute("activeMenu", "meals");
        model.addAttribute("pageTitle", "Quản lý Suất ăn");
        return "admin/meals/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("meal", new Meal());
        model.addAttribute("activeMenu", "meals");
        model.addAttribute("pageTitle", "Thêm Suất ăn mới");
        return "admin/meals/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Meal meal, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileStorageService.store(imageFile, "meals");
                meal.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi tải ảnh: " + e.getMessage());
            return "redirect:/admin/meals/create";
        }
        mealRepository.save(meal);
        redirectAttributes.addFlashAttribute("success", "Thêm suất ăn thành công!");
        return "redirect:/admin/meals";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Meal meal = mealRepository.findById(id).orElseThrow();
        model.addAttribute("meal", meal);
        model.addAttribute("activeMenu", "meals");
        model.addAttribute("pageTitle", "Sửa Suất ăn");
        return "admin/meals/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Meal meal, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Meal existingMeal = mealRepository.findById(id).orElseThrow();
        meal.setId(id);
        meal.setImageUrl(existingMeal.getImageUrl()); // Giữ ảnh cũ mặc định
        
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileStorageService.store(imageFile, "meals");
                fileStorageService.deleteIfLocal(existingMeal.getImageUrl()); // Xóa ảnh cũ
                meal.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi tải ảnh: " + e.getMessage());
            return "redirect:/admin/meals/edit/" + id;
        }

        mealRepository.save(meal);
        redirectAttributes.addFlashAttribute("success", "Cập nhật suất ăn thành công!");
        return "redirect:/admin/meals";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Meal meal = mealRepository.findById(id).orElse(null);
        if (meal != null) {
            fileStorageService.deleteIfLocal(meal.getImageUrl());
            mealRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa suất ăn thành công!");
        }
        return "redirect:/admin/meals";
    }
}
