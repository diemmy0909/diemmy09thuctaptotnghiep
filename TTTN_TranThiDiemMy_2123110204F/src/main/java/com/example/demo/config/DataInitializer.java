package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final PromotionRepository promotionRepository;
    private final FlightRepository flightRepository;
    private final TourActivityRepository tourActivityRepository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Tự động tạo 3 loại phòng cho bất kỳ khách sạn nào chưa có phòng
        var allHotels = hotelRepository.findAll();
        for (Hotel h : allHotels) {
            if (roomRepository.findByHotelId(h.getId()).isEmpty()) {
                // Đa dạng hóa giá: 800k, 1000k, 1200k, 1400k, 1600k, 1800k
                long basePriceLong = 800000L + ((h.getId() % 6) * 200000L);
                
                BigDecimal standardPrice = new BigDecimal(basePriceLong);
                BigDecimal deluxePrice = new BigDecimal(basePriceLong + 700000L);
                BigDecimal vipPrice = new BigDecimal(basePriceLong + 2200000L);

                roomRepository.save(Room.builder()
                        .hotel(h).roomType(RoomType.STANDARD).price(standardPrice)
                        .maxGuests(2).status(RoomStatus.AVAILABLE)
                        .build());
                roomRepository.save(Room.builder()
                        .hotel(h).roomType(RoomType.DELUXE).price(deluxePrice)
                        .maxGuests(3).status(RoomStatus.AVAILABLE)
                        .build());
                roomRepository.save(Room.builder()
                        .hotel(h).roomType(RoomType.VIP).price(vipPrice)
                        .maxGuests(4).status(RoomStatus.AVAILABLE)
                        .build());
            }
        }

        if (flightRepository.count() == 0) {
            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-202")
                    .departureCity("TP HCM (SGN)").arrivalCity("Hà Nội (HAN)")
                    .departureTime(LocalDateTime.now().plusDays(2).withHour(8).withMinute(0))
                    .arrivalTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(15))
                    .price(new BigDecimal("1500000")).availableSeats(120).build());

            flightRepository.save(Flight.builder()
                    .airline("VietJet Air").flightNumber("VJ-111")
                    .departureCity("TP HCM (SGN)").arrivalCity("Hà Nội (HAN)")
                    .departureTime(LocalDateTime.now().plusDays(2).withHour(9).withMinute(30))
                    .arrivalTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(40))
                    .price(new BigDecimal("950000")).availableSeats(180).build());

            flightRepository.save(Flight.builder()
                    .airline("Bamboo Airways").flightNumber("QH-303")
                    .departureCity("TP HCM (SGN)").arrivalCity("Bangkok (BKK)")
                    .departureTime(LocalDateTime.now().plusDays(5).withHour(14).withMinute(0))
                    .arrivalTime(LocalDateTime.now().plusDays(5).withHour(15).withMinute(30))
                    .price(new BigDecimal("2100000")).availableSeats(50).build());
        }

        if (flightRepository.count() <= 3) {
            int currentYear = LocalDateTime.now().getYear();
            int currentMonth = LocalDateTime.now().getMonthValue();
            
            // Ngày 10
            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-210")
                    .departureCity("Hà Nội (HAN)").arrivalCity("TP HCM (SGN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 10, 7, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 10, 9, 15))
                    .price(new BigDecimal("1600000")).availableSeats(100).build());
                    
            flightRepository.save(Flight.builder()
                    .airline("VietJet Air").flightNumber("VJ-210")
                    .departureCity("Đà Nẵng (DAD)").arrivalCity("Hà Nội (HAN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 10, 14, 30))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 10, 15, 50))
                    .price(new BigDecimal("850000")).availableSeats(150).build());

            // Ngày 11
            flightRepository.save(Flight.builder()
                    .airline("Bamboo Airways").flightNumber("QH-211")
                    .departureCity("TP HCM (SGN)").arrivalCity("Đà Nẵng (DAD)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 11, 10, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 11, 11, 20))
                    .price(new BigDecimal("1200000")).availableSeats(80).build());

            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-211")
                    .departureCity("Hà Nội (HAN)").arrivalCity("Đà Nẵng (DAD)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 11, 16, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 11, 17, 20))
                    .price(new BigDecimal("1400000")).availableSeats(110).build());

            // Ngày 12
            flightRepository.save(Flight.builder()
                    .airline("VietJet Air").flightNumber("VJ-212")
                    .departureCity("TP HCM (SGN)").arrivalCity("Hà Nội (HAN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 12, 6, 30))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 12, 8, 45))
                    .price(new BigDecimal("990000")).availableSeats(200).build());

            // Ngày 13
            flightRepository.save(Flight.builder()
                    .airline("Bamboo Airways").flightNumber("QH-213")
                    .departureCity("Đà Nẵng (DAD)").arrivalCity("TP HCM (SGN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 13, 9, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 13, 10, 20))
                    .price(new BigDecimal("1100000")).availableSeats(60).build());

            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-213")
                    .departureCity("Hà Nội (HAN)").arrivalCity("TP HCM (SGN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 13, 19, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 13, 21, 15))
                    .price(new BigDecimal("1750000")).availableSeats(90).build());

            // Ngày 14
            flightRepository.save(Flight.builder()
                    .airline("VietJet Air").flightNumber("VJ-214")
                    .departureCity("TP HCM (SGN)").arrivalCity("Đà Nẵng (DAD)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 14, 11, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 14, 12, 20))
                    .price(new BigDecimal("750000")).availableSeats(160).build());

            flightRepository.save(Flight.builder()
                    .airline("Bamboo Airways").flightNumber("QH-214")
                    .departureCity("Hà Nội (HAN)").arrivalCity("Đà Nẵng (DAD)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 14, 15, 30))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 14, 16, 50))
                    .price(new BigDecimal("1300000")).availableSeats(70).build());

            // Ngày 15
            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-215")
                    .departureCity("Đà Nẵng (DAD)").arrivalCity("Hà Nội (HAN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 15, 8, 30))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 15, 9, 50))
                    .price(new BigDecimal("1550000")).availableSeats(120).build());
        }

        if (flightRepository.count() <= 13) {
            int currentYear = 2026;
            int currentMonth = 7; // Tháng 7
            
            // Ngày 12
            flightRepository.save(Flight.builder()
                    .airline("Bamboo Airways").flightNumber("QH-401")
                    .departureCity("Hà Nội (HAN)").arrivalCity("Đà Nẵng (DAD)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 12, 10, 30))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 12, 11, 50))
                    .price(new BigDecimal("1450000")).availableSeats(90).build());
                    
            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-402")
                    .departureCity("Đà Nẵng (DAD)").arrivalCity("TP HCM (SGN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 12, 16, 45))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 12, 18, 15))
                    .price(new BigDecimal("1650000")).availableSeats(140).build());

            // Ngày 13
            flightRepository.save(Flight.builder()
                    .airline("VietJet Air").flightNumber("VJ-403")
                    .departureCity("TP HCM (SGN)").arrivalCity("Hà Nội (HAN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 13, 5, 30))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 13, 7, 40))
                    .price(new BigDecimal("890000")).availableSeats(180).build());

            flightRepository.save(Flight.builder()
                    .airline("Bamboo Airways").flightNumber("QH-404")
                    .departureCity("Hà Nội (HAN)").arrivalCity("Bangkok (BKK)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 13, 11, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 13, 13, 5))
                    .price(new BigDecimal("2300000")).availableSeats(60).build());
                    
            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-405")
                    .departureCity("TP HCM (SGN)").arrivalCity("Đà Nẵng (DAD)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 13, 20, 15))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 13, 21, 35))
                    .price(new BigDecimal("1550000")).availableSeats(110).build());

            // Ngày 14
            flightRepository.save(Flight.builder()
                    .airline("VietJet Air").flightNumber("VJ-406")
                    .departureCity("Đà Nẵng (DAD)").arrivalCity("Hà Nội (HAN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 14, 8, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 14, 9, 20))
                    .price(new BigDecimal("950000")).availableSeats(200).build());

            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-407")
                    .departureCity("Hà Nội (HAN)").arrivalCity("TP HCM (SGN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 14, 14, 30))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 14, 16, 45))
                    .price(new BigDecimal("1850000")).availableSeats(130).build());
                    
            flightRepository.save(Flight.builder()
                    .airline("Bamboo Airways").flightNumber("QH-408")
                    .departureCity("TP HCM (SGN)").arrivalCity("Đà Nẵng (DAD)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 14, 18, 0))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 14, 19, 20))
                    .price(new BigDecimal("1250000")).availableSeats(85).build());

            // Ngày 15
            flightRepository.save(Flight.builder()
                    .airline("VietJet Air").flightNumber("VJ-409")
                    .departureCity("Hà Nội (HAN)").arrivalCity("Đà Nẵng (DAD)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 15, 6, 15))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 15, 7, 35))
                    .price(new BigDecimal("790000")).availableSeats(170).build());

            flightRepository.save(Flight.builder()
                    .airline("Vietnam Airlines").flightNumber("VN-410")
                    .departureCity("Đà Nẵng (DAD)").arrivalCity("TP HCM (SGN)")
                    .departureTime(LocalDateTime.of(currentYear, currentMonth, 15, 17, 30))
                    .arrivalTime(LocalDateTime.of(currentYear, currentMonth, 15, 19, 0))
                    .price(new BigDecimal("1700000")).availableSeats(100).build());
        }

        if (tourActivityRepository.count() == 0) {
            tourActivityRepository.save(TourActivity.builder()
                    .title("Vé Công viên nước The Amazing Bay")
                    .imageUrl("https://images.unsplash.com/photo-1578530332818-6ba472e67b9f?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80")
                    .description("Khám phá công viên nước lớn nhất Việt Nam")
                    .build());

            tourActivityRepository.save(TourActivity.builder()
                    .title("Vé Công viên Văn hóa Suối Tiên")
                    .imageUrl("https://images.unsplash.com/photo-1522201932371-d853e5eaf888?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80")
                    .description("Khu vui chơi giải trí văn hóa tâm linh")
                    .build());

            tourActivityRepository.save(TourActivity.builder()
                    .title("Sun World Ba Den Mountain")
                    .imageUrl("https://images.unsplash.com/photo-1542361345-89ce52fa041c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80")
                    .description("Khám phá đỉnh núi thiêng")
                    .build());

            tourActivityRepository.save(TourActivity.builder()
                    .title("VinWonders Nha Trang")
                    .imageUrl("https://images.unsplash.com/photo-1506197603052-3cc9c3a201bd?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80")
                    .description("Công viên giải trí đẳng cấp quốc tế")
                    .build());
        }

        if (carRepository.count() == 0) {
            carRepository.save(Car.builder().name("VinFast VF8").type("SUV").transmission("Số tự động").seats(5).pricePerDay(new BigDecimal("1200000")).imageUrl("https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80").provider("AVIS").build());
            carRepository.save(Car.builder().name("Toyota Fortuner").type("SUV").transmission("Số tự động").seats(7).pricePerDay(new BigDecimal("1500000")).imageUrl("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80").provider("Hertz").build());
            carRepository.save(Car.builder().name("Honda City").type("Sedan").transmission("Số tự động").seats(5).pricePerDay(new BigDecimal("800000")).imageUrl("https://images.unsplash.com/photo-1552519507-da3b142c6e3d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80").provider("SMART").build());
            carRepository.save(Car.builder().name("Kia Carnival").type("MPV").transmission("Số tự động").seats(7).pricePerDay(new BigDecimal("2000000")).imageUrl("https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80").provider("AVIS").build());
            carRepository.save(Car.builder().name("Mazda 3").type("Sedan").transmission("Số tự động").seats(5).pricePerDay(new BigDecimal("900000")).imageUrl("https://images.unsplash.com/photo-1552519507-da3b142c6e3d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80").provider("Hertz").build());
            carRepository.save(Car.builder().name("Ford Ranger").type("Bán tải").transmission("Số sàn").seats(5).pricePerDay(new BigDecimal("1100000")).imageUrl("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80").provider("TRAC").build());
        }
        
        // Auto-fix broken images for existing cars in database
        for (Car c : carRepository.findAll()) {
            if (c.getImageUrl() != null && c.getImageUrl().contains("1621007947382")) {
                c.setImageUrl("https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80");
                carRepository.save(c);
            }
            if (c.getImageUrl() != null && c.getImageUrl().contains("1590362891991")) {
                c.setImageUrl("https://images.unsplash.com/photo-1552519507-da3b142c6e3d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80");
                carRepository.save(c);
            }
        }

        if (!promotionRepository.existsByCode("SUMMER2026")) {
            promotionRepository.save(Promotion.builder()
                    .code("SUMMER2026")
                    .description("Giảm 20% mùa hè")
                    .discountPercent(20)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(3))
                    .active(true)
                    .build());
        }
        
        promotionRepository.findByCode("VIP20").ifPresent(promotionRepository::delete);
        
        if (!promotionRepository.existsByCode("WELCOME12")) {
            promotionRepository.save(Promotion.builder()
                    .code("WELCOME12")
                    .description("Chào bạn mới giảm 12%")
                    .discountPercent(12)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(1))
                    .active(true)
                    .build());
        }
        
        if (!promotionRepository.existsByCode("FLASH15")) {
            promotionRepository.save(Promotion.builder()
                    .code("FLASH15")
                    .description("Flash Sale giảm 15%")
                    .discountPercent(15)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(7))
                    .active(true)
                    .build());
        }

        if (!promotionRepository.existsByCode("VIP75K")) {
            promotionRepository.save(Promotion.builder()
                    .code("VIP75K")
                    .description("Đặc quyền VIP giảm 75,000đ")
                    .discountAmount(new BigDecimal("75000"))
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(6))
                    .active(true)
                    .build());
        }

        if (userRepository.count() > 0) {
            return;
        }

        String encoded = passwordEncoder.encode("admin123");

        userRepository.save(User.builder()
                .fullName("Super Admin")
                .email("admin@hotel.com")
                .phone("0901234567")
                .password(encoded)
                .role(Role.SUPER_ADMIN)
                .build());

        userRepository.save(User.builder()
                .fullName("Nguyễn Văn Manager")
                .email("manager@hotel.com")
                .phone("0902222222")
                .password(encoded)
                .role(Role.MANAGER)
                .build());

        userRepository.save(User.builder()
                .fullName("Trần Thị Staff")
                .email("staff@hotel.com")
                .phone("0903333333")
                .password(encoded)
                .role(Role.STAFF)
                .build());

        User customer = userRepository.save(User.builder()
                .fullName("Lê Khách Hàng")
                .email("khach@email.com")
                .phone("0912345678")
                .password(encoded)
                .role(Role.CUSTOMER)
                .build());

        Amenity wifi = amenityRepository.save(Amenity.builder().name("WiFi").description("WiFi miễn phí").icon("wifi").build());
        Amenity pool = amenityRepository.save(Amenity.builder().name("Hồ bơi").description("Hồ bơi ngoài trời").icon("pool").build());
        Amenity parking = amenityRepository.save(Amenity.builder().name("Bãi đỗ xe").description("Bãi đỗ xe miễn phí").icon("parking").build());
        Amenity breakfast = amenityRepository.save(Amenity.builder().name("Buffet sáng").description("Ăn sáng buffet").icon("breakfast").build());

        Hotel hotel = hotelRepository.save(Hotel.builder()
                .name("Khách sạn Biển Xanh")
                .address("123 Đường Biển, Đà Nẵng")
                .description("Khách sạn 5 sao view biển, phục vụ chu đáo")
                .starRating(5)
                .amenities(Set.of(wifi, pool, parking, breakfast))
                .build());

        roomRepository.save(Room.builder()
                .hotel(hotel).roomType(RoomType.STANDARD).price(new BigDecimal("800000"))
                .maxGuests(2).status(RoomStatus.AVAILABLE)
                .build());

        Room deluxe = roomRepository.save(Room.builder()
                .hotel(hotel).roomType(RoomType.DELUXE).price(new BigDecimal("1500000"))
                .maxGuests(3).status(RoomStatus.AVAILABLE)
                .build());

        roomRepository.save(Room.builder()
                .hotel(hotel).roomType(RoomType.VIP).price(new BigDecimal("3000000"))
                .maxGuests(4).status(RoomStatus.AVAILABLE)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .bookingCode("BK20260001")
                .customer(customer)
                .room(deluxe)
                .checkInDate(LocalDate.now().plusDays(3))
                .checkOutDate(LocalDate.now().plusDays(5))
                .totalAmount(new BigDecimal("3000000"))
                .status(BookingStatus.PENDING)
                .build());

        paymentRepository.save(Payment.builder()
                .paymentCode("PAY20260001")
                .booking(booking)
                .amount(new BigDecimal("3000000"))
                .method(PaymentMethod.MOMO)
                .status(PaymentStatus.PENDING)
                .build());

        reviewRepository.save(Review.builder()
                .hotel(hotel).user(customer).rating(5)
                .comment("Khách sạn rất đẹp, nhân viên thân thiện!")
                .approved(false)
                .build());

    }
}
// trigger recompile 2
