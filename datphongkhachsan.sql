-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for datphongkhachsan
CREATE DATABASE IF NOT EXISTS `datphongkhachsan` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `datphongkhachsan`;

-- Dumping structure for table datphongkhachsan.amenities
CREATE TABLE IF NOT EXISTS `amenities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKm492b3r7aa2c88y156nvic16s` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.amenities: ~4 rows (approximately)
INSERT INTO `amenities` (`id`, `description`, `icon`, `name`) VALUES
	(1, 'WiFi miễn phí', 'wifi', 'WiFi'),
	(2, 'Hồ bơi ngoài trời', 'pool', 'Hồ bơi'),
	(3, 'Bãi đỗ xe miễn phí', 'parking', 'Bãi đỗ xe'),
	(4, 'Ăn sáng buffet', 'breakfast', 'Buffet sáng');

-- Dumping structure for table datphongkhachsan.bookings
CREATE TABLE IF NOT EXISTS `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_code` varchar(255) NOT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `status` enum('CANCELLED','CHECKED_IN','CHECKED_OUT','CONFIRMED','PENDING') DEFAULT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `user_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKq97166k18hklq6ls46osbrftx` (`booking_code`),
  KEY `FKeyog2oic85xg7hsu2je2lx3s6` (`user_id`),
  KEY `FKrgoycol97o21kpjodw1qox4nc` (`room_id`),
  CONSTRAINT `FKeyog2oic85xg7hsu2je2lx3s6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrgoycol97o21kpjodw1qox4nc` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.bookings: ~0 rows (approximately)
INSERT INTO `bookings` (`id`, `booking_code`, `check_in_date`, `check_out_date`, `created_at`, `status`, `total_amount`, `user_id`, `room_id`) VALUES
	(1, 'BK20260001', '2026-05-21', '2026-05-23', '2026-05-18 23:32:33.245939', 'PENDING', 3000000.00, 4, 2),
	(2, 'BK1779123530104', '2026-05-19', '2026-05-20', '2026-05-18 23:58:50.105419', 'PENDING', 1500000.00, 1, 2),
	(3, 'BK1779123572159', '2026-05-19', '2026-05-20', '2026-05-18 23:59:32.159473', 'CONFIRMED', 3000000.00, 1, 3);

-- Dumping structure for table datphongkhachsan.contact_messages
CREATE TABLE IF NOT EXISTS `contact_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `message` text,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.contact_messages: ~0 rows (approximately)

-- Dumping structure for table datphongkhachsan.hotels
CREATE TABLE IF NOT EXISTS `hotels` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `description` text,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `star_rating` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.hotels: ~0 rows (approximately)
INSERT INTO `hotels` (`id`, `address`, `description`, `image_url`, `name`, `star_rating`) VALUES
	(1, '123 Đường Biển, Đà Nẵng', 'Khách sạn 5 sao view biển, phục vụ chu đáo', 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800', 'Khách sạn Biển Xanh', 5),
	(2, '08/11', 'úhdfjkglksdjfg', '/uploads/hotels/6fd602e1-8389-4386-9209-369acd2470f0.png', 'Khách Sạn ', 3);

-- Dumping structure for table datphongkhachsan.hotel_amenities
CREATE TABLE IF NOT EXISTS `hotel_amenities` (
  `hotel_id` bigint NOT NULL,
  `amenity_id` bigint NOT NULL,
  PRIMARY KEY (`hotel_id`,`amenity_id`),
  KEY `FK8briqcy2j5b6td3kpi2bbq03w` (`amenity_id`),
  CONSTRAINT `FK5v984hm7iyuvyccsgboplo7ii` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FK8briqcy2j5b6td3kpi2bbq03w` FOREIGN KEY (`amenity_id`) REFERENCES `amenities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.hotel_amenities: ~4 rows (approximately)
INSERT INTO `hotel_amenities` (`hotel_id`, `amenity_id`) VALUES
	(1, 1),
	(2, 1),
	(1, 2),
	(1, 3),
	(1, 4);

-- Dumping structure for table datphongkhachsan.payments
CREATE TABLE IF NOT EXISTS `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(12,2) NOT NULL,
  `method` enum('BANK_TRANSFER','CARD','CASH','MOMO','VNPAY') DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_code` varchar(255) NOT NULL,
  `status` enum('COMPLETED','PENDING','REFUNDED') DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKn939tpje3kshdfk6pbi7ewwj7` (`payment_code`),
  KEY `FKc52o2b1jkxttngufqp3t7jr3h` (`booking_id`),
  CONSTRAINT `FKc52o2b1jkxttngufqp3t7jr3h` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.payments: ~0 rows (approximately)
