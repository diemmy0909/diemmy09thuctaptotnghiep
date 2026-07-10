import random

sql = "USE datphongkhachsan;\n"
amenities = [1, 2, 3, 4]

for h in range(3, 43):
    num_amenities = random.randint(2, 4)
    selected = random.sample(amenities, num_amenities)
    for a in selected:
        sql += f"INSERT IGNORE INTO hotel_amenities (hotel_id, amenity_id) VALUES ({h}, {a});\n"

with open("d:/demo/fix_amenities.sql", "w", encoding="utf-8") as f:
    f.write(sql)
