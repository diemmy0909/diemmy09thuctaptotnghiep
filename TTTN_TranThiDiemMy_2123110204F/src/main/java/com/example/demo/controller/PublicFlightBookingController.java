package com.example.demo.controller;

import com.example.demo.entity.Flight;
import com.example.demo.entity.User;
import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FlightBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.repository.FlightBookingRepository;
import com.example.demo.repository.MealRepository;
import com.example.demo.entity.BookingStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dat-ve-may-bay")
@RequiredArgsConstructor
public class PublicFlightBookingController {

    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final FlightBookingService flightBookingService;
    private final FlightBookingRepository flightBookingRepository;
    private final MealRepository mealRepository;

    @GetMapping
    public String form(@RequestParam(required = false) Long flightId,
                       @RequestParam(required = false) Long outboundFlightId,
                       @RequestParam(required = false) String outboundSeatClass,
                       @RequestParam(required = false) String outboundMealOption,
                       @RequestParam(required = false) Long returnFlightId,
                       @RequestParam(required = false) String returnSeatClass,
                       @RequestParam(required = false) String returnMealOption,
                       @RequestParam(required = false, defaultValue = "ECONOMY") String seatClass,
                       @RequestParam(required = false) String mealOption,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        
        Long finalOutboundId = flightId != null ? flightId : outboundFlightId;
        String finalOutboundClass = seatClass != null && !seatClass.equals("ECONOMY") && outboundSeatClass == null ? seatClass : (outboundSeatClass != null ? outboundSeatClass : "ECONOMY");
        String finalOutboundMeal = mealOption != null && outboundMealOption == null ? mealOption : outboundMealOption;
                       
        Flight flight = flightRepository.findById(finalOutboundId).orElseThrow();
        User user = null;
        if (userDetails != null) {
            user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        model.addAttribute("flight", flight);
        model.addAttribute("user", user);
        model.addAttribute("passengers", 1);
        model.addAttribute("extraBaggageKg", 0);
        model.addAttribute("seatClass", finalOutboundClass);
        model.addAttribute("mealOption", finalOutboundMeal);
        
        BigDecimal mealPrice = BigDecimal.ZERO;
        if (finalOutboundMeal != null && !finalOutboundMeal.isEmpty()) {
            mealRepository.findByName(finalOutboundMeal).ifPresent(m -> {
                model.addAttribute("mealPrice", m.getPrice());
            });
        }
        if (!model.containsAttribute("mealPrice")) {
            model.addAttribute("mealPrice", BigDecimal.ZERO);
        }

        BigDecimal outboundTotal = flightBookingService.calculateTotal(flight, 1, 0, 0, 0, finalOutboundClass, finalOutboundMeal);
        
        // Handle return flight
        if (returnFlightId != null) {
            Flight returnFlight = flightRepository.findById(returnFlightId).orElseThrow();
            model.addAttribute("returnFlight", returnFlight);
            model.addAttribute("returnSeatClass", returnSeatClass != null ? returnSeatClass : "ECONOMY");
            model.addAttribute("returnMealOption", returnMealOption);
            
            BigDecimal returnMealPrice = BigDecimal.ZERO;
            if (returnMealOption != null && !returnMealOption.isEmpty()) {
                var m = mealRepository.findByName(returnMealOption);
                if (m.isPresent()) returnMealPrice = m.get().getPrice();
            }
            model.addAttribute("returnMealPrice", returnMealPrice);
            
            BigDecimal returnTotal = flightBookingService.calculateTotal(returnFlight, 1, 0, 0, 0, returnSeatClass != null ? returnSeatClass : "ECONOMY", returnMealOption);
            model.addAttribute("totalAmount", outboundTotal.add(returnTotal));
            
            // Return booked seats
            List<String> returnBookedSeats = new ArrayList<>();
            List<com.example.demo.entity.FlightBooking> returnBookings = flightBookingRepository.findByFlight_IdAndStatusNot(returnFlightId, BookingStatus.CANCELLED);
            for (com.example.demo.entity.FlightBooking b : returnBookings) {
                if (b.getReturnSeatNumbers() != null && !b.getReturnSeatNumbers().trim().isEmpty()) {
                    String[] seats = b.getReturnSeatNumbers().split(",");
                    for (String s : seats) { returnBookedSeats.add(s.trim()); }
                }
                if (b.getSeatNumbers() != null && !b.getSeatNumbers().trim().isEmpty() && b.getFlight().getId().equals(returnFlightId)) {
                    String[] seats = b.getSeatNumbers().split(",");
                    for (String s : seats) { returnBookedSeats.add(s.trim()); }
                }
            }
            model.addAttribute("returnBookedSeats", returnBookedSeats);
            model.addAttribute("isRoundTrip", true);
        } else {
            model.addAttribute("totalAmount", outboundTotal);
            model.addAttribute("isRoundTrip", false);
        }
        
        List<String> bookedSeats = new ArrayList<>();
        List<com.example.demo.entity.FlightBooking> bookings = flightBookingRepository.findByFlight_IdAndStatusNot(flightId, BookingStatus.CANCELLED);
        for (com.example.demo.entity.FlightBooking b : bookings) {
            if (b.getSeatNumbers() != null && !b.getSeatNumbers().trim().isEmpty()) {
                String[] seats = b.getSeatNumbers().split(",");
                for (String s : seats) {
                    bookedSeats.add(s.trim());
                }
            }
        }
        model.addAttribute("bookedSeats", bookedSeats);
        
        return "public/flight-booking/form";
    }

    @PostMapping
    public String submit(@RequestParam(required = false) Long flightId,
                         @RequestParam(required = false) Long outboundFlightId,
                         @RequestParam Integer adults,
                         @RequestParam Integer children,
                         @RequestParam Integer infants,
                         @RequestParam(required = false) String[] passengerTypes,
                         @RequestParam(required = false) String[] passengerNames,
                         @RequestParam(required = false) String[] passengerIdCards,
                         @RequestParam(required = false) String seatNumbers,
                         @RequestParam Integer extraBaggageKg,
                         @RequestParam(required = false, defaultValue = "ECONOMY") String seatClass,
                         @RequestParam(required = false) String mealOption,
                         @RequestParam(required = false) Long returnFlightId,
                         @RequestParam(required = false) String returnSeatClass,
                         @RequestParam(required = false) String returnMealOption,
                         @RequestParam(required = false) String returnSeatNumbers,
                         @RequestParam String fullName,
                         @RequestParam String phone,
                         @RequestParam String email,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes ra) {
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        // Cập nhật thông tin cá nhân nếu cần
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setEmail(email);
        userRepository.save(user);

        Long finalOutboundId = flightId != null ? flightId : outboundFlightId;

        var booking = flightBookingService.createBooking(finalOutboundId, user.getId(), adults, children, infants, 
                                                         passengerTypes, passengerNames, passengerIdCards, 
                                                         extraBaggageKg, seatClass, mealOption);
        
        if (seatNumbers != null && !seatNumbers.isEmpty()) {
            booking.setSeatNumbers(seatNumbers);
        }
        
        if (returnFlightId != null) {
            Flight returnFlight = flightRepository.findById(returnFlightId).orElseThrow();
            booking.setReturnFlight(returnFlight);
            booking.setReturnSeatClass(returnSeatClass);
            booking.setReturnMealOption(returnMealOption);
            if (returnSeatNumbers != null && !returnSeatNumbers.isEmpty()) {
                booking.setReturnSeatNumbers(returnSeatNumbers);
            }
            
            // Add return total amount
            BigDecimal returnTotal = flightBookingService.calculateTotal(returnFlight, adults, children, infants, 0, returnSeatClass != null ? returnSeatClass : "ECONOMY", returnMealOption);
            booking.setTotalAmount(booking.getTotalAmount().add(returnTotal));
        }
        
        flightBookingRepository.save(booking);

        ra.addFlashAttribute("success", "Đặt vé máy bay thành công! Mã: " + booking.getBookingCode());
        
        int totalSeatsNeeded = adults + children; // Infants don't take a seat
        
        // Cập nhật số ghế trống
        Flight flight = flightRepository.findById(finalOutboundId).orElseThrow();
        if ("BUSINESS".equalsIgnoreCase(seatClass)) {
            if (flight.getAvailableBusinessSeats() != null && flight.getAvailableBusinessSeats() >= totalSeatsNeeded) {
                flight.setAvailableBusinessSeats(flight.getAvailableBusinessSeats() - totalSeatsNeeded);
            }
        } else {
            if (flight.getAvailableSeats() >= totalSeatsNeeded) {
                flight.setAvailableSeats(flight.getAvailableSeats() - totalSeatsNeeded);
            }
        }
        flightRepository.save(flight);
        
        if (returnFlightId != null) {
            Flight rFlight = flightRepository.findById(returnFlightId).orElseThrow();
            if ("BUSINESS".equalsIgnoreCase(returnSeatClass)) {
                if (rFlight.getAvailableBusinessSeats() != null && rFlight.getAvailableBusinessSeats() >= totalSeatsNeeded) {
                    rFlight.setAvailableBusinessSeats(rFlight.getAvailableBusinessSeats() - totalSeatsNeeded);
                }
            } else {
                if (rFlight.getAvailableSeats() >= totalSeatsNeeded) {
                    rFlight.setAvailableSeats(rFlight.getAvailableSeats() - totalSeatsNeeded);
                }
            }
            flightRepository.save(rFlight);
        }

        // Chuyển hướng tới cổng thanh toán
        return "redirect:/thanh-toan-may-bay/" + booking.getId();
    }
}
