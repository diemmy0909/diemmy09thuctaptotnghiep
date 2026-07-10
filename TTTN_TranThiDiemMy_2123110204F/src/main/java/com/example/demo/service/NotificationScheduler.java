package com.example.demo.service;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.NotificationType;
import com.example.demo.entity.TicketBooking;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.TicketBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final BookingRepository bookingRepository;
    private final TicketBookingRepository ticketBookingRepository;
    private final NotificationService notificationService;

    // Run every day at 00:00 (Midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    public void sendDepartureReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Remind Hotel Bookings
        List<Booking> upcomingBookings = bookingRepository.findByStatusAndCheckInDate(BookingStatus.CONFIRMED, tomorrow);
        for (Booking booking : upcomingBookings) {
            String title = "Nhắc nhở ngày nhận phòng";
            String message = "Bạn có lịch nhận phòng tại " + booking.getRoom().getHotel().getName() + " vào ngày mai (" + tomorrow + "). Mã đặt phòng: " + booking.getBookingCode();
            notificationService.createNotification(booking.getCustomer(), title, message, NotificationType.REMINDER);
        }

        // Remind Ticket Bookings
        List<TicketBooking> upcomingTickets = ticketBookingRepository.findByStatusAndParticipationDate(BookingStatus.CONFIRMED, tomorrow);
        for (TicketBooking ticketBooking : upcomingTickets) {
            String title = "Nhắc nhở ngày tham gia";
            String message = "Bạn có lịch tham gia " + ticketBooking.getTicket().getTitle() + " vào ngày mai (" + tomorrow + "). Mã đặt vé: " + ticketBooking.getBookingCode();
            notificationService.createNotification(ticketBooking.getCustomer(), title, message, NotificationType.REMINDER);
        }
    }
}
