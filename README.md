# Aluminum Travel - Đặt Dịch vụ Du lịch & Khách sạn

Dự án Website thương mại điện tử chuyên biệt trong lĩnh vực du lịch, hỗ trợ khách hàng tìm kiếm và đặt phòng khách sạn, vé máy bay, vé tham quan và thuê xe dễ dàng.

## Công nghệ sử dụng
- **Backend:** Java 21, Spring Boot 3, Spring Security, Hibernate/JPA.
- **Frontend:** HTML/CSS/JS, Thymeleaf, Bootstrap.
- **Database:** MySQL 8.

## Hướng dẫn chạy dự án
1. Tạo database MySQL tên `datphongkhachsan` và import file `datphongkhachsan.sql`.
2. Sửa lại username/password database trong file `src/main/resources/application.properties` nếu cần.
3. Mở Terminal tại thư mục gốc và chạy lệnh:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
4. Truy cập web tại: `http://localhost:8080` (Trang khách hàng) hoặc `http://localhost:8080/admin/login` (Trang quản trị).

---
*Dự án Thực tập Tốt nghiệp của sinh viên Trần Thị Diễm My - MSSV: 2123110204F.*
