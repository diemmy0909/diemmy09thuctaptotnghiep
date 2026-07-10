import random
import os

sql = "USE datphongkhachsan;\nSET NAMES utf8mb4;\n"

categories = ['AMUSEMENT_PARK', 'CABLE_CAR', 'MUSEUM', 'PARK']
titles = ['Vé VinWonders Phú Quốc', 'Cáp treo Bà Nà Hills', 'Vé Bảo tàng Dân tộc học', 'Vé Safari Phú Quốc']
locations = ['Phú Quốc', 'Đà Nẵng', 'Hà Nội', 'Phú Quốc']
prices_adult = [900000, 1150000, 50000, 650000]
prices_child = [600000, 850000, 20000, 450000]

try:
    images = os.listdir('d:/demo/demo/uploads/activities')
except:
    images = []

sql += "DELETE FROM tickets;\n"

for i in range(4):
    img = f"/uploads/activities/{images[i % len(images)]}" if images else ""
    sql += f"INSERT INTO tickets (title, location, category, adult_price, child_price, description, image_url) VALUES ('{titles[i]}', '{locations[i]}', '{categories[i]}', {prices_adult[i]}, {prices_child[i]}, 'Trải nghiệm vui chơi giải trí hàng đầu.', '{img}');\n"

with open("d:/demo/fix_tickets.sql", "w", encoding="utf-8") as f:
    f.write(sql)
