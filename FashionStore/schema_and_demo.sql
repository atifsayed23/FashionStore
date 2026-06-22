CREATE DATABASE IF NOT EXISTS fashion_store;
USE fashion_store;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    phone VARCHAR(20),
    address TEXT
);

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)
);

-- 3. Products Table
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10, 2),
    category_id INT,
    brand VARCHAR(100),
    image_url VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL
);

-- 4. Product Sizes Table
CREATE TABLE IF NOT EXISTS product_sizes (
    size_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    size VARCHAR(20),
    stock INT,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- 5. Product Variants Table
CREATE TABLE IF NOT EXISTS product_variants (
    variant_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    size VARCHAR(20),
    color VARCHAR(50),
    stock INT,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- 6. Cart Table
CREATE TABLE IF NOT EXISTS cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    variant_id INT,
    quantity INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE CASCADE
);

-- 7. Orders Table
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    total_amount DECIMAL(10, 2),
    status VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 8. Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    size_id INT,
    quantity INT,
    price DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (size_id) REFERENCES product_sizes(size_id) ON DELETE CASCADE
);

-- ==========================================
-- DEMO DATA TO POPULATE THE DATABASE
-- ==========================================

-- Add Demo Users
INSERT INTO users (name, email, password, phone, address) VALUES
('John Doe', 'john@example.com', 'password123', '1234567890', '123 Fashion St, NY'),
('Jane Smith', 'jane@example.com', 'password123', '0987654321', '456 Style Ave, LA');

-- Add Demo Categories
INSERT INTO categories (name) VALUES
('Men'),
('Women'),
('Kids');

-- Add Demo Products
INSERT INTO products (name, description, price, category_id, brand, image_url) VALUES
('Classic White T-Shirt', 'A high-quality basic cotton t-shirt.', 19.99, 1, 'Nike', 'assets/images/men1.jpg'),
('Summer Floral Dress', 'Lightweight floral dress for summer days.', 49.99, 2, 'Zara', 'assets/images/women1.jpg'),
('Kids Denim Jacket', 'Cool and comfortable denim jacket for kids.', 39.99, 3, 'Levi', 'assets/images/kids1.jpg');

-- Add Demo Product Variants
INSERT INTO product_variants (product_id, size, color, stock) VALUES
(1, 'M', 'White', 50),
(1, 'L', 'White', 30),
(2, 'S', 'Floral', 20),
(2, 'M', 'Floral', 15),
(3, 'Age 7-8', 'Blue', 40);

-- Add Demo Product Sizes
INSERT INTO product_sizes (product_id, size, stock) VALUES
(1, 'M', 50),
(1, 'L', 30),
(2, 'S', 20),
(2, 'M', 15),
(3, 'Age 7-8', 40);
