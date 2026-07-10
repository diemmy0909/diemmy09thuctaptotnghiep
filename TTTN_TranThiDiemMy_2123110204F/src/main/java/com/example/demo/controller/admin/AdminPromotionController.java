package com.example.demo.controller.admin;

import com.example.demo.entity.Promotion;
import com.example.demo.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import com.example.demo.service.NotificationService;
import com.example.demo.entity.NotificationType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final PromotionRepository promotionRepository;
    private final NotificationService notificationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Quản lý khuyến mãi");
        model.addAttribute("activeMenu", "promotions");
        model.addAttribute("promotions", promotionRepository.findAll());
        return "admin/promotions/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("pageTitle", "Tạo mã giảm giá");
        model.addAttribute("activeMenu", "promotions");
        model.addAttribute("promotion", new Promotion());
        return "admin/promotions/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Sửa voucher");
        model.addAttribute("activeMenu", "promotions");
        model.addAttribute("promotion", promotionRepository.findById(id).orElseThrow());
        return "admin/promotions/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Promotion promotion,
                       @RequestParam(required = false, defaultValue = "false") boolean active,
                       @RequestParam(required = false, defaultValue = "false") boolean sendNotification,
                       RedirectAttributes ra) {
        promotion.setActive(active);
        promotionRepository.save(promotion);
        
        if (sendNotification) {
            notificationService.createGlobalNotification(
                "Khuyến mãi mới: " + promotion.getCode(),
                promotion.getDescription() + ". Nhập mã " + promotion.getCode() + " để được giảm giá " + promotion.getDiscountPercent() + "%.",
                NotificationType.PROMOTION
            );
        }
        
        ra.addFlashAttribute("success", "Đã lưu khuyến mãi");
        return "redirect:/admin/promotions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        promotionRepository.deleteById(id);
        ra.addFlashAttribute("success", "Đã xóa khuyến mãi");
        return "redirect:/admin/promotions";
    }
}