INSERT INTO `payments` (`id`, `amount`, `method`, `paid_at`, `payment_code`, `status`, `booking_id`) VALUES
	(1, 3000000.00, 'MOMO', NULL, 'PAY20260001', 'PENDING', 1),
	(2, 3000000.00, 'CASH', '2026-05-18 23:59:39.927522', 'PAY9E405091', 'COMPLETED', 3);

-- Dumping structure for table datphongkhachsan.promotions
CREATE TABLE IF NOT EXISTS `promotions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_amount` decimal(12,2) DEFAULT NULL,
  `discount_percent` int DEFAULT NULL,
  `end_date` date NOT NULL,
  `start_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjdho73ymbyu46p2hh562dk4kk` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.promotions: ~2 rows (approximately)
INSERT INTO `promotions` (`id`, `active`, `code`, `description`, `discount_amount`, `discount_percent`, `end_date`, `start_date`) VALUES
	(1, b'1', 'SUMMER2026', 'Giảm 20% mùa hè', NULL, 20, '2026-08-18', '2026-05-18'),
	(2, b'1', '35941', 'jksdaflkj', 100000.00, 12, '2026-05-27', '2026-05-20');

-- Dumping structure for table datphongkhachsan.reviews
CREATE TABLE IF NOT EXISTS `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved` bit(1) NOT NULL,
  `comment` text,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int NOT NULL,
  `hotel_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb9igk5exfb4knqklcvka6cdhx` (`hotel_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FKb9igk5exfb4knqklcvka6cdhx` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.reviews: ~0 rows (approximately)
INSERT INTO `reviews` (`id`, `approved`, `comment`, `created_at`, `rating`, `hotel_id`, `user_id`) VALUES
	(1, b'0', 'Khách sạn rất đẹp, nhân viên thân thiện!', '2026-05-18 23:32:33.299947', 5, 1, 4);

-- Dumping structure for table datphongkhachsan.rooms
CREATE TABLE IF NOT EXISTS `rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) DEFAULT NULL,
  `max_guests` int NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `room_type` enum('DELUXE','STANDARD','VIP') NOT NULL,
  `status` enum('AVAILABLE','MAINTENANCE','OCCUPIED') DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp5lufxy0ghq53ugm93hdc941k` (`hotel_id`),
  CONSTRAINT `FKp5lufxy0ghq53ugm93hdc941k` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.rooms: ~3 rows (approximately)
INSERT INTO `rooms` (`id`, `image_url`, `max_guests`, `price`, `room_type`, `status`, `hotel_id`) VALUES
	(1, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800', 2, 800000.00, 'STANDARD', 'AVAILABLE', 1),
	(2, 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800', 3, 1500000.00, 'DELUXE', 'AVAILABLE', 1),
	(3, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800', 4, 3000000.00, 'VIP', 'AVAILABLE', 1);

-- Dumping structure for table datphongkhachsan.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `locked` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('CUSTOMER','MANAGER','STAFF','SUPER_ADMIN') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table datphongkhachsan.users: ~4 rows (approximately)
INSERT INTO `users` (`id`, `created_at`, `email`, `full_name`, `locked`, `password`, `phone`, `role`) VALUES
	(1, '2026-05-18 23:32:33.045355', 'admin@hotel.com', 'nhuy', b'0', '$2a$10$qVLD4pU4Nd4c4cE1So5Rtuj4VDF2DPnaTHIer8P7PO4KXX4J.n31q', '0901234567', 'SUPER_ADMIN'),
	(2, '2026-05-18 23:32:33.110626', 'manager@hotel.com', 'Nguyễn Văn Manager', b'0', '$2a$10$qVLD4pU4Nd4c4cE1So5Rtuj4VDF2DPnaTHIer8P7PO4KXX4J.n31q', '0902222222', 'MANAGER'),
	(3, '2026-05-18 23:32:33.120423', 'staff@hotel.com', 'Trần Thị Staff', b'0', '$2a$10$qVLD4pU4Nd4c4cE1So5Rtuj4VDF2DPnaTHIer8P7PO4KXX4J.n31q', '0903333333', 'STAFF'),
	(4, '2026-05-18 23:32:33.129978', 'khach@email.com', 'Lê Khách Hàng', b'0', '$2a$10$qVLD4pU4Nd4c4cE1So5Rtuj4VDF2DPnaTHIer8P7PO4KXX4J.n31q', '0912345678', 'CUSTOMER');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
