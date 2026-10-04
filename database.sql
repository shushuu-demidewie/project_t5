-- ========================================================
-- CƠ SỞ DỮ LIỆU: project_quan_ts
-- HỆ QUẢN TRỊ CSDL: MySQL / phpMyAdmin (XAMPP)
-- ========================================================

CREATE DATABASE IF NOT EXISTS `project_quan_ts` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `project_quan_ts`;

-- 1. BẢNG USER (Người dùng hệ thống / Thu ngân)
DROP TABLE IF EXISTS `order_details`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'STAFF'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. BẢNG CATEGORIES (Danh mục đồ uống)
CREATE TABLE `categories` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `category_name` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. BẢNG PRODUCTS (Danh sách món / Đồ uống)
CREATE TABLE `products` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `category_id` INT NOT NULL,
    `product_name` VARCHAR(150) NOT NULL,
    `price` DOUBLE NOT NULL DEFAULT 0,
    `status` VARCHAR(50) NOT NULL DEFAULT 'Còn hàng',
    CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. BẢNG ORDERS (Hóa đơn bán hàng)
CREATE TABLE `orders` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `order_code` VARCHAR(50) NOT NULL UNIQUE,
    `user_id` INT NOT NULL,
    `order_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `total_amount` DOUBLE NOT NULL DEFAULT 0,
    CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. BẢNG ORDER_DETAILS (Chi tiết từng món trong hóa đơn)
CREATE TABLE `order_details` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `order_id` INT NOT NULL,
    `product_id` INT NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    `unit_price` DOUBLE NOT NULL DEFAULT 0,
    CONSTRAINT `fk_order_details_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_details_product` FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================================
-- DỮ LIỆU MẪU ĐỂ CHẠY THỬ NGHIỆM
-- ========================================================

-- Dữ liệu mẫu bảng user (Tài khoản: admin / 123456 hoặc staff / 123)
INSERT INTO `user` (`username`, `password`, `full_name`, `role`) VALUES
('admin', '123456', 'Quản Trị Viên', 'ADMIN'),
('staff01', '123', 'Nguyễn Thị Bích', 'STAFF');

-- Dữ liệu mẫu bảng categories
INSERT INTO `categories` (`category_name`) VALUES
('Trà Sữa Truyền Thống'),
('Trà Hoa Quả / Trái Cây'),
('Cà Phê'),
('Đá Xay & Sinh Tố'),
('Topping');

-- Dữ liệu mẫu bảng products
INSERT INTO `products` (`category_id`, `product_name`, `price`, `status`) VALUES
(1, 'Trà Sữa Trân Châu Đường Đen', 35000, 'Còn hàng'),
(1, 'Trà Sữa Oolong Nướng', 40000, 'Còn hàng'),
(1, 'Trà Sữa Thái Xanh', 30000, 'Còn hàng'),
(1, 'Trà Sữa Khoai Môn', 35000, 'Còn hàng'),
(2, 'Trà Đào Cam Sả', 45000, 'Còn hàng'),
(2, 'Trà Dâu Tằm Macchiato', 42000, 'Còn hàng'),
(2, 'Trà Xoài Nhiệt Đới', 38000, 'Còn hàng'),
(3, 'Cà Phê Sữa Đá Sài Gòn', 28000, 'Còn hàng'),
(3, 'Bạc Xỉu', 32000, 'Còn hàng'),
(3, 'Cà Phê Muối', 35000, 'Còn hàng'),
(4, 'Matcha Đá Xay', 48000, 'Còn hàng'),
(4, 'Socola Cookie Đá Xay', 45000, 'Còn hàng'),
(5, 'Trân Châu Đen', 5000, 'Còn hàng'),
(5, 'Trân Châu Trắng 3Q', 8000, 'Còn hàng'),
(5, 'Thạch Kem Phô Mai', 10000, 'Còn hàng');
