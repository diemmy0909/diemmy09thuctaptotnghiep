package com.example.demo.controller;

import com.example.demo.entity.Hotel;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class PublicReviewController {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @PostMapping("/danh-gia/khach-san")
    public String submitReview(
            @RequestParam("hotelId") Long hotelId,
            @RequestParam("rating") Integer rating,
            @RequestParam("comment") String comment,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication,
            RedirectAttributes ra
    ) {
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }

        try {
            User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
            Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new IllegalArgumentException("Khách sạn không tồn tại"));

            Review review = new Review();
            review.setUser(user);
            review.setHotel(hotel);
            review.setRating(rating != null ? rating : 5);
            review.setComment(comment);
            review.setApproved(false);

            if (image != null && !image.isEmpty()) {
                String imageUrl = fileStorageService.store(image, "reviews");
                review.setImageUrl(imageUrl);
            }

            reviewRepository.save(review);
            ra.addFlashAttribute("successMessage", "Cảm ơn bạn! Đánh giá của bạn đã được gửi và đang chờ duyệt.");

        } catch (IOException e) {
            ra.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tải ảnh lên. Vui lòng thử lại.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại.");
        }

        return "redirect:/khach-san/" + hotelId;
    }
}
