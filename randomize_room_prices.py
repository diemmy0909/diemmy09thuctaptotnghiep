import mysql.connector
import random

def get_random_price(room_type):
    if room_type == 'STANDARD':
        return random.randrange(800, 2001, 50) * 1000
    elif room_type == 'DELUXE':
        return random.randrange(2200, 4501, 50) * 1000
    elif room_type == 'VIP':
        return random.randrange(5000, 7501, 100) * 1000
    return 1000000

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="datphongkhachsan"
    )
    cursor = conn.cursor()
    
    cursor.execute("SELECT id, room_type FROM rooms")
    rooms = cursor.fetchall()
    
    update_queries = []
    for room_id, room_type in rooms:
        price = get_random_price(room_type)
        update_queries.append(f"UPDATE rooms SET price = {price} WHERE id = {room_id};")
    
    for query in update_queries:
        cursor.execute(query)
        
    conn.commit()
    print(f"Successfully updated {len(rooms)} rooms with diverse prices.")
    
except Exception as e:
    print(f"Error: {e}")
finally:
    if 'conn' in locals() and conn.is_connected():
        cursor.close()
        conn.close()
