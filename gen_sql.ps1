$utf8NoBom = New-Object System.Text.UTF8Encoding $False

$cities = @('Đà Nẵng', 'Nha Trang', 'Phú Quốc', 'Hà Nội', 'TP. Hồ Chí Minh', 'Đà Lạt', 'Vũng Tàu', 'Quy Nhơn', 'Sapa', 'Hội An', 'Hạ Long', 'Huế', 'Phan Thiết', 'Cần Thơ', 'Đồng Hới')
$streets = @('Trần Phú', 'Nguyễn Trãi', 'Lê Lợi', 'Hai Bà Trưng', 'Lý Thường Kiệt', 'Điện Biên Phủ', 'Phạm Văn Đồng', 'Võ Nguyên Giáp', 'Nguyễn Huệ', 'Lê Duẩn', 'Hoàng Sa', 'Trường Sa', 'Nguyễn Đình Chiểu', 'Tôn Đức Thắng')
$descs = @(
    'Khách sạn tiêu chuẩn 5 sao với tầm nhìn hướng biển tuyệt đẹp, mang đến trải nghiệm nghỉ dưỡng đẳng cấp và sang trọng bậc nhất.',
    'Nằm ngay trung tâm thành phố, tiện lợi cho việc di chuyển. Không gian thiết kế hiện đại, ấm cúng và đầy đủ tiện nghi.',
    'Resort sinh thái nghỉ dưỡng bao quanh bởi thiên nhiên xanh mát, thích hợp cho các gia đình và cặp đôi tìm kiếm sự yên bình.',
    'Kiến trúc cổ điển kết hợp phong cách hoàng gia, cung cấp dịch vụ spa và hồ bơi vô cực độc quyền.',
    'Khách sạn lý tưởng cho những chuyến công tác với phòng hội nghị lớn, internet tốc độ cao và nhà hàng phục vụ 24/7.',
    'Trải nghiệm không gian sống thượng lưu với các phòng suite rộng rãi, ban công riêng và dịch vụ quản gia cá nhân.',
    'Nổi bật với thiết kế thân thiện môi trường, sử dụng năng lượng mặt trời và cung cấp thực đơn ăn chay hữu cơ.',
    'Điểm đến hoàn hảo cho kỳ nghỉ trăng mật, không gian lãng mạn, yên tĩnh, phục vụ bữa tối dưới ánh nến tại bãi biển.'
)

$sql = "USE datphongkhachsan;
SET NAMES utf8mb4;
"

for ($h = 3; $h -le 42; $h++) {
    $r1 = Get-Random -Maximum $streets.Length
    $r2 = Get-Random -Maximum $cities.Length
    $num = Get-Random -Minimum 10 -Maximum 999
    $addr = "$num $($streets[$r1]), $($cities[$r2])"
    $desc = $descs[Get-Random -Maximum $descs.Length]
    $sql += "UPDATE hotels SET address = '$addr', description = '$desc' WHERE id = $h;
"
}

$sql += "DELETE FROM tour_activities;
"
$activityNames = @('Tour Đảo Vip', 'Lặn Ngắm San Hô', 'Trải Nghiệm Dù Lượn', 'Vé Công Viên Nước', 'Khám Phá Hang Động', 'Tour Thành Phố', 'Vé Tắm Bùn Khoáng', 'Cắm Trại Rừng Thông', 'Du Thuyền Ngắm Hoàng Hôn')
$activityImages = Get-ChildItem -Path "d:\demo\demo\uploads\activities" -File | Select-Object -ExpandProperty Name

for ($i = 0; $i -lt $activityImages.Length -and $i -lt $activityNames.Length; $i++) {
    $name = $activityNames[$i]
    $img = "/uploads/activities/" + $activityImages[$i]
    $desc = "Trải nghiệm tuyệt vời với $name. Dịch vụ trọn gói chất lượng cao."
    $sql += "INSERT INTO tour_activities (title, description, image_url) VALUES ('$name', '$desc', '$img');
"
}

[System.IO.File]::WriteAllText("d:\demo\fix.sql", $sql, $utf8NoBom)
