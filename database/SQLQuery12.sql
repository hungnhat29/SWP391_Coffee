use
master
DROP
DATABASE G3P_Coffee

DROP PROCEDURE IF EXISTS G3P_Coffee;
GO

CREATE
DATABASE G3P_Coffee;
GO
USE G3P_Coffee;
CREATE TABLE Users
(
    id         INT PRIMARY KEY IDENTITY(1,1),
    name       VARCHAR(255)        NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    phone      VARCHAR(20),
    address    TEXT,
    role       VARCHAR(50),
    created_at DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at DATETIME DEFAULT (CURRENT_TIMESTAMP),
    CONSTRAINT chk_phone CHECK (PATINDEX('%[^0-9]%', phone) = 0)
);
GO
INSERT INTO Users (id, name, email, password, phone, address, role, created_at, updated_at)
VALUES
(1, 'John Doe', 'john@example.com', 'hashed_pwd_1', '1234567890', '123 Main St', 'customer', GETDATE(), GETDATE()),
(2, 'Jane Smith', 'jane@example.com', 'hashed_pwd_2', '9876543210', '456 Elm St', 'admin', GETDATE(), GETDATE()),
(3, 'Alice Johnson', 'alice@example.com', 'hashed_pwd_3', '1122334455', '789 Pine St', 'customer', GETDATE(), GETDATE()),
(4, 'Bob Brown', 'bob@example.com', 'hashed_pwd_4', '2233445566', '321 Maple St', 'customer', GETDATE(), GETDATE()),
(5, 'Charlie Davis', 'charlie@example.com', 'hashed_pwd_5', '3344556677', '654 Oak St', 'customer', GETDATE(), GETDATE()),
(6, 'Emily White', 'emily@example.com', 'hashed_pwd_6', '4455667788', '987 Cedar St', 'customer', GETDATE(), GETDATE()),
(7, 'Frank Harris', 'frank@example.com', 'hashed_pwd_7', '5566778899', '135 Birch St', 'manager', GETDATE(), GETDATE()),
(8, 'Grace Lewis', 'grace@example.com', 'hashed_pwd_8', '6677889900', '246 Walnut St', 'customer', GETDATE(), GETDATE()),
(9, 'Hannah Green', 'hannah@example.com', 'hashed_pwd_9', '7788990011', '369 Cherry St', 'customer', GETDATE(), GETDATE()),
(10, 'Ian Black', 'ian@example.com', 'hashed_pwd_10', '8899001122', '159 Ash St', 'customer', GETDATE(), GETDATE()),
(11, 'Alice Smith', 'alice2@example.com', 'hashed_pwd_2', '9876543210', '456 Elm St', 'manager', GETDATE(), GETDATE()),
(12, 'Bob Johnson', 'bob2@example.com', 'hashed_pwd_3', '1122334455', '789 Oak St', 'manager', GETDATE(), GETDATE()),
(13, 'Eve Carter', 'eve@example.com', 'hashed_pwd_4', '6677889900', '321 Pine St', 'manager', GETDATE(), GETDATE()),
(14, 'Charlie Davis', 'charlie2@example.com', 'hashed_pwd_5', '4455667788', '654 Maple St', 'manager', GETDATE(), GETDATE()),
(15, 'Diana Lewis', 'diana@example.com', 'hashed_pwd_6', '9988776655', '987 Cedar St', 'manager', GETDATE(), GETDATE());
GO
CREATE TABLE Roles
(
    id          INT PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL,
    permissions TEXT,
    user_id     INT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id)
);
GO
--INSERT INTO Roles (id, role_name, permissions)
--VALUES
--(1, 'admin', 'all_access'),
--(2, 'manager', 'create_order, manage_inventory, manage_revenue'),
--(3, 'barista', 'create_order, report_inventory'),
--(4, 'shipper', 'receive_order, transport_order'),
--(5, 'customer', 'view_menu, place_order'),
--(6, 'guest', 'view_only');
--GO
INSERT INTO Roles (id, role_name, user_id, permissions)
VALUES
(1, 'admin', 2, 'all_access'),
(2, 'manager', 7, 'create_order, manage_inventory, manage_revenue'),
(3, 'manager', 11, 'create_order, manage_inventory, manage_revenue'),
(4, 'manager', 12, 'create_order, manage_inventory, manage_revenue'),
(5, 'manager', 13, 'create_order, manage_inventory, manage_revenue'),
(6, 'manager', 14, 'create_order, manage_inventory, manage_revenue'),
(7, 'manager', 15, 'create_order, manage_inventory, manage_revenue');


