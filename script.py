
import mysql.connector
import random
import os

conn = mysql.connector.connect(host='localhost', user='root', password='', database='datphongkhachsan')
cursor = conn.cursor()

names = ['Sunset Marina Hotel', 'Ivy Boutique Hotel', 'Palm Breeze Resort', 'Orchid Luxury Hotel', 'Coral Bay Hotel', 'Evergreen Residence', 'White Pearl Hotel', 'Mountain View Lodge', 'Sky Garden Suites', 'Pearl River Hotel', 'Magnolia Hotel', 'Green Valley Resort', 'Nova Crown Hotel', 'Horizon Plaza Hotel', 'Blue Orchid Resort', 'Heritage Palace Hotel', 'Crystal Lake Resort', 'The Imperial Nest Hotel', 'Sapphire Garden Resort', 'Azure Vista Hotel', 'Rosewood Signature Hotel', 'Seaside Harmony Resort']
cities = ['Da Nang', 'Nha Trang', 'Phu Quoc', 'Ha Noi', 'HCM', 'Da Lat', 'Vung Tau', 'Quy Nhon', 'Sapa', 'Hoi An']

try:
    images = os.listdir('d:/demo/demo/uploads/hotels')
except:
    images = []

for i in range(40):
    name = random.choice(names) + ' ' + str(random.randint(1, 100))
    address = str(random.randint(10, 999)) + ' Duong Tran Phu, ' + random.choice(cities)
    desc = 'Khach san cao cap day du tien nghi, view dep.'
    stars = random.randint(3, 5)
    img = '/uploads/hotels/' + images[i % len(images)] if images else None
    
    cursor.execute('INSERT INTO hotels (name, address, description, star_rating, image_url) VALUES (%s, %s, %s, %s, %s)', (name, address, desc, stars, img))

conn.commit()
cursor.close()
conn.close()
print('Inserted 40 hotels!')

