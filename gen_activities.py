sql = "USE datphongkhachsan;\nSET NAMES utf8mb4;\n"
sql += "DELETE FROM tour_activities;\n"

titles = [
    'Khám Phá Phố Cổ Hội An', 'Tour Tràng An - Bái Đính', 'Lặn Ngắm San Hô Phú Quốc', 
    'Chinh Phục Đỉnh Fansipan', 'Du Thuyền Trên Vịnh Hạ Long', 'Trải Nghiệm Đồi Cát Bay', 
    'Khám Phá Hang Sơn Đoòng', 'Tham Quan Hoàng Thành Huế', 'Chợ Nổi Cái Răng', 
    'Cắm Trại Đà Lạt'
]

descriptions = [
    'Dạo bước qua những con phố lung linh ánh đèn lồng, thưởng thức các món ăn đặc sản địa phương và cảm nhận vẻ đẹp hoài cổ của di sản thế giới Hội An.',
    'Hành trình tâm linh và khám phá thiên nhiên kỳ vĩ tại quần thể danh thắng Tràng An và ngôi chùa lớn nhất Đông Nam Á - Bái Đính.',
    'Đắm mình trong làn nước trong xanh, khám phá thế giới đại dương đầy màu sắc với hàng ngàn loài san hô và sinh vật biển độc đáo.',
    'Vượt qua giới hạn bản thân, chạm tay vào Nóc nhà Đông Dương, ngắm nhìn toàn cảnh núi non hùng vĩ từ độ cao 3.143m.',
    'Tận hưởng kỳ nghỉ sang trọng trên du thuyền 5 sao, thư giãn giữa hàng ngàn hòn đảo đá vôi kỳ thú của kỳ quan thiên nhiên thế giới.',
    'Trượt cát, chạy xe địa hình và ngắm hoàng hôn tuyệt đẹp trên những cồn cát vàng óng ả mênh mông như sa mạc thu nhỏ.',
    'Chuyến thám hiểm kỳ thú vào hang động lớn nhất hành tinh, chiêm ngưỡng hệ sinh thái ngầm độc nhất vô nhị.',
    'Tìm hiểu lịch sử hào hùng của triều đại phong kiến cuối cùng Việt Nam qua những công trình kiến trúc cung đình đồ sộ.',
    'Hòa mình vào không khí nhộn nhịp của phiên chợ trên sông lớn nhất miền Tây, thưởng thức trái cây tươi ngon ngay trên ghe thuyền.',
    'Trải nghiệm đêm lửa trại giữa rừng thông mộng mơ, đón bình minh trong sương sớm và thưởng thức ly cà phê nóng hổi giữa tiết trời se lạnh.'
]

for i in range(10):
    sql += f"INSERT INTO tour_activities (title, description, image_url) VALUES ('{titles[i]}', '{descriptions[i]}', NULL);\n"

with open("d:/demo/fix_activities.sql", "w", encoding="utf-8") as f:
    f.write(sql)