--(3, 'barista', 103, 'create_order, report_inventory'),
--(4, 'shipper', 104, 'receive_order, transport_order'),
--(5, 'customer', 105, 'view_menu, place_order'),

CREATE TABLE Memberships
(
    id              INT PRIMARY KEY IDENTITY(1,1),
    user_id         INT NOT NULL,
    membership_type VARCHAR(50),
    start_date      DATE,
    expiry_date     DATE,
    rank            VARCHAR(20),
    points          INT,
    status          VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES Users (id)
);
GO
INSERT INTO Memberships (id, user_id, membership_type, start_date, expiry_date, rank, points, status)
VALUES
(1, 1, 'premium', '2025-01-01', '2025-12-31', 'Gold', 5000, 'active'),
(2, 3, 'basic', '2025-01-01', '2025-06-30', 'Silver', 2000, 'active'),
(3, 5, 'basic', '2025-01-01', '2025-06-30', 'Bronze', 1000, 'inactive'),
(4, 7, 'premium', '2025-01-01', '2025-12-31', 'Gold', 7000, 'active'),
(5, 8, 'basic', '2025-01-01', '2025-06-30', 'Silver', 3000, 'active');
GO

CREATE TABLE Categories
(
    id                 INT PRIMARY KEY IDENTITY(1,1),
    name               VARCHAR(255) NOT NULL,
    description        TEXT,
    parent_category_id INT,
    created_at         DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at         DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (parent_category_id) REFERENCES Categories (id)
);
GO
INSERT INTO Categories (id, name, description, parent_category_id, created_at, updated_at)
VALUES
(1, 'Coffee', 'Freshly brewed coffee', NULL, GETDATE(), GETDATE()),
(2, 'Tea', 'A variety of teas', NULL, GETDATE(), GETDATE()),
(3, 'Pastries', 'Baked goods and desserts', NULL, GETDATE(), GETDATE()),
(4, 'Sandwiches', 'Savory and fresh sandwiches', NULL, GETDATE(), GETDATE());
GO

CREATE TABLE Vouchers
(
    voucher_id   INT PRIMARY KEY IDENTITY(1,1),
    voucher_code VARCHAR(50)    NOT NULL UNIQUE,
    description  TEXT,
    voucher_type VARCHAR(20) NOT NULL,
    value        DECIMAL(10, 2) NOT NULL,
    min_points   INT            NOT NULL,
    expiry_date  DATE           NOT NULL,
    quantity     INT            NOT NULL,
    min_spending  DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at   DATETIME  DEFAULT GETDATE()
);
GO
INSERT INTO Vouchers (voucher_code, description, voucher_type, value, min_points, expiry_date, quantity)
VALUES
('DISCOUNT50K', 'Giảm 50,000 VND', 'fixed', 50000, 1000, '2025-12-31', 100),
('DISCOUNT10%', 'Giảm 10% trên tổng đơn hàng', 'percent', 10, 1500, '2025-12-31', 50);
GO

CREATE TABLE User_Vouchers
(
    id          INT PRIMARY KEY IDENTITY(1,1),
    user_id     INT NOT NULL,
    voucher_id  INT NOT NULL,
    redeemed_at DATETIME  DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE,
    FOREIGN KEY (voucher_id) REFERENCES Vouchers (voucher_id) ON DELETE CASCADE,
    UNIQUE (user_id, voucher_id) -- Đảm bảo mỗi user chỉ đổi 1 lần
);
GO

