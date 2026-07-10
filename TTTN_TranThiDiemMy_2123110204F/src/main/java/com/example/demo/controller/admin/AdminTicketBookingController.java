package com.example.demo.controller.admin;

import com.example.demo.entity.TicketBooking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.repository.TicketBookingRepository;
import lombok.RequiredArgsConstructor;
import com.example.demo.service.NotificationService;
import com.example.demo.entity.NotificationType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/ticket-bookings")
@RequiredArgsConstructor
public class AdminTicketBookingController {

    private final TicketBookingRepository ticketBookingRepository;
    private final NotificationService notificationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", ticketBookingRepository.findAll());
        return "admin/ticket-bookings/list";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
        TicketBooking booking = ticketBookingRepository.findById(id).orElseThrow();
        booking.setStatus(status);
        ticketBookingRepository.save(booking);
        
        if (status == BookingStatus.CONFIRMED) {
            notificationService.createNotification(
                    booking.getCustomer(),
                    "Xác nhận đặt vé thành công",
                    "Đơn đặt vé " + booking.getBookingCode() + " của bạn đã được xác nhận. Vui lòng kiểm tra lịch sử đặt vé.",
                    NotificationType.TICKET_CONFIRMED
            );
        }
        
        return "redirect:/admin/ticket-bookings";
    }
}
