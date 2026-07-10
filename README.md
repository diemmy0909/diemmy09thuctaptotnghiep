# Aluminum Travel - Hệ thống Quản lý Đặt Dịch vụ Du lịch & Khách sạn

Dự án **Aluminum Travel** là một nền tảng thương mại điện tử chuyên biệt trong lĩnh vực du lịch và lưu trú. Trang web cung cấp một giải pháp toàn diện giúp khách hàng dễ dàng tìm kiếm, so sánh và đặt nhiều loại dịch vụ khác nhau trong cùng một chuyến đi, mô phỏng các trang web OTA (Online Travel Agent) lớn hiện nay.

## 🌟 Chức năng nổi bật

### 1. Dành cho Khách hàng (User)
- **Đăng ký & Đăng nhập:** Xác thực người dùng, bảo mật tài khoản.
- **Khách sạn & Lưu trú:** Tìm kiếm khách sạn theo địa điểm, lọc theo giá và đánh giá. Xem chi tiết hình ảnh, tiện ích, và tiến hành đặt phòng dễ dàng.
- **Dịch vụ di chuyển:** Hỗ trợ tra cứu và đặt vé máy bay, cũng như thuê xe du lịch (ô tô, xe máy).
- **Tour & Hoạt động:** Khám phá và đặt vé các hoạt động vui chơi giải trí tại các địa điểm nổi tiếng.
- **Giỏ hàng & Khuyến mãi:** Áp dụng mã giảm giá, xem lại lịch sử đặt dịch vụ, đánh giá trải nghiệm sau chuyến đi.

### 2. Dành cho Quản trị viên (Admin)
- **Dashboard:** Thống kê tổng quan số lượng đơn hàng, doanh thu trực quan.
- **Quản lý Dịch vụ:** Thêm, sửa, xóa thông tin khách sạn, các loại phòng, chuyến bay, và các tiện ích đi kèm.
- **Xử lý Đơn hàng:** Theo dõi và cập nhật trạng thái các đơn đặt phòng, vé tham quan của khách hàng.
- **Quản lý User & Khuyến mãi:** Quản lý danh sách người dùng, thiết lập các chương trình mã giảm giá.

## 💻 Công nghệ sử dụng
Dự án được xây dựng dựa trên kiến trúc Monolithic chuẩn, sử dụng các công nghệ hiện đại:
- **Backend:** Java 21, Spring Boot 3.x, Spring Web MVC, Spring Data JPA (Hibernate).
- **Security:** Spring Security 6, mã hóa mật khẩu BCrypt.
- **Frontend:** Server-Side Rendering với Thymeleaf, kết hợp HTML5, CSS3, JavaScript, và Bootstrap 5.
- **Database:** MySQL 8.x.

## 🚀 Hướng dẫn cài đặt và chạy dự án

1. **Chuẩn bị CSDL:** Tạo database trong MySQL với tên `datphongkhachsan` (Encoding: `utf8mb4`). Sau đó import file `datphongkhachsan.sql` có sẵn trong thư mục gốc vào database vừa tạo.
2. **Cấu hình:** Mở file `src/main/resources/application.properties`, chỉnh sửa lại `username` và `password` của MySQL cho phù hợp với máy của bạn.
3. **Chạy ứng dụng:** Mở Terminal/Command Prompt tại thư mục gốc của dự án và gõ lệnh:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
4. **Trải nghiệm:** Truy cập web qua trình duyệt:
   - Trang giao diện khách hàng: `http://localhost:8080`
   - Trang quản lý (Admin): `http://localhost:8080/admin/login`

---
*Đề tài Thực tập Tốt nghiệp - Sinh viên thực hiện: Trần Thị Diễm My (MSSV: 2123110204F)*