CREATE TABLE Products
(
    id           INT PRIMARY KEY IDENTITY(1,1),
    name         VARCHAR(255)   NOT NULL,
    description  TEXT,
    price        DECIMAL(10, 2) NOT NULL,
    category_id  INT,
    image_url    VARCHAR(255),
    size         VARCHAR(50),
    topping_name VARCHAR(255),
    created_at   DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at   DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (category_id) REFERENCES Categories (id)
);
GO
INSERT INTO Products (id, name, description, price, category_id, image_url, size, topping_name, created_at, updated_at)
VALUES
(1, 'Espresso', 'Rich and bold shot of coffee', 2.50, 1, 'https://example.com/espresso.jpg', 'Small', NULL, GETDATE(), GETDATE()),
(2, 'Latte', 'Smooth and creamy coffee', 3.50, 1, 'https://example.com/latte.jpg', 'Medium', 'Vanilla', GETDATE(), GETDATE()),
(3, 'Cappuccino', 'Foamy and delicious', 3.00, 1, 'https://example.com/cappuccino.jpg', 'Medium', 'Caramel', GETDATE(), GETDATE()),
(4, 'Green Tea', 'Refreshing green tea', 2.00, 2, 'https://example.com/greentea.jpg', 'Large', NULL, GETDATE(), GETDATE()),
(5, 'Blueberry Muffin', 'Soft and sweet muffin', 1.50, 3, 'https://example.com/muffin.jpg', NULL, NULL, GETDATE(), GETDATE());
GO



CREATE TABLE Inventory
(
    id         INT PRIMARY KEY IDENTITY(1,1),
    product_id INT NOT NULL,
    store_id   INT,
    quantity   INT NOT NULL,
    status     VARCHAR(50),
    FOREIGN KEY (product_id) REFERENCES Products (id)
);
GO
INSERT INTO Inventory (id, product_id, store_id, quantity, status)
VALUES
(1, 1, 1, 50, 'normal'),
(2, 2, 1, 30, 'low'),
(3, 3, 1, 20, 'normal'),
(4, 4, 1, 40, 'normal'),
(5, 5, 1, 15, 'low');
GO


--CREATE TABLE Materials (
--    id INT PRIMARY KEY,
--    name VARCHAR(255) NOT NULL,
--    unit VARCHAR(50),
--    unit_price DECIMAL(10, 2),
--    stock INT,
--	inventory_id INT NOT NULL,
--	FOREIGN KEY (inventory_id) REFERENCES Inventory(id),
--    created_at DATETIME DEFAULT (CURRENT_TIMESTAMP),
--    updated_at DATETIME DEFAULT (CURRENT_TIMESTAMP)
--);
--GO
--INSERT INTO Materials (id, name, unit, unit_price, stock, created_at, updated_at)
--VALUES
--(1, 'Coffee Beans', 'kg', 15.00, 100, GETDATE(), GETDATE()),
--(2, 'Milk', 'liters', 1.50, 200, GETDATE(), GETDATE()),
--(3, 'Sugar', 'kg', 0.80, 50, GETDATE(), GETDATE()),
--(4, 'Tea Leaves', 'kg', 10.00, 30, GETDATE(), GETDATE()),
--(5, 'Cocoa Powder', 'kg', 12.00, 25, GETDATE(), GETDATE()),
--(6, 'Flour', 'kg', 2.00, 100, GETDATE(), GETDATE()),
--(7, 'Blueberries', 'kg', 5.00, 20, GETDATE(), GETDATE()),
--(8, 'Vanilla Syrup', 'liters', 8.00, 10, GETDATE(), GETDATE()),
--(9, 'Caramel Syrup', 'liters', 8.50, 15, GETDATE(), GETDATE()),
--(10, 'Butter', 'kg', 3.00, 40, GETDATE(), GETDATE());
--GO


CREATE TABLE Orders
(
    id               INT PRIMARY KEY,
    user_id          INT NOT NULL,
    order_date       DATETIME DEFAULT (CURRENT_TIMESTAMP),
    status           VARCHAR(50),
    total_price      DECIMAL(10, 2),
    payment_method   VARCHAR(50),
    shipping_address TEXT,
    created_at       DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at       DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (user_id) REFERENCES Users (id)
);
GO

INSERT INTO Orders (id, user_id, order_date, status, total_price, payment_method, shipping_address, created_at, updated_at)
VALUES
(1, 1, GETDATE(), 'completed', 7.50, 'credit_card', '123 Brew St', GETDATE(), GETDATE()),
(2, 4, GETDATE(), 'pending', 5.00, 'cash', '321 Mocha Ct', GETDATE(), GETDATE()),
(3, 5, GETDATE(), 'completed', 10.00, 'credit_card', '654 Cappuccino Rd', GETDATE(), GETDATE());
GO

