# BÁO CÁO THỰC TẬP TỐT NGHIỆP: HỆ THỐNG QUẢN LÝ ĐẶT DỊCH VỤ DU LỊCH & KHÁCH SẠN TỔNG HỢP (ALUMINUM TRAVEL)

---

## MỤC LỤC
1. [Giới thiệu dự án](#1-giới-thiệu-dự-án)
2. [Mục tiêu và Phạm vi dự án](#2-mục-tiêu-và-phạm-vi-dự-án)
3. [Công nghệ sử dụng](#3-công-nghệ-sử-dụng)
4. [Kiến trúc hệ thống](#4-kiến-trúc-hệ-thống)
5. [Cấu trúc thư mục dự án](#5-cấu-trúc-thư-mục-dự-án)
6. [Mô tả Chức năng chi tiết](#6-mô-tả-chức-năng-chi-tiết)
   - [6.1 Dành cho Khách hàng (User)](#61-dành-cho-khách-hàng-user)
   - [6.2 Dành cho Quản trị viên (Admin)](#62-dành-cho-quản-trị-viên-admin)
7. [Thiết kế Cơ sở dữ liệu](#7-thiết-kế-cơ-sở-dữ-liệu)
8. [Hướng dẫn cài đặt và chạy dự án](#8-hướng-dẫn-cài-đặt-và-chạy-dự-án)
9. [Bảo mật & Tích hợp](#9-bảo-mật--tích-hợp)
10. [Hình ảnh Giao diện (Demo)](#10-hình-ảnh-giao-diện-demo)
11. [Hướng phát triển tương lai](#11-hướng-phát-triển-tương-lai)
12. [Thông tin Sinh viên & Giảng viên hướng dẫn](#12-thông-tin-sinh-viên--giảng-viên-hướng-dẫn)

---

## 1. Giới thiệu dự án

Dự án **Hệ thống Quản lý Đặt Dịch vụ Du lịch & Khách sạn Tổng hợp** (Tên thương hiệu giả định: *Aluminum Travel*) là một nền tảng thương mại điện tử chuyên biệt trong lĩnh vực du lịch. Trang web cung cấp một giải pháp toàn diện cho du khách, cho phép họ tìm kiếm, so sánh và đặt nhiều loại dịch vụ khác nhau trong cùng một chuyến đi, bao gồm:
- **Đặt phòng khách sạn.**
- **Đặt vé máy bay.**
- **Đặt vé tham quan (Tour & Activities).**
- **Thuê xe du lịch.**

Dự án được phát triển dưới dạng một ứng dụng web Monolithic sử dụng Java Spring Boot kết hợp với giao diện SSR (Server-Side Rendering) thông qua Thymeleaf. Hệ thống không chỉ chú trọng vào trải nghiệm khách hàng (Front-end/Public UI) mà còn xây dựng một hệ thống quản trị mạnh mẽ (Admin Dashboard) để theo dõi, quản lý đơn hàng và doanh thu.

---

## 2. Mục tiêu và Phạm vi dự án

### Mục tiêu dự án
- Xây dựng một quy trình đặt phòng và dịch vụ du lịch hoàn chỉnh, mô phỏng các trang web OTA (Online Travel Agent) lớn như Traveloka, Booking.com, Agoda.
- Nắm vững và áp dụng các framework hiện đại của ngôn ngữ lập trình Java (Spring Boot, Spring Security, Hibernate/JPA).
- Đảm bảo tính bảo mật của hệ thống: Xác thực người dùng, ủy quyền vai trò (Role-based Authorization), mã hóa mật khẩu, và bảo mật các luồng thanh toán.
- Áp dụng các quy trình phát triển phần mềm cơ bản từ lúc phân tích thiết kế CSDL đến lập trình và triển khai.

### Phạm vi hệ thống
Hệ thống xử lý đa dạng các luồng dữ liệu nghiệp vụ:
- **Quản lý Lưu trú:** Thông tin Khách sạn, Phòng, Tiện ích, Hình ảnh, Giá thay đổi theo ngày, Trạng thái phòng trống.
- **Quản lý Di chuyển:** Danh sách chuyến bay, Điểm đi/Điểm đến, Các hạng vé, Dịch vụ thuê xe ô tô, xe máy.
- **Quản lý Giải trí:** Vé tham quan, Hoạt động trải nghiệm tại các địa điểm du lịch.
- **Quản lý Thanh toán & Khuyến mãi:** Tích hợp mã giảm giá (Promo Code), mô phỏng thanh toán quét mã QR/VNPay.

---

## 3. Công nghệ sử dụng

Dự án được xây dựng dựa trên các công nghệ tiên tiến, phổ biến trong các doanh nghiệp hiện nay:

**Back-end:**
- **Ngôn ngữ:** Java 21 (Phiên bản LTS mới nhất hỗ trợ các tính năng tối ưu như Records, Virtual Threads).
- **Core Framework:** Spring Boot 3.x (Quản lý Dependency Injection, Auto-configuration).
- **Web Layer:** Spring Web MVC.
- **Security:** Spring Security 6 (Quản lý đăng nhập, phân quyền, mã hóa BCrypt, bảo vệ CSRF).
- **Database Access:** Spring Data JPA, Hibernate ORM.
- **Email Service:** Spring Boot Mail (Tích hợp gửi mã OTP xác thực tài khoản/Quên mật khẩu).
- **Validation:** Hibernate Validator.
- **Khác:** Lombok (Giảm thiểu boilerplate code).

**Front-end:**
- **Template Engine:** Thymeleaf (Thymeleaf Extras Spring Security 6).
- **Thiết kế giao diện:** HTML5, CSS3, JavaScript (Vanilla ES6+).
- **UI Framework / Thư viện:** Bootstrap 5 (hoặc CSS thuần) kết hợp với các hiệu ứng Animation/Glassmorphism hiện đại.
- **Icons & Fonts:** FontAwesome, Google Fonts.

**Database & Server:**
- **Hệ quản trị CSDL:** MySQL 8.x.
- **Build Tool:** Maven.
- **Server:** Apache Tomcat (Nhúng sẵn trong Spring Boot).

---

## 4. Kiến trúc hệ thống

Hệ thống được thiết kế theo mô hình **MVC (Model - View - Controller)** kết hợp với kiến trúc N-Tier chuẩn của Spring Boot:
1. **Presentation Layer (View & Controller):** Tiếp nhận các HTTP Request từ trình duyệt, điều phối dữ liệu qua các `@Controller` hoặc `@RestController`. Trả về các trang HTML (Thymeleaf) hoặc JSON data.
2. **Business Logic Layer (Service):** Chứa toàn bộ các nghiệp vụ phức tạp, tính toán giá tiền, áp dụng khuyến mãi, kiểm tra phòng trống. Sử dụng các interface và implementation (Service - ServiceImpl).
3. **Data Access Layer (Repository):** Sử dụng các interface kế thừa từ `JpaRepository`. Layer này xử lý việc tạo tự động các câu truy vấn SQL để giao tiếp với MySQL.
4. **Data Layer (Database):** Cơ sở dữ liệu quan hệ MySQL lưu trữ các bảng thực thể.

---

## 5. Cấu trúc thư mục dự án

```text
TTTN_TranThiDiemMy_2123110204F/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/        # Cấu hình Spring Security, MVC, Web, File Upload
│   │   │   ├── controller/    # Điều hướng luồng (Public Controller, Admin Controller)
│   │   │   ├── model/         # Các Entity class mapping trực tiếp với bảng DB
│   │   │   ├── repository/    # Giao tiếp cơ sở dữ liệu (JpaRepository)
│   │   │   ├── service/       # Chứa logic nghiệp vụ
│   │   │   ├── util/          # Các hàm tiện ích (Format tiền tệ, Tạo mã, File Utils)
│   │   │   └── DemoApplication.java # File chạy chính của project
│   │   └── resources/
│   │       ├── application.properties # Cấu hình Database, Cổng Server, Mail SMTP
│   │       ├── static/        # Chứa CSS, JS, Images tĩnh
│   │       └── templates/     # Các trang giao diện HTML (Thymeleaf)
│   │           ├── admin/     # Giao diện cho quản trị viên
│   │           └── public/    # Giao diện cho người dùng
├── pom.xml                    # File cấu hình Maven, danh sách thư viện
├── datphongkhachsan.sql       # Script khởi tạo Cơ sở dữ liệu và dữ liệu mẫu
└── README.md                  # Tài liệu thông tin dự án
```

---

## 6. Mô tả Chức năng chi tiết

Hệ thống được chia làm hai khu vực (Dashboard) dành cho 2 nhóm đối tượng chính.

### 6.1 Dành cho Khách hàng (User)

**A. Đăng nhập, Đăng ký và Quản lý tài khoản:**
- Đăng ký tài khoản với xác thực Email OTP (One-Time Password).
- Đăng nhập, Đăng xuất, Lưu phiên đăng nhập (Remember Me).
- Quên mật khẩu: Yêu cầu mã OTP về email để đặt lại mật khẩu mới.
- Quản lý Hồ sơ cá nhân (Profile): Cập nhật thông tin, thay đổi ảnh đại diện (Avatar).

**B. Khách sạn & Lưu trú:**
- **Tìm kiếm nâng cao:** Tìm khách sạn theo địa điểm, tên khách sạn.
- **Bộ lọc đa dạng:** Lọc khách sạn theo mức giá, hạng sao (1-5 sao), tiện ích (Hồ bơi, Wifi, Đưa đón).
- **Chi tiết Khách sạn:** Xem mô tả, địa chỉ, hình ảnh gallery, các tiện ích đi kèm, và danh sách các loại phòng (Standard, Superior, Deluxe, Suite).
- **Đặt phòng:** Chọn ngày nhận (Check-in), ngày trả (Check-out), số lượng khách. Hệ thống tự động tính tổng tiền.

**C. Đặt Vé Máy Bay:**
- Tìm kiếm chuyến bay Khứ hồi / Một chiều.
- Hiển thị danh sách chuyến bay theo hãng hàng không, giờ khởi hành, giá vé.
- Thông tin chi tiết vé máy bay và số ghế.

**D. Dịch vụ Mở rộng (Vé tham quan & Thuê xe):**
- **Tour & Activities:** Khám phá và mua vé các hoạt động giải trí tại điểm đến.
- **Thuê xe tự lái/có tài xế:** Chọn loại xe (4 chỗ, 7 chỗ), hiển thị chi tiết giá thuê theo ngày.

**E. Giỏ hàng, Yêu thích và Đánh giá (Wishlist & Reviews):**
- **Yêu thích (Wishlist):** Lưu lại khách sạn, chuyến bay hoặc phòng mong muốn để xem lại sau.
- **Đánh giá & Bình luận:** Khách hàng sau khi sử dụng dịch vụ có thể để lại Rating (1-5 sao) và hình ảnh thực tế. Hệ thống kiểm duyệt nội dung.
- **Thông báo (Notifications):** Nhận thông báo tự động khi đặt phòng thành công hoặc khi admin duyệt đơn.

**F. Thanh toán & Áp dụng Khuyến mãi:**
- Nhập mã giảm giá (Voucher/Promo Code). Hệ thống tự động kiểm tra tính hợp lệ (Thời hạn sử dụng, % giảm hoặc Số tiền giảm tối đa) và cập nhật tổng bill.
- Mô phỏng quá trình thanh toán qua Cổng thanh toán (Hiển thị mã QR hoặc Form nhập thẻ).
- Xem lại **Lịch sử Đặt phòng (Booking History)** và **Lịch sử Vé (Ticket History)**.

### 6.2 Dành cho Quản trị viên (Admin)

Khu vực Admin được bảo vệ chặt chẽ, chỉ những tài khoản có `ROLE_ADMIN` hoặc `ROLE_STAFF` mới được truy cập.

**A. Quản lý Tổng quan (Dashboard):**
- Thống kê doanh thu, số lượng đơn đặt phòng, số lượng user đang hoạt động bằng biểu đồ trực quan.

**B. Quản lý Dịch vụ:**
- **Quản lý Khách sạn:** Thêm, Sửa, Xóa thông tin khách sạn. Upload nhiều hình ảnh cùng lúc. Cập nhật trạng thái (Active/Inactive).
- **Quản lý Phòng:** Định nghĩa các loại phòng thuộc từng khách sạn. Cài đặt giá cơ bản, sức chứa tối đa.
- **Quản lý Tiện ích:** Thêm các thẻ (Tags) tiện ích để gán vào khách sạn/phòng.

**C. Quản lý Đặt phòng & Vé (Booking Management):**
- Xem danh sách các đơn đặt phòng/vé từ khách hàng.
- Xử lý trạng thái đơn hàng: Từ *Chờ xác nhận (Pending)* -> *Đã xác nhận (Confirmed)* / *Đã hủy (Cancelled)* -> *Hoàn thành (Completed)*.
- Gửi thông báo thay đổi trạng thái đơn hàng đến User qua hệ thống Notification trong app.

**D. Quản lý Khuyến mãi (Promotions):**
- Tạo mã giảm giá mới (ví dụ: `SUMMER2026`).
- Quy định % giảm giá, số tiền giảm tối đa, ngày bắt đầu, ngày kết thúc.

**E. Quản lý Người dùng & Nhân viên:**
- Xem danh sách khách hàng.
- Thêm mới, phân quyền cho nhân viên quản trị (Staff/Admin).
- Khóa hoặc Mở khóa tài khoản có hành vi bất thường.

---

## 7. Thiết kế Cơ sở dữ liệu

Dự án sử dụng cơ sở dữ liệu quan hệ với nhiều bảng được liên kết chặt chẽ. Dưới đây là mô tả một số bảng cốt lõi (Core Tables):

1. **`users`**: Lưu thông tin đăng nhập, họ tên, email, mật khẩu mã hóa (BCrypt), vai trò (Role), ảnh đại diện.
2. **`hotels`**: Tên khách sạn, địa chỉ, mô tả, sao đánh giá trung bình.
3. **`rooms`**: Liên kết N-1 với `hotels`. Chứa tên loại phòng, giá, sức chứa.
4. **`room_images`**: Liên kết 1-N từ `rooms`. Lưu đường dẫn ảnh phòng.
5. **`amenities` / `hotel_amenities`**: Bảng tiện ích và bảng trung gian (N-N) để map các tiện ích vào khách sạn.
6. **`bookings`**: Bảng quan trọng nhất lưu trữ giao dịch. Chứa `user_id`, `room_id`, `check_in_date`, `check_out_date`, `total_price`, `status`, `payment_method`.
7. **`promotions`**: Lưu mã giảm giá, giới hạn áp dụng, thời gian.
8. **`reviews`**: Nhận xét từ khách hàng cho khách sạn/phòng, bao gồm `rating` và `comment`.
9. **`flights` / `tickets` / `cars` / `tour_activities`**: Các bảng mở rộng phục vụ cho hệ sinh thái du lịch đa dạng.
10. **`notifications`**: Hệ thống tin nhắn nội bộ thông báo trạng thái.

---

## 8. Hướng dẫn cài đặt và chạy dự án

Để triển khai dự án này tại local machine (máy cá nhân), vui lòng thực hiện theo các bước sau:

### 8.1 Yêu cầu hệ thống (Prerequisites)
- **Java Development Kit (JDK):** Yêu cầu bắt buộc **JDK 21** trở lên. (Có thể kiểm tra bằng lệnh `java -version`).
- **Cơ sở dữ liệu:** **MySQL Server** (Có thể dùng XAMPP, WAMP hoặc MySQL Workbench).
- **IDE (Khuyên dùng):** IntelliJ IDEA Ultimate / Community, Eclipse, hoặc Visual Studio Code.
- **Git:** Để clone dự án.

### 8.2 Các bước thực hiện

**Bước 1: Clone dự án từ Github**
Mở Terminal/Git Bash tại thư mục muốn lưu dự án và chạy lệnh:
```bash
git clone https://github.com/diemmy0909/diemmy09thuctaptotnghiep.git
```

**Bước 2: Cài đặt Cơ sở dữ liệu**
1. Mở MySQL/HeidiSQL/phpMyAdmin.
2. Tạo một Database mới với tên: `datphongkhachsan` (Encoding/Collation: `utf8mb4_general_ci`).
3. Import file dữ liệu mẫu đã cung cấp sẵn ở thư mục gốc: `datphongkhachsan.sql`.

**Bước 3: Cấu hình ứng dụng**
Truy cập vào đường dẫn: `TTTN_TranThiDiemMy_2123110204F/src/main/resources/application.properties`.
Chỉnh sửa lại các thông tin kết nối CSDL và Email cho phù hợp với máy của bạn:
```properties
# Cấu hình Database
spring.datasource.url=jdbc:mysql://localhost:3306/datphongkhachsan?useSSL=false
spring.datasource.username=root
spring.datasource.password=
# (Nếu bạn dùng password khác cho MySQL, hãy điền vào đây)

# Cấu hình Email (Để dùng chức năng OTP/Quên mật khẩu)
spring.mail.username=email_cua_ban@gmail.com
spring.mail.password=mat_khau_ung_dung_cua_ban
```

**Bước 4: Build và Chạy dự án (Run)**
* **Sử dụng IDE:** Mở thư mục `TTTN_TranThiDiemMy_2123110204F` bằng IntelliJ hoặc Eclipse. Chờ IDE tải thư viện Maven (Sync dependencies). Sau đó mở file `DemoApplication.java` và chọn Run.
* **Sử dụng Command Line:**
Mở Terminal, trỏ tới thư mục chứa `pom.xml` và chạy lệnh:
```bash
# Đối với Windows:
.\mvnw.cmd spring-boot:run

# Đối với Mac/Linux:
./mvnw spring-boot:run
```

**Bước 5: Truy cập Website**
Sau khi console báo `Started DemoApplication`, mở trình duyệt web và truy cập:
- **Trang dành cho Khách (User):** `http://localhost:8080`
- **Trang Quản trị (Admin):** `http://localhost:8080/admin/login`

*(Ghi chú: Tài khoản Admin mẫu có thể tạo mới trong CSDL phần Users hoặc đăng ký tài khoản và đổi role trong Database từ `USER` sang `ADMIN`).*

---

## 9. Bảo mật & Tích hợp

- **Xác thực và Phân quyền (Authentication & Authorization):** Dự án sử dụng bộ lọc bảo mật `SecurityFilterChain` của Spring Security. Các URL `/admin/**` bị chặn đối với khách thường, yêu cầu đăng nhập và có Role hợp lệ. Các mật khẩu lưu trong CSDL đều được băm bằng `BCryptPasswordEncoder`.
- **Upload File an toàn:** Tính năng upload hình ảnh cho Khách sạn, Phòng, và Avatar được cấu hình dung lượng tối đa (10MB) và lưu trữ trong thư mục `uploads/` biệt lập để ngăn chặn thực thi mã độc.
- **Xác thực Email OTP:** Ngăn chặn việc tạo tài khoản ảo (Spam) bằng việc gửi OTP thực tế thông qua giao thức SMTP đến hòm thư người dùng trước khi kích hoạt tài khoản.

---

## 10. Hình ảnh Giao diện (Demo)

*(Sinh viên có thể thêm Screenshot trực tiếp vào thư mục Github và cập nhật đường dẫn ảnh vào phần này)*

1. **Trang Chủ (Home Page):**  
![Trang Chủ](https://via.placeholder.com/800x400.png?text=Hinh+Anh+Trang+Chu)
2. **Danh sách Khách sạn (Hotel Listing):**  
![Danh Sách Khách Sạn](https://via.placeholder.com/800x400.png?text=Danh+Sach+Khach+San)
3. **Chi tiết Khách sạn và Chọn Phòng (Hotel Details):**  
![Chi Tiết Khách Sạn](https://via.placeholder.com/800x400.png?text=Chi+Tiet+Khach+San)
4. **Trang Quản trị (Admin Dashboard):**  
![Admin Dashboard](https://via.placeholder.com/800x400.png?text=Admin+Dashboard)

---

## 11. Hướng phát triển tương lai

Báo cáo Thực tập tốt nghiệp này đã hoàn thành những tính năng cốt lõi của một hệ thống OTA. Trong tương lai, dự án có thể được nâng cấp thêm với các chức năng:
- **Tích hợp cổng thanh toán thật:** Kết nối API VNPay, MoMo, ZaloPay, Stripe hoặc PayPal để xử lý tiền thật.
- **Gợi ý AI (Recommendation System):** Đề xuất khách sạn, tour du lịch dựa trên lịch sử tìm kiếm và sở thích của người dùng.
- **Chatbot Hỗ trợ:** Tích hợp AI Chatbot trả lời tự động các câu hỏi thường gặp của khách hàng.
- **Chuyển đổi sang Kiến trúc Microservices:** Tách phần Đặt vé máy bay và Đặt khách sạn thành các service độc lập để dễ dàng scale hệ thống khi lượng truy cập lớn.
- **Mobile App:** Phát triển thêm ứng dụng React Native / Flutter sử dụng chung Restful API từ hệ thống Spring Boot hiện tại.

---

## 12. Thông tin Sinh viên & Giảng viên hướng dẫn

- **Tên Đề tài:** Hệ thống Quản lý Đặt Dịch vụ Du lịch & Khách sạn Tổng hợp (Aluminum Travel)
- **Sinh viên thực hiện:** Trần Thị Diễm My
- **Mã số sinh viên (MSSV):** 2123110204F
- **Cơ sở đào tạo:** *(Cần bổ sung tên Trường Đại Học / Cơ sở đào tạo của bạn vào đây)*
- **Khoa/Ngành:** Công nghệ Thông tin / Kỹ thuật Phần mềm
- **Năm học:** 2025 - 2026

*Báo cáo này được thực hiện nhằm đáp ứng yêu cầu của đồ án Thực tập Tốt nghiệp chuyên ngành.*
