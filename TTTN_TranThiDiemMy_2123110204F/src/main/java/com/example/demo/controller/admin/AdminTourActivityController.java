package com.example.demo.controller.admin;

import com.example.demo.entity.TourActivity;
import com.example.demo.repository.TourActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.service.FileStorageService;
import java.io.IOException;

@Controller
@RequestMapping("/admin/activities")
@RequiredArgsConstructor
public class AdminTourActivityController {

    private final TourActivityRepository tourActivityRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String listActivities(@RequestParam(defaultValue = "1") int page, Model model) {
        model.addAttribute("pageTitle", "Quản lý Hoạt động du lịch");
        model.addAttribute("activeMenu", "activities");
        
        java.util.List<TourActivity> allActivities = tourActivityRepository.findAll();
        
        int pageSize = 5;
        int totalItems = allActivities.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        
        java.util.List<TourActivity> pagedActivities = start <= totalItems && start >= 0 
                ? allActivities.subList(start, end) 
                : java.util.Collections.emptyList();
                
        model.addAttribute("activities", pagedActivities);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "admin/activities/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "Thêm Hoạt động du lịch");
        model.addAttribute("activeMenu", "activities");
        model.addAttribute("activity", new TourActivity());
        return "admin/activities/form";
    }

    @PostMapping("/create")
    public String createActivity(@ModelAttribute TourActivity activity, 
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileStorageService.store(imageFile, "activities");
                activity.setImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tourActivityRepository.save(activity);
        redirectAttributes.addFlashAttribute("success", "Đã thêm hoạt động thành công!");
        return "redirect:/admin/activities";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        TourActivity activity = tourActivityRepository.findById(id).orElseThrow();
        model.addAttribute("pageTitle", "Sửa Hoạt động du lịch");
        model.addAttribute("activeMenu", "activities");
        model.addAttribute("activity", activity);
        return "admin/activities/form";
    }

    @PostMapping("/edit/{id}")
    public String editActivity(@PathVariable Long id, 
                               @ModelAttribute TourActivity activity, 
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               RedirectAttributes redirectAttributes) {
        TourActivity existingActivity = tourActivityRepository.findById(id).orElseThrow();
        activity.setId(id);
        activity.setImageUrl(existingActivity.getImageUrl()); // Giữ lại ảnh cũ mặc định
        
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                fileStorageService.deleteIfLocal(existingActivity.getImageUrl());
                String imageUrl = fileStorageService.store(imageFile, "activities");
                activity.setImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tourActivityRepository.save(activity);
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật hoạt động thành công!");
        return "redirect:/admin/activities";
    }

    @GetMapping("/delete/{id}")
    public String deleteActivity(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        TourActivity activity = tourActivityRepository.findById(id).orElseThrow();
        fileStorageService.deleteIfLocal(activity.getImageUrl());
        tourActivityRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa hoạt động thành công!");
        return "redirect:/admin/activities";
    }
}
