import random
import os

cities = ['Đà Nẵng', 'Nha Trang', 'Phú Quốc', 'Hà Nội', 'TP. Hồ Chí Minh', 'Đà Lạt', 'Vũng Tàu', 'Quy Nhơn', 'Sapa', 'Hội An', 'Hạ Long', 'Huế', 'Phan Thiết', 'Cần Thơ', 'Đồng Hới']
streets = ['Trần Phú', 'Nguyễn Trãi', 'Lê Lợi', 'Hai Bà Trưng', 'Lý Thường Kiệt', 'Điện Biên Phủ', 'Phạm Văn Đồng', 'Võ Nguyên Giáp', 'Nguyễn Huệ', 'Lê Duẩn', 'Hoàng Sa', 'Trường Sa', 'Nguyễn Đình Chiểu', 'Tôn Đức Thắng']
descs = [
    'Khách sạn tiêu chuẩn 5 sao với tầm nhìn hướng biển tuyệt đẹp, mang đến trải nghiệm nghỉ dưỡng đẳng cấp và sang trọng bậc nhất.',
    'Nằm ngay trung tâm thành phố, tiện lợi cho việc di chuyển. Không gian thiết kế hiện đại, ấm cúng và đầy đủ tiện nghi.',
    'Resort sinh thái nghỉ dưỡng bao quanh bởi thiên nhiên xanh mát, thích hợp cho các gia đình và cặp đôi tìm kiếm sự yên bình.',
    'Kiến trúc cổ điển kết hợp phong cách hoàng gia, cung cấp dịch vụ spa và hồ bơi vô cực độc quyền.',
    'Khách sạn lý tưởng cho những chuyến công tác với phòng hội nghị lớn, internet tốc độ cao và nhà hàng phục vụ 24/7.',
    'Trải nghiệm không gian sống thượng lưu với các phòng suite rộng rãi, ban công riêng và dịch vụ quản gia cá nhân.',
    'Nổi bật với thiết kế thân thiện môi trường, sử dụng năng lượng mặt trời và cung cấp thực đơn ăn chay hữu cơ.',
    'Điểm đến hoàn hảo cho kỳ nghỉ trăng mật, không gian lãng mạn, yên tĩnh, phục vụ bữa tối dưới ánh nến tại bãi biển.'
]

sql = "USE datphongkhachsan;\nSET NAMES utf8mb4;\n"
for h in range(3, 43):
    addr = f"{random.randint(10, 999)} {random.choice(streets)}, {random.choice(cities)}"
    desc = random.choice(descs)
    sql += f"UPDATE hotels SET address = '{addr}', description = '{desc}' WHERE id = {h};\n"

sql += "DELETE FROM tour_activities;\n"
activityNames = ['Tour Đảo Vip', 'Lặn Ngắm San Hô', 'Trải Nghiệm Dù Lượn', 'Vé Công Viên Nước', 'Khám Phá Hang Động', 'Tour Thành Phố', 'Vé Tắm Bùn Khoáng', 'Cắm Trại Rừng Thông', 'Du Thuyền Ngắm Hoàng Hôn']

try:
    activityImages = os.listdir('d:/demo/demo/uploads/activities')
except:
    activityImages = []

for i in range(min(len(activityImages), len(activityNames))):
    name = activityNames[i]
    img = "/uploads/activities/" + activityImages[i]
    desc = f"Trải nghiệm tuyệt vời với {name}. Dịch vụ trọn gói chất lượng cao."
    sql += f"INSERT INTO tour_activities (title, description, image_url) VALUES ('{name}', '{desc}', '{img}');\n"

with open("d:/demo/fix.sql", "w", encoding="utf-8") as f:
    f.write(sql)
