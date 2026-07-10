package com.example.demo.controller.admin;

import com.example.demo.entity.Room;
import com.example.demo.entity.RoomStatus;
import com.example.demo.entity.RoomType;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

@Controller
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    @GetMapping
    public String listHotels(@RequestParam(defaultValue = "1") int page, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        model.addAttribute("pageTitle", "Chọn khách sạn để quản lý phòng");
        model.addAttribute("activeMenu", "rooms");
        
        java.util.List<com.example.demo.entity.Hotel> allHotels;
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
        
        java.util.List<com.example.demo.entity.Hotel> pagedHotels = start <= totalItems && start >= 0 
                ? allHotels.subList(start, end) 
                : java.util.Collections.emptyList();
                
        model.addAttribute("hotels", pagedHotels);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "admin/rooms/hotel-list";
    }

    @GetMapping("/hotel/{hotelId}")
    public String listRooms(@PathVariable Long hotelId, @RequestParam(defaultValue = "1") int page, Model model) {
        var hotel = hotelRepository.findById(hotelId).orElseThrow();
        model.addAttribute("pageTitle", "Quản lý phòng - " + hotel.getName());
        model.addAttribute("activeMenu", "rooms");
        
        var allRooms = roomRepository.findByHotelId(hotelId);
        
        int pageSize = 10;
        int totalItems = allRooms.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        
        java.util.List<Room> pagedRooms = start <= totalItems && start >= 0 
                ? allRooms.subList(start, end) 
                : java.util.Collections.emptyList();
                
        model.addAttribute("rooms", pagedRooms);
        model.addAttribute("hotel", hotel);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "admin/rooms/list";
    }

    @GetMapping("/hotel/{hotelId}/new")
    public String createForm(@PathVariable Long hotelId, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        var hotel = hotelRepository.findById(hotelId).orElseThrow();
        Room room = new Room();
        room.setHotel(hotel);
        prepareForm(model, room, "Thêm phòng - " + hotel.getName(), userDetails);
        return "admin/rooms/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, 
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Room room = roomRepository.findById(id).orElseThrow();
        prepareForm(model, room, "Sửa phòng", userDetails);
        return "admin/rooms/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam Long hotelId,
                       @RequestParam RoomType roomType,
                       @RequestParam BigDecimal price,
                       @RequestParam Integer maxGuests,
                       @RequestParam RoomStatus status,
                       @RequestParam(required = false, defaultValue = "false") Boolean freeCancellation,
                       @RequestParam(required = false, defaultValue = "false") Boolean breakfastIncluded,
                       @RequestParam(required = false, defaultValue = "false") Boolean prepayRequired,
                       @RequestParam(required = false) MultipartFile imageFile,
                       RedirectAttributes ra) {
        try {
            Room room = id != null ? roomRepository.findById(id).orElse(new Room()) : new Room();
            room.setHotel(hotelRepository.findById(hotelId).orElseThrow());
            room.setRoomType(roomType);
            room.setPrice(price);
            room.setMaxGuests(maxGuests);
            room.setStatus(status);
            room.setFreeCancellation(freeCancellation);
            room.setBreakfastIncluded(breakfastIncluded);
            room.setPrepayRequired(prepayRequired);

            if (imageFile != null && !imageFile.isEmpty()) {
                fileStorageService.deleteIfLocal(room.getImageUrl());
                room.setImageUrl(fileStorageService.store(imageFile, "rooms"));
            }

            roomRepository.save(room);
            ra.addFlashAttribute("success", "Đã lưu phòng thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return id != null ? "redirect:/admin/rooms/" + id + "/edit" : "redirect:/admin/rooms/hotel/" + hotelId + "/new";
        }
        return "redirect:/admin/rooms/hotel/" + hotelId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Room room = roomRepository.findById(id).orElseThrow();
        Long hotelId = room.getHotel().getId();
        fileStorageService.deleteIfLocal(room.getImageUrl());
        roomRepository.deleteById(id);
        ra.addFlashAttribute("success", "Đã xóa phòng");
        return "redirect:/admin/rooms/hotel/" + hotelId;
    }

    private void prepareForm(Model model, Room room, String title, UserDetails userDetails) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "rooms");
        model.addAttribute("room", room);
        
        boolean isPartner = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTNER"));
        if (isPartner) {
            User partner = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            model.addAttribute("hotels", hotelRepository.findByPartnerId(partner.getId()));
        } else {
            model.addAttribute("hotels", hotelRepository.findAll());
        }
        
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("roomStatuses", RoomStatus.values());
    }
}
