package com.example.demo.controller.admin;

import com.example.demo.entity.Hotel;
import com.example.demo.repository.AmenityRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

@Controller
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
public class AdminHotelController {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, 
                       @AuthenticationPrincipal UserDetails userDetails, 
                       Model model) {
        model.addAttribute("pageTitle", "Quản lý khách sạn");
        model.addAttribute("activeMenu", "hotels");
        
        List<Hotel> allHotels;
        boolean isPartner = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTNER"));
        
        if (isPartner) {
            User partner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            allHotels = hotelRepository.findByPartnerId(partner.getId());
        } else {
            allHotels = hotelRepository.findAll();
        }
        
        int pageSize = 10;
        int totalItems = allHotels.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        
        List<Hotel> pagedHotels = start <= totalItems && start >= 0 
                ? allHotels.subList(start, end) 
                : Collections.emptyList();
                
        model.addAttribute("hotels", pagedHotels);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "admin/hotels/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("pageTitle", "Thêm khách sạn");
        model.addAttribute("activeMenu", "hotels");
        model.addAttribute("hotel", new Hotel());
        model.addAttribute("amenities", amenityRepository.findAll());
        return "admin/hotels/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow();
        model.addAttribute("pageTitle", hotel.getName());
        model.addAttribute("activeMenu", "hotels");
        model.addAttribute("hotel", hotel);
        return "admin/hotels/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow();
        model.addAttribute("pageTitle", "Sửa khách sạn");
        model.addAttribute("activeMenu", "hotels");
        model.addAttribute("hotel", hotel);
        model.addAttribute("amenities", amenityRepository.findAll());
        return "admin/hotels/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String address,
                       @RequestParam(required = false) String description,
                       @RequestParam(required = false, defaultValue = "3") Integer starRating,
                       @RequestParam(required = false) Set<Long> amenityIds,
                       @RequestParam(required = false) MultipartFile imageFile,
                       @AuthenticationPrincipal UserDetails userDetails,
                       RedirectAttributes ra) {
        try {
            Hotel hotel = id != null
                    ? hotelRepository.findById(id).orElseThrow()
                    : new Hotel();

            hotel.setName(name);
            hotel.setAddress(address);
            hotel.setDescription(description);
            hotel.setStarRating(starRating);

            if (amenityIds != null && !amenityIds.isEmpty()) {
                hotel.setAmenities(new HashSet<>(amenityRepository.findAllById(amenityIds)));
            } else {
                hotel.setAmenities(new HashSet<>());
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                fileStorageService.deleteIfLocal(hotel.getImageUrl());
                hotel.setImageUrl(fileStorageService.store(imageFile, "hotels"));
            }

            // Assign partner if it's a new hotel created by a PARTNER
            if (id == null) {
                boolean isPartner = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTNER"));
                if (isPartner) {
                    User partner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
                    hotel.setPartner(partner);
                }
            }

            hotelRepository.save(hotel);
            ra.addFlashAttribute("success", "Đã lưu khách sạn thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return id != null ? "redirect:/admin/hotels/" + id + "/edit" : "redirect:/admin/hotels/new";
        }
        return "redirect:/admin/hotels";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Hotel hotel = hotelRepository.findById(id).orElseThrow();
            fileStorageService.deleteIfLocal(hotel.getImageUrl());
            hotelRepository.deleteById(id);
            ra.addFlashAttribute("success", "Đã xóa khách sạn");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Không thể xóa khách sạn vì đang có phòng hoặc giao dịch liên kết!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/hotels";
    }
}
