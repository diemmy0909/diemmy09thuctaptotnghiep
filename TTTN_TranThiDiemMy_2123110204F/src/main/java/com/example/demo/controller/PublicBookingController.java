package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/dat-phong")
@RequiredArgsConstructor
public class PublicBookingController {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @GetMapping
    public String form(@RequestParam Long roomId,
                       @RequestParam(required = false) String checkIn,
                       @RequestParam(required = false) String checkOut,
                       @RequestParam(required = false) Integer guests,
                       @RequestParam(required = false, defaultValue = "1") Integer numberOfRooms,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        var room = roomRepository.findById(roomId).orElseThrow();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        LocalDate in = checkIn != null && !checkIn.isBlank() ? LocalDate.parse(checkIn) : LocalDate.now().plusDays(1);
        LocalDate out = checkOut != null && !checkOut.isBlank() ? LocalDate.parse(checkOut) : in.plusDays(1);

        model.addAttribute("room", room);
        model.addAttribute("user", user);
        model.addAttribute("checkIn", in);
        model.addAttribute("checkOut", out);
        model.addAttribute("guests", guests != null ? guests : room.getMaxGuests());
        model.addAttribute("numberOfRooms", numberOfRooms);
        model.addAttribute("totalAmount", bookingService.calculateTotal(room, in, out, numberOfRooms));
        return "public/booking/form";
    }

    @PostMapping
    public String submit(@RequestParam Long roomId,
                         @RequestParam LocalDate checkIn,
                         @RequestParam LocalDate checkOut,
                         @RequestParam(defaultValue = "1") Integer numberOfRooms,
                         @RequestParam String fullName,
                         @RequestParam String phone,
                         @RequestParam String email,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes ra) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setEmail(email);
        userRepository.save(user);

        var booking = bookingService.createBooking(roomId, user.getId(), checkIn, checkOut, numberOfRooms);
        ra.addFlashAttribute("success", "Đặt phòng thành công! Mã: " + booking.getBookingCode());
        return "redirect:/thanh-toan/" + booking.getId();
    }
}