CREATE TABLE Order_Items
(
    id           INT PRIMARY KEY,
    order_id     INT NOT NULL,
    product_id   INT NOT NULL,
    size         VARCHAR(50),
    topping_name VARCHAR(255),
    quantity     INT NOT NULL,
    price        DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES Orders (id),
    FOREIGN KEY (product_id) REFERENCES Products (id)
);
GO
INSERT INTO Order_Items (id, order_id, product_id, size, topping_name, quantity, price)
VALUES
(1, 1, 1, 'Small', NULL, 2, 5.00),
(2, 1, 5, NULL, NULL, 1, 2.50),
(3, 2, 2, 'Medium', 'Vanilla', 1, 3.50),
(4, 2, 4, 'Large', NULL, 1, 2.00);
GO

CREATE TABLE Cart
(
    id           INT PRIMARY KEY,
    user_id      INT NOT NULL,
    product_id   INT NOT NULL,
    quantity     INT NOT NULL,
    size         VARCHAR(50),
    topping_name VARCHAR(255),
    created_at   DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at   DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (user_id) REFERENCES Users (id),
    FOREIGN KEY (product_id) REFERENCES Products (id)
);
GO

CREATE TABLE Payment
(
    id                INT PRIMARY KEY,
    order_id          INT NOT NULL,
    member_id         INT NOT NULL,
    money             DECIMAL(10, 2),
    note              TEXT,
    vnp_response_code VARCHAR(50),
    vnp_code          VARCHAR(50),
    bank_code         VARCHAR(50),
    time              DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (order_id) REFERENCES Orders (id),
    FOREIGN KEY (member_id) REFERENCES Users (id)
);
GO
DROP TABLE if exists Promotions;
CREATE TABLE Promotions
(
    id          INT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    start_date  DATE,
    end_date    DATE,
    created_at  DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at  DATETIME DEFAULT (CURRENT_TIMESTAMP)
);
GO
INSERT INTO Promotions (id, name, description, start_date, end_date, created_at, updated_at)
VALUES
(1, 'Winter Warmers', 'Enjoy hot coffee with discounts', '2025-01-01', '2025-01-31', GETDATE(), GETDATE()),
(2, 'Pastry Delights', 'Special pastry offers', '2025-02-01', '2025-02-28', GETDATE(), GETDATE());
GO
DROP TABLE if exists Discounts;
CREATE TABLE Discounts
(
    id                  INT PRIMARY KEY,
    code                VARCHAR(50)   NOT NULL,
    description         TEXT,
    discount_percentage DECIMAL(5, 2) NOT NULL CHECK (discount_percentage BETWEEN 0.00 AND 60.00),
    start_date          DATE,
    end_date            DATE,
    status              VARCHAR(20),
    promotion_id        INT,
    FOREIGN KEY (promotion_id) REFERENCES Promotions (id)
);
GO
INSERT INTO Discounts (id, code, description, discount_percentage, start_date, end_date, status, promotion_id)
VALUES
(1, 'COFFEE10', '10% off on coffee', 10.00, '2025-01-01', '2025-01-31', 'active', NULL),
(2, 'PASTRY5', '5% off on pastries', 5.00, '2025-01-01', '2025-02-28', 'active', NULL);
GO

CREATE table Store_Chain
(
    id           INT PRIMARY KEY,
    name         varchar(50) NOT NULL,
    location     varchar(50) NOT NULL,
    manager_id   INT         NOT NULL,
    open_time    DATETIME,
    closing_time DATETIME,
    FOREIGN KEY (manager_id) REFERENCES Users (id)
);
go
INSERT INTO Store_Chain (id, name, location, manager_id, open_time, closing_time)
VALUES
(1, 'Central Coffee', 'Downtown', 7, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
(2, 'Westside Brews', 'Westside', 11, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
(3, 'North Park Cafe', 'North Park', 12, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
(4, 'East End Coffee', 'East End', 13, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
(5, 'Suburban Coffee', 'Suburbs', 14, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
(6, 'Uptown Java', 'Uptown', 15, '2025-01-01 08:00:00', '2025-01-01 20:00:00');
go