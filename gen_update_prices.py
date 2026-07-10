import random

def get_random_price(room_type):
    if room_type == 'STANDARD':
        return random.randrange(800, 2001, 50) * 1000
    elif room_type == 'DELUXE':
        return random.randrange(2200, 4501, 50) * 1000
    elif room_type == 'VIP':
        return random.randrange(5000, 7501, 100) * 1000
    return 1000000

sql = "USE datphongkhachsan;\n"

# There are 120 rooms. We'll just generate update queries for all 120 ids (we know they are 1-120 and have a predictable pattern or we can just randomly guess the room types if we don't know them).
# Wait, I know there are 40 hotels, each with 3 rooms (ID 1-120). 
# ID % 3 == 1 -> STANDARD
# ID % 3 == 2 -> DELUXE
# ID % 3 == 0 -> VIP

for i in range(1, 121):
    if i % 3 == 1:
        room_type = 'STANDARD'
    elif i % 3 == 2:
        room_type = 'DELUXE'
    else:
        room_type = 'VIP'
        
    price = get_random_price(room_type)
    sql += f"UPDATE rooms SET price = {price} WHERE id = {i};\n"

with open("d:/demo/update_prices.sql", "w", encoding="utf-8") as f:
    f.write(sql)
