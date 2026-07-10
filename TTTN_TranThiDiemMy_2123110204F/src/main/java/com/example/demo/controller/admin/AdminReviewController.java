package com.example.demo.controller.admin;

import com.example.demo.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewRepository reviewRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Quản lý đánh giá");
        model.addAttribute("activeMenu", "reviews");
        model.addAttribute("reviews", reviewRepository.findAll());
        return "admin/reviews/list";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        var review = reviewRepository.findById(id).orElseThrow();
        review.setApproved(true);
        reviewRepository.save(review);
        ra.addFlashAttribute("success", "Đã duyệt đánh giá");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        reviewRepository.deleteById(id);
        ra.addFlashAttribute("success", "Đã xóa bình luận");
        return "redirect:/admin/reviews";
    }
}
