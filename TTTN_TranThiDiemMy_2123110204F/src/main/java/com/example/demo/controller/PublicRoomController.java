package com.example.demo.controller;

import com.example.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/phong")
@RequiredArgsConstructor
public class PublicRoomController {

    private final RoomRepository roomRepository;

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) String checkIn,
                         @RequestParam(required = false) String checkOut,
                         @RequestParam(required = false) Integer guests,
                         Model model) {
        var room = roomRepository.findById(id).orElseThrow();
        model.addAttribute("room", room);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("guests", guests);
        return "public/rooms/detail";
    }
}
