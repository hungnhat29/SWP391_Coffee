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
INSERT INTO Users ( name, email, password, phone, address, role, created_at, updated_at)
VALUES
( 'John Doe', 'john@example.com', 'hashed_pwd_1', '1234567890', '123 Main St', 'customer', GETDATE(), GETDATE()),
('Jane Smith', 'jane@example.com', 'hashed_pwd_2', '9876543210', '456 Elm St', 'admin', GETDATE(), GETDATE()),
( 'Alice Johnson', 'alice@example.com', 'hashed_pwd_3', '1122334455', '789 Pine St', 'customer', GETDATE(), GETDATE()),
( 'Bob Brown', 'bob@example.com', 'hashed_pwd_4', '2233445566', '321 Maple St', 'customer', GETDATE(), GETDATE()),
( 'Charlie Davis', 'charlie@example.com', 'hashed_pwd_5', '3344556677', '654 Oak St', 'customer', GETDATE(), GETDATE()),
( 'Emily White', 'emily@example.com', 'hashed_pwd_6', '4455667788', '987 Cedar St', 'customer', GETDATE(), GETDATE()),
( 'Frank Harris', 'frank@example.com', 'hashed_pwd_7', '5566778899', '135 Birch St', 'manager', GETDATE(), GETDATE()),
( 'Grace Lewis', 'grace@example.com', 'hashed_pwd_8', '6677889900', '246 Walnut St', 'customer', GETDATE(), GETDATE()),
( 'Hannah Green', 'hannah@example.com', 'hashed_pwd_9', '7788990011', '369 Cherry St', 'customer', GETDATE(), GETDATE()),
('Ian Black', 'ian@example.com', 'hashed_pwd_10', '8899001122', '159 Ash St', 'customer', GETDATE(), GETDATE()),
( 'Alice Smith', 'alice2@example.com', 'hashed_pwd_2', '9876543210', '456 Elm St', 'manager', GETDATE(), GETDATE()),
( 'Bob Johnson', 'bob2@example.com', 'hashed_pwd_3', '1122334455', '789 Oak St', 'manager', GETDATE(), GETDATE()),
( 'Eve Carter', 'eve@example.com', 'hashed_pwd_4', '6677889900', '321 Pine St', 'manager', GETDATE(), GETDATE()),
( 'Charlie Davis', 'charlie2@example.com', 'hashed_pwd_5', '4455667788', '654 Maple St', 'manager', GETDATE(), GETDATE()),
( 'Diana Lewis', 'diana@example.com', 'hashed_pwd_6', '9988776655', '987 Cedar St', 'manager', GETDATE(), GETDATE());
GO
CREATE TABLE Roles
(
    id          INT PRIMARY KEY IDENTITY(1,1),
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
INSERT INTO Roles ( role_name, user_id, permissions)
VALUES
( 'admin', 2, 'all_access'),
('manager', 7, 'create_order, manage_inventory, manage_revenue'),
( 'manager', 11, 'create_order, manage_inventory, manage_revenue'),
( 'manager', 12, 'create_order, manage_inventory, manage_revenue'),
('manager', 13, 'create_order, manage_inventory, manage_revenue'),
( 'manager', 14, 'create_order, manage_inventory, manage_revenue'),
( 'manager', 15, 'create_order, manage_inventory, manage_revenue');


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
INSERT INTO Memberships ( user_id, membership_type, start_date, expiry_date, rank, points, status)
VALUES
( 1, 'premium', '2025-01-01', '2025-12-31', 'Gold', 5000, 'active'),
( 3, 'basic', '2025-01-01', '2025-06-30', 'Silver', 2000, 'active'),
( 5, 'basic', '2025-01-01', '2025-06-30', 'Bronze', 1000, 'inactive'),
( 7, 'premium', '2025-01-01', '2025-12-31', 'Gold', 7000, 'active'),
(8, 'basic', '2025-01-01', '2025-06-30', 'Silver', 3000, 'active');
GO

CREATE TABLE Categories (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at DATETIME DEFAULT (CURRENT_TIMESTAMP),

);
GO

SET IDENTITY_INSERT Categories ON;

INSERT INTO Categories (id, name, description, image_url) VALUES
(1, N'Cà Phê Highlight', N'Các loại cà phê đặc biệt', 'https://product.hstatic.net/1000075078/product/1737355620_tx-espresso-marble_3942abe277644167a391b0a3bcfc52fc_large.png'),
(2, N'Cà Phê Việt Nam', N'Các loại cà phê truyền thống Việt Nam', 'https://product.hstatic.net/1000075078/product/1737357048_uong-den-sua-da_979b4b23b19f429b9dedb3491743c016_large.png'),
(3, N'Cà Phê Máy', N'Các loại cà phê pha máy', 'https://product.hstatic.net/1000075078/product/1737357102_ame-nong_201c8430dc7d479896e5e9c0947f05c6_large.png'),
(4, N'Cold Brew', N'Các loại cà phê pha lạnh', 'https://product.hstatic.net/1000075078/product/1737357505_coldbrew-classic_9d08e3ee0d154050898affcb0ebb7745_large.png'),
(5, N'A-Mê', N'Dòng sản phẩm A-Mê', 'https://product.hstatic.net/1000075078/product/1737354959_ame-classic_db5b4a4f50cd4d3da97e0264ecebe6ec_large.png'),
(6, N'Trái Cây Xay 0°C', N'Các loại đồ uống xay từ trái cây', 'https://product.hstatic.net/1000075078/product/1737355134_tcx-bo_033cc1e550434da988d1aee8a7b34ebb_large.png'),
(7, N'Trà Trái Cây', N'Các loại trà kết hợp trái cây', 'https://product.hstatic.net/1000075078/product/1737356345_oolong-sen_f30ecb190ecf49149c8c592943b7bfb5_large.png'),
(8, N'Hi-Tea', N'Dòng sản phẩm Hi-Tea', 'https://product.hstatic.net/1000075078/product/1737365858_hi-tea-dao-kombucha-1_6e7127daaebb426b9ad2e2c9b900c56c_large.png'),
(9, N'Trà Sữa', N'Các loại trà sữa', 'https://product.hstatic.net/1000075078/product/1737356814_ts-oolong-nuong-suong-sao_38937aa46d4f4ccb9525df18c752ef0c_large.png'),
(10, N'Trà Xanh Tây Bắc', N'Các loại trà xanh Tây Bắc', 'https://product.hstatic.net/1000075078/product/1737355604_tx-latte-nong_111bf6feaf044991bd17012b13f0ecc3_large.png'),
(11, N'Chocolate', N'Các loại đồ uống chocolate', 'https://product.hstatic.net/1000075078/product/1737355560_chocolate-da_56317be1073646d880612ae27912f343_large.png'),
(12, N'Đá xay Frosty', N'Các loại đồ uống đá xay', 'https://product.hstatic.net/1000075078/product/1737355736_fosty-banh-kem-dau_090ebd3012d2403da48e067e254e9a81_large.png'),
(13, N'Bánh mặn', N'Các loại bánh mặn', 'https://product.hstatic.net/1000075078/product/1737355270_bmq-cha-bong-pm_013c91b6596245cbb65563798ee90742_large.png'),
(14, N'Bánh ngọt', N'Các loại bánh ngọt', 'https://product.hstatic.net/1000075078/product/1737355355_mochi-phuc-bon-tu_55c636c7e56b44199f39d52b04a30acf_large.png'),
(15, N'Bánh Pastry', N'Các loại bánh Pastry', 'https://product.hstatic.net/1000075078/product/1737355250_croissant_3e064007c02443458022028fadccaf8b_large.png'),
(16, N'Cà phê tại nhà', N'Các sản phẩm cà phê đóng gói', 'https://product.hstatic.net/1000075078/product/1737355913_cpg-cf-den-da_bb4e227e5f8c4ae3b43549ac61036783_large.png');

SET IDENTITY_INSERT Categories off;
GO

DROP TABLE IF EXISTS Products;
GO
CREATE TABLE Products (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(255) NOT NULL,
    description NTEXT,
    base_price DECIMAL(10, 0) NOT NULL, -- Giá cơ bản của sản phẩm
    category_id INT,
    image_url NVARCHAR(255),
    sizes NVARCHAR(MAX), -- Lưu thông tin size dưới dạng JSON
    toppings NVARCHAR(MAX), -- Lưu thông tin topping dưới dạng JSON
    created_at DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (category_id) REFERENCES Categories(id)
);

GO
DECLARE @Sizes NVARCHAR(MAX) = N'{
    "sizes": [
        {"name": "Nhỏ", "price": 0},
        {"name": "Vừa", "price": 6000},
        {"name": "Lớn", "price": 16000}
    ]
}';

DECLARE @Toppings NVARCHAR(MAX) = N'{
    "toppings": [
        {"name": "Thạch Sương Sáo", "price": 10000},
        {"name": "Thạch Kim Quất", "price": 10000},
        {"name": "Thạch Cà Phê", "price": 10000},
        {"name": "Foam Phô Mai", "price": 10000},
        {"name": "Shot Espresso", "price": 10000},
        {"name": "Sốt Caramel", "price": 10000},
        {"name": "Trân châu trắng", "price": 10000},
        {"name": "Đào Miếng", "price": 10000},
        {"name": "Hạt Sen", "price": 10000},
        {"name": "Trái Vải", "price": 10000},
        {"name": "Kem Phô Mai Macchiato", "price": 10000}
    ]
}';
SET IDENTITY_INSERT products ON;
INSERT INTO Products (id, name, description, base_price, category_id, image_url, sizes, toppings)
<<<<<<< HEAD
VALUES
(1, 'Trà Xanh Espresso Marble', 'Cho ngày thêm tươi, tỉnh, êm, mượt với Trà Xanh Espresso Marble. Đây là sự mai mối bất ngờ giữa trà xanh Tây Bắc vị mộc và cà phê Arabica Đà Lạt. Muốn ngày thêm chút highlight, nhớ tìm đến sự bất ngờ này bạn nhé!', 49000, 1, 'https://product.hstatic.net/1000075078/product/1737355620_tx-espresso-marble_3942abe277644167a391b0a3bcfc52fc.png', @Sizes, @Toppings),
(2, 'Bơ Arabica', 'Bơ sáp Đắk Lắk dẻo quẹo hòa quyện cùng Cà phê Arabica Cầu Đất êm mượt. Khuấy đều để thưởng thức hương vị tươi tỉnh, đầy mới lạ!', 49000, 2, 'https://product.hstatic.net/1000075078/product/1737357055_bo-arabica_b64556656a7d479bbe641ab7cff99605.png', @Sizes, @Toppings),
=======
VALUES 
(1, N'Trà Xanh Espresso Marble', N'Cho ngày thêm tươi, tỉnh, êm, mượt với Trà Xanh Espresso Marble. Đây là sự mai mối bất ngờ giữa trà xanh Tây Bắc vị mộc và cà phê Arabica Đà Lạt. Muốn ngày thêm chút highlight, nhớ tìm đến sự bất ngờ này bạn nhé!', 49000, 1, 'https://product.hstatic.net/1000075078/product/1737355620_tx-espresso-marble_3942abe277644167a391b0a3bcfc52fc.png', @Sizes, @Toppings),
(2, N'Bơ Arabica', N'Bơ sáp Đắk Lắk dẻo quẹo hòa quyện cùng Cà phê Arabica Cầu Đất êm mượt. Khuấy đều để thưởng thức hương vị tươi tỉnh, đầy mới lạ!', 49000, 2, 'https://product.hstatic.net/1000075078/product/1737357055_bo-arabica_b64556656a7d479bbe641ab7cff99605.png', @Sizes, @Toppings),
>>>>>>> feature/KienLL
(3, 'Đường Đen Sữa Đá', 'Đường đen thơm ngọt hòa quyện cùng sữa đặc và đá viên, tạo nên một thức uống mát lạnh, sảng khoái cho ngày dài.', 45000, 2, 'https://product.hstatic.net/1000075078/product/1737357048_uong-den-sua-da_979b4b23b19f429b9dedb3491743c016.png', @Sizes, @Toppings),
(4, 'G3P Sữa Đá', 'Cà phê đậm đà kết hợp với sữa đặc và đá viên, mang đến hương vị truyền thống nhưng không kém phần hiện đại.', 39000, 2, 'https://product.hstatic.net/1000075078/product/1737357037_tch-sua-da_5802e6e0dcb14c76b36bb45333996d33.png', @Sizes, @Toppings),
(5, 'Bạc Xỉu', 'Bạc xỉu với vị cà phê nhẹ nhàng, hòa quyện cùng sữa đặc, tạo nên một thức uống thơm ngon, phù hợp cho những ai yêu thích vị ngọt dịu.', 29000, 2, 'https://product.hstatic.net/1000075078/product/1737357020_bac-xiu-da_b59fbf30268c40deadd2aab42df34821.png', @Sizes, @Toppings),
(6, 'Bạc Xỉu Nóng', 'Bạc xỉu nóng với hương thơm của cà phê và vị ngọt dịu của sữa, là sự lựa chọn hoàn hảo cho những ngày se lạnh.', 39000, 2, 'https://product.hstatic.net/1000075078/product/1737357029_bac-xiu-nong_e72d12e16da140ffb856398c1da0e935.png', @Sizes, @Toppings),
(7, 'Cà Phê Đen Nóng', 'Cà phê đen nóng đậm đà, thơm nồng, giúp bạn tỉnh táo và tràn đầy năng lượng cho ngày mới.', 39000, 2, 'https://product.hstatic.net/1000075078/product/1737356979_cf-den-nong_ea24bdf37cbc4216ba07cffc0dffb82f.png', @Sizes, @Toppings),
(8, 'Cà Phê Sữa Nóng', 'Cà phê sữa nóng với vị đắng nhẹ của cà phê hòa quyện cùng vị ngọt béo của sữa, mang đến cảm giác ấm áp và thư giãn.', 39000, 2, 'https://product.hstatic.net/1000075078/product/1737356996_cfs-nong_3d7f237bf9944469bd0d067b9eb6a093.png', @Sizes, @Toppings),
(9, 'Cà Phê Đen Đá', 'Cà phê đen đá mát lạnh, đậm đà, giúp bạn tỉnh táo và sảng khoái trong những ngày nắng nóng.', 29000, 2, 'https://product.hstatic.net/1000075078/product/1737356988_cf-den-da_8f7dd455223e409fbd4caf9bbe394f0b.png', @Sizes, @Toppings),
(10, 'Cà Phê Sữa Đá', 'Cà phê sữa đá với vị đắng nhẹ của cà phê và vị ngọt béo của sữa, là thức uống giải khát tuyệt vời cho mùa hè.', 29000, 2, 'https://product.hstatic.net/1000075078/product/1737357012_cfsd_ab683c1d7cb6429ab3556719ce662f0c.png', @Sizes, @Toppings),
(11, 'Bạc Xỉu Lắc Sữa Yến Mạch', 'Bạc xỉu lắc sữa yến mạch với vị ngọt dịu của sữa yến mạch và hương thơm nhẹ nhàng của cà phê, mang đến một trải nghiệm mới lạ.', 45000, 2, 'https://product.hstatic.net/1000075078/product/1737357063_bac-xiu-lac-sua-yen-mach_cbd6b29a670040ccb1fa14b2799a6156.png', @Sizes, @Toppings),
(12, 'A-Mê Classic', 'A-Mê Classic với vị cà phê đậm đà, thơm nồng, là sự lựa chọn hoàn hảo cho những ai yêu thích hương vị cà phê nguyên bản.', 39000, 5, 'https://product.hstatic.net/1000075078/product/1737354959_ame-classic_db5b4a4f50cd4d3da97e0264ecebe6ec.png', @Sizes, @Toppings),
(13, 'A-Mê Đào', 'A-Mê Đào với vị chua ngọt của đào hòa quyện cùng cà phê, tạo nên một thức uống độc đáo và hấp dẫn.', 55000, 5, 'https://product.hstatic.net/1000075078/product/1737354989_ame-dao_2d10766d733b4399b8599a9064fbe8a5.png', @Sizes, @Toppings),
(14, 'A-Mê Mơ', 'A-Mê Mơ với vị chua nhẹ của mơ và hương thơm của cà phê, mang đến một trải nghiệm thú vị cho vị giác.', 55000, 5, 'https://product.hstatic.net/1000075078/product/1737355000_ame-mo_dc823b45087240f4a724c781cb94e30f.png', @Sizes, @Toppings),
(15, 'A-Mê Quất', 'A-Mê Quất với vị chua thanh của quất và hương thơm của cà phê, tạo nên một thức uống sảng khoái và tươi mới.', 55000, 5, 'https://product.hstatic.net/1000075078/product/1737355010_ame-quat_1174dd5cdd254f99b1e3a0dc662a5ff9.png', @Sizes, @Toppings),
(16, 'A-Mê Tuyết Đào', 'A-Mê Tuyết Đào với vị chua ngọt của đào và lớp tuyết mát lạnh, là sự kết hợp hoàn hảo giữa cà phê và trái cây.', 65000, 5, 'https://product.hstatic.net/1000075078/product/1737355029_ame-tuyet-dao_de2f73da79c643caa6fb56d98db5a9e0.png', @Sizes, @Toppings),
(17, 'A-Mê Tuyết Mơ', 'A-Mê Tuyết Mơ với vị chua nhẹ của mơ và lớp tuyết mát lạnh, mang đến một trải nghiệm thú vị và sảng khoái.', 65000, 5, 'https://product.hstatic.net/1000075078/product/1737355037_ame-tuyet-mo_5421ce73c76041169a860732eb442e18.png', @Sizes, @Toppings),
(18, 'A-Mê Tuyết Quất', 'A-Mê Tuyết Quất với vị chua thanh của quất và lớp tuyết mát lạnh, tạo nên một thức uống độc đáo và hấp dẫn.', 65000, 5, 'https://product.hstatic.net/1000075078/product/1737355048_ame-tuyet-quat_514338c991b64fa4b48d4955a3b48352.png', @Sizes, @Toppings),
(19, 'Americano Nóng', 'Americano nóng với vị cà phê đậm đà, thơm nồng, là sự lựa chọn hoàn hảo cho những ai yêu thích hương vị cà phê nguyên bản.', 49000, 3, 'https://product.hstatic.net/1000075078/product/1737357102_ame-nong_201c8430dc7d479896e5e9c0947f05c6.png', @Sizes, @Toppings),
(20, 'Cappuccino Nóng', 'Cappuccino nóng với lớp bọt sữa mịn màng và hương thơm của cà phê, mang đến cảm giác ấm áp và thư giãn.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357108_cappucino-nong_8102b9252c924903b332fc6cb5f095fe.png', @Sizes, @Toppings),
(21, 'Cappuccino Đá', 'Cappuccino đá mát lạnh với lớp bọt sữa mịn màng và hương thơm của cà phê, là thức uống giải khát tuyệt vời cho mùa hè.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357118_cappucio-da_03315d8d22034e918142c8040101b754.png', @Sizes, @Toppings),
(22, 'Caramel Macchiato Nóng', 'Caramel Macchiato nóng với vị ngọt của caramel và hương thơm của cà phê, mang đến một trải nghiệm thưởng thức đầy tinh tế.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357126_caramel-macchiato-nong_ffc0fb416090487fa17372171d97e167_large.png', @Sizes, @Toppings),
(23, 'Caramel Macchiato Đá', 'Caramel Macchiato đá mát lạnh với vị ngọt của caramel và hương thơm của cà phê, là thức uống giải khát tuyệt vời cho mùa hè.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357135_caramel-macchiato-da_b3952ad1bccd430eaff3962b2aa81541_large.png', @Sizes, @Toppings),
(24, 'Espresso Nóng', 'Espresso nóng đậm đà, thơm nồng, giúp bạn tỉnh táo và tràn đầy năng lượng cho ngày mới.', 45000, 3, 'https://product.hstatic.net/1000075078/product/1737357444_espresso-nong_5fdcc7cf79354261ba37f0615fe54ce7_large.png', @Sizes, @Toppings),
(25, 'Espresso Đá', 'Espresso đá mát lạnh, đậm đà, giúp bạn tỉnh táo và sảng khoái trong những ngày nắng nóng.', 49000, 3, 'https://product.hstatic.net/1000075078/product/1737357429_espresso-da_a1aadc906e3c4ce8ae5cb9bc24d58857_large.png', @Sizes, @Toppings),
(26, 'Latte Nóng', 'Latte nóng với lớp bọt sữa mịn màng và hương thơm của cà phê, mang đến cảm giác ấm áp và thư giãn.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357451_latte-nong_879435385c804cef9a3b723b1d1f9c91_large.png', @Sizes, @Toppings),
(27, 'Latte Đá', 'Latte đá mát lạnh với lớp bọt sữa mịn màng và hương thơm của cà phê, là thức uống giải khát tuyệt vời cho mùa hè.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357460_latte-da_65cbfd340d044ff29e324e1089b78fac_large.png', @Sizes, @Toppings),
(28, 'Cold Brew Truyền Thống', 'Cold Brew truyền thống với vị cà phê đậm đà, thơm nồng, được ủ lạnh trong nhiều giờ, mang đến hương vị tinh tế và sảng khoái.', 50000, 4, 'https://product.hstatic.net/1000075078/product/1737357505_coldbrew-classic_9d08e3ee0d154050898affcb0ebb7745_large.png', @Sizes, @Toppings);
/*(29, 'Cold Brew Kim Quất', 'Cold Brew Kim Quất với vị chua thanh của kim quất và hương thơm của cà phê, tạo nên một thức uống độc đáo và hấp dẫn.', 50000, 4, 'image_url_here', @Sizes, @Toppings),
(30, 'Cold Brew Sữa Tươi', 'Cold Brew Sữa Tươi với vị béo ngậy của sữa tươi và hương thơm của cà phê, mang đến một trải nghiệm thưởng thức đầy tinh tế.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(31, 'Bơ Sữa Phô Mai', 'Bơ Sữa Phô Mai với vị béo ngậy của bơ, sữa và phô mai, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(32, 'Dâu Phô Mai', 'Dâu Phô Mai với vị chua ngọt của dâu và vị béo ngậy của phô mai, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(33, 'Oolong Tứ Quý Sen (Nóng)', 'Oolong Tứ Quý Sen nóng với hương thơm của sen và vị trà Oolong đậm đà, mang đến cảm giác ấm áp và thư giãn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(34, 'Oolong Tứ Quý Sen', 'Oolong Tứ Quý Sen với hương thơm của sen và vị trà Oolong đậm đà, là thức uống giải khát tuyệt vời cho mùa hè.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(35, 'Oolong Tứ Quý Dâu Trân Châu', 'Oolong Tứ Quý Dâu Trân Châu với vị chua ngọt của dâu và trân châu dai giòn, tạo nên một thức uống độc đáo và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(36, 'Oolong Tứ Quý Kim Quất Trân Châu', 'Oolong Tứ Quý Kim Quất Trân Châu với vị chua thanh của kim quất và trân châu dai giòn, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(37, 'Oolong Tứ Quý Vải', 'Oolong Tứ Quý Vải với vị ngọt thanh của vải và hương thơm của trà Oolong, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(38, 'Hi-Tea Đào Kombucha', 'Hi-Tea Đào Kombucha với vị chua ngọt của đào và hương thơm của kombucha, mang đến một trải nghiệm thưởng thức đầy tinh tế.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(39, 'Hi-Tea Yuzu Kombucha', 'Hi-Tea Yuzu Kombucha với vị chua thanh của yuzu và hương thơm của kombucha, tạo nên một thức uống độc đáo và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(40, 'Hi-Tea Yuzu Trân Châu', 'Hi-Tea Yuzu Trân Châu với vị chua thanh của yuzu và trân châu dai giòn, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(41, 'Hi-Tea Vải', 'Hi-Tea Vải với vị ngọt thanh của vải và hương thơm của trà, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(42, 'Hi-Tea Đào', 'Hi-Tea Đào với vị chua ngọt của đào và hương thơm của trà, mang đến một trải nghiệm thưởng thức đầy tinh tế.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(43, 'Trà Sữa Oolong Nướng Sương Sáo', 'Trà Sữa Oolong Nướng Sương Sáo với hương thơm của trà Oolong nướng và sương sáo mát lạnh, tạo nên một thức uống độc đáo và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(44, 'Trà Sữa Oolong Tứ Quý Bơ', 'Trà Sữa Oolong Tứ Quý Bơ với vị béo ngậy của bơ và hương thơm của trà Oolong, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(45, 'Trà Sữa Oolong Tứ Quý Sương Sáo', 'Trà Sữa Oolong Tứ Quý Sương Sáo với hương thơm của trà Oolong và sương sáo mát lạnh, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(46, 'Hồng Trà Sữa Nóng', 'Hồng Trà Sữa Nóng với hương thơm của hồng trà và vị ngọt béo của sữa, mang đến cảm giác ấm áp và thư giãn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(47, 'Hồng Trà Sữa Trân Châu', 'Hồng Trà Sữa Trân Châu với hương thơm của hồng trà và trân châu dai giòn, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(48, 'Trà Đen Macchiato', 'Trà Đen Macchiato với lớp bọt sữa mịn màng và hương thơm của trà đen, mang đến một trải nghiệm thưởng thức đầy tinh tế.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(49, 'Trà Sữa Oolong BLao', 'Trà Sữa Oolong BLao với hương thơm của trà Oolong và vị ngọt béo của sữa, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(50, 'Trà Xanh Latte (Nóng)', 'Trà Xanh Latte nóng với hương thơm của trà xanh và vị ngọt béo của sữa, mang đến cảm giác ấm áp và thư giãn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(51, 'Trà Xanh Đường Đen', 'Trà Xanh Đường Đen với vị ngọt của đường đen và hương thơm của trà xanh, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(52, 'Trà Xanh Espresso Marble', 'Trà Xanh Espresso Marble với hương thơm của trà xanh và vị đậm đà của espresso, mang đến một trải nghiệm thưởng thức đầy tinh tế.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(53, 'Trà Xanh Latte Sữa Yến Mạch', 'Trà Xanh Latte Sữa Yến Mạch với vị ngọt dịu của sữa yến mạch và hương thơm của trà xanh, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(54, 'Trà Xanh Latte Đậm Vị', 'Trà Xanh Latte Đậm Vị với hương thơm đậm đà của trà xanh và vị ngọt béo của sữa, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(55, 'Trà Xanh Latte', 'Trà Xanh Latte với hương thơm của trà xanh và vị ngọt béo của sữa, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(56, 'Trà Xanh Latte Sữa Yến Mạch (Nóng)', 'Trà Xanh Latte Sữa Yến Mạch nóng với vị ngọt dịu của sữa yến mạch và hương thơm của trà xanh, mang đến cảm giác ấm áp và thư giãn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(57, 'Chocolate Đá', 'Chocolate đá mát lạnh với vị ngọt đậm đà của chocolate, là thức uống giải khát tuyệt vời cho mùa hè.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(58, 'Chocolate Nóng', 'Chocolate nóng với vị ngọt đậm đà của chocolate, mang đến cảm giác ấm áp và thư giãn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(59, 'Frosty Bánh Kem Dâu', 'Frosty Bánh Kem Dâu với vị ngọt của dâu và lớp kem mát lạnh, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(60, 'Frosty Choco Chip', 'Frosty Choco Chip với vị ngọt đậm đà của chocolate và những viên sô cô la chip giòn tan, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(61, 'Frosty Trà Xanh', 'Frosty Trà Xanh với hương thơm của trà xanh và lớp kem mát lạnh, tạo nên một thức uống thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(62, 'Frosty Cà Phê Đường Đen', 'Frosty Cà Phê Đường Đen với vị đậm đà của cà phê và vị ngọt của đường đen, mang đến một trải nghiệm thưởng thức đầy tinh tế.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(63, 'Frosty Caramel Arabica', 'Frosty Caramel Arabica với vị ngọt của caramel và hương thơm của cà phê Arabica, tạo nên một thức uống độc đáo và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(64, 'Frosty Phin-Gato', 'Frosty Phin-Gato với vị đậm đà của cà phê Phin và lớp kem mát lạnh, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(65, 'Bánh Mì Que Chà Bông Phô Mai Bơ Cay', 'Bánh Mì Que Chà Bông Phô Mai Bơ Cay với vị béo ngậy của phô mai và vị cay nồng của bơ, tạo nên một món ăn nhẹ thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(66, 'Bánh Mì Que Bò Nấm Xốt Bơ', 'Bánh Mì Que Bò Nấm Xốt Bơ với vị đậm đà của bò nấm và xốt bơ thơm ngon, mang đến một trải nghiệm ẩm thực đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(67, 'Bánh Mì Que Pate Cột Đèn', 'Bánh Mì Que Pate Cột Đèn với vị đậm đà của pate và hương thơm của cột đèn, tạo nên một món ăn nhẹ thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(68, 'Croissant trứng muối', 'Croissant trứng muối với lớp vỏ bánh giòn tan và vị mặn ngọt của trứng muối, mang đến một trải nghiệm ẩm thực đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(69, 'Butter Croissant Sữa Đặc', 'Butter Croissant Sữa Đặc với lớp vỏ bánh giòn tan và vị ngọt béo của sữa đặc, tạo nên một món ăn nhẹ thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(70, 'Chà Bông Phô Mai', 'Chà Bông Phô Mai với vị béo ngậy của phô mai và vị mặn ngọt của chà bông, mang đến một món ăn nhẹ thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(71, 'Mít Sấy', 'Mít Sấy với vị ngọt tự nhiên của mít và độ giòn tan, là món ăn nhẹ lý tưởng cho những buổi trò chuyện cùng bạn bè.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(72, 'Mochi Kem Trà Sữa Trân Châu', 'Mochi Kem Trà Sữa Trân Châu với vị ngọt của trà sữa và trân châu dai giòn, tạo nên một món tráng miệng thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(73, 'Mochi Kem Phúc Bồn Tử', 'Mochi Kem Phúc Bồn Tử với vị chua ngọt của phúc bồn tử và lớp vỏ mochi dẻo dai, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(74, 'Mochi Kem Việt Quất', 'Mochi Kem Việt Quất với vị chua ngọt của việt quất và lớp vỏ mochi dẻo dai, tạo nên một món tráng miệng thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(75, 'Mochi Kem Chocolate', 'Mochi Kem Chocolate với vị ngọt đậm đà của chocolate và lớp vỏ mochi dẻo dai, mang đến một trải nghiệm thưởng thức đầy thú vị.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(76, 'Mochi Kem Matcha', 'Mochi Kem Matcha với vị đắng nhẹ của matcha và lớp vỏ mochi dẻo dai, tạo nên một món tráng miệng thơm ngon và hấp dẫn.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(77, 'Cà Phê Đen Đá Hộp (14 gói x 16g)', 'Cà Phê Đen Đá Hộp với vị đậm đà của cà phê, tiện lợi và dễ dàng thưởng thức mọi lúc mọi nơi.', 50000, 1, 'image_url_here', @Sizes, @Toppings),
(78, 'Cà Phê Sữa Đá Hòa Tan Túi 25x22G', 'Cà Phê Sữa Đá Hòa Tan Túi với vị ngọt béo của sữa và hương thơm của cà phê, tiện lợi và dễ dàng thưởng thức mọi lúc mọi nơi.', 50000, 1, 'image_url_here', null, null),
(79, 'Cà Phê Sữa Đá Hòa Tan (10 gói x 22g)', 'Cà Phê Sữa Đá Hòa Tan với vị ngọt béo của sữa và hương thơm của cà phê, tiện lợi và dễ dàng thưởng thức mọi lúc mọi nơi.', 50000, 1, 'image_url_here', null, null),
(80, 'Cà Phê Rang Xay Original 1 250G', 'Cà Phê Rang Xay Original với hương thơm đậm đà của cà phê rang xay, mang đến hương vị nguyên bản và tinh tế.', 50000, 1, 'image_url_here', null, null);*/
SET IDENTITY_INSERT products off;
go

CREATE TABLE Vouchers
(
    voucher_id   INT PRIMARY KEY IDENTITY(1,1),
    voucher_code VARCHAR(50)    NOT NULL UNIQUE,
    description  TEXT,
    voucher_type VARCHAR(20)    NOT NULL,
    value        DECIMAL(10, 2) NOT NULL,
    min_points   INT            NOT NULL,
    expiry_date  DATE           NOT NULL,
    quantity     INT            NOT NULL,
    min_spending DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at   DATETIME                DEFAULT GETDATE()
);
GO
INSERT INTO Vouchers (voucher_code, description, voucher_type, value, min_points, expiry_date, quantity)
VALUES
('DISCOUNT50K', 'Giảm 50,000 VND', 'FIXED', 50000, 1000, '2025-12-31', 100),
('DISCOUNT10%', 'Giảm 10% trên tổng đơn hàng', 'PERCENT', 10, 1500, '2025-12-31', 50);
GO

CREATE TABLE User_Vouchers
(
    id          INT PRIMARY KEY IDENTITY(1,1),
    user_id     INT NOT NULL,
    voucher_id  INT NOT NULL,
    redeemed_at DATETIME DEFAULT GETDATE(),
    claimed_at  DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE,
    FOREIGN KEY (voucher_id) REFERENCES Vouchers (voucher_id) ON DELETE CASCADE,
    UNIQUE (user_id, voucher_id) -- Đảm bảo mỗi user chỉ đổi 1 lần
);
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
INSERT INTO Inventory ( product_id, store_id, quantity, status)
VALUES
( 1, 1, 50, 'normal'),
( 2, 1, 30, 'low'),
( 3, 1, 20, 'normal'),
( 4, 1, 40, 'normal'),
( 5, 1, 15, 'low');
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
DROP TABLE IF EXISTS Orders;
GO

CREATE TABLE Orders
(
    id               INT PRIMARY KEY IDENTITY(1,1),
    user_id          INT NULL,
    session_id       VARCHAR(100) NULL,
    order_date       DATETIME DEFAULT (CURRENT_TIMESTAMP),
    status           VARCHAR(50) DEFAULT 'pending',
    order_total      DECIMAL(10, 0) NOT NULL, 
    payment_method   VARCHAR(50),
    shipping_address TEXT NULL,
    created_at       DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at       DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (user_id) REFERENCES Users (id),
    CONSTRAINT CHK_Order_User CHECK (
        (user_id IS NOT NULL AND session_id IS NULL) OR 
        (user_id IS NULL AND session_id IS NOT NULL)
    )
);
GO



DROP TABLE IF EXISTS Order_Items;
GO

CREATE TABLE Order_Items
(
    id             INT PRIMARY KEY IDENTITY(1,1),
    order_id       INT NOT NULL,
    product_id     INT NOT NULL,
    quantity       INT NOT NULL,
    size_info      NVARCHAR(MAX),  -- {"name": "Nhỏ", "price": 0}
    toppings_info  NVARCHAR(MAX),  -- JSON array of selected toppings
    sub_total      DECIMAL(10,0) NOT NULL, -- Tổng giá của sản phẩm này trong order
    created_at     DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at     DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (order_id) REFERENCES Orders (id),
    FOREIGN KEY (product_id) REFERENCES Products (id)
);
GO



DROP TABLE IF EXISTS Cart;
GO

CREATE TABLE Cart
(
    id           INT PRIMARY KEY IDENTITY(1,1),
    user_id      INT,
    session_id   VARCHAR(100),
    product_id   INT NOT NULL,
    quantity     INT NOT NULL,
    size_info    NVARCHAR(MAX),  -- Store as JSON: {"name": "Nhỏ", "price": 0}
    toppings_info NVARCHAR(MAX), -- Store as JSON array of selected toppings
    sub_total    DECIMAL(10,0) NOT NULL, -- Calculated total price
    created_at   DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at   DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (user_id) REFERENCES Users (id),
    FOREIGN KEY (product_id) REFERENCES Products (id),
    CONSTRAINT CHK_Cart_User CHECK (
        (user_id IS NOT NULL AND session_id IS NULL) OR 
        (user_id IS NULL AND session_id IS NOT NULL)
    )
);
GO
/*DROP TABLE IF EXISTS Payment;
GO
CREATE TABLE Payment
(
    id                INT PRIMARY KEY IDENTITY(1,1),
    order_id          INT NOT NULL,
    member_id         INT NOT NULL,
    money             DECIMAL(10, 0),
    note              TEXT,
    vnp_response_code VARCHAR(50),
    vnp_code          VARCHAR(50),
    bank_code         VARCHAR(50),
    time              DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (order_id) REFERENCES Orders (id),
    FOREIGN KEY (member_id) REFERENCES Users (id)
);
GO*/
DROP TABLE if exists Promotions;
CREATE TABLE Promotions
(
    id          INT PRIMARY KEY IDENTITY(1,1),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    start_date  DATE,
    end_date    DATE,
    created_at  DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at  DATETIME DEFAULT (CURRENT_TIMESTAMP)
);
GO
INSERT INTO Promotions ( name, description, start_date, end_date, created_at, updated_at)
VALUES
( 'Winter Warmers', 'Enjoy hot coffee with discounts', '2025-01-01', '2025-01-31', GETDATE(), GETDATE()),
('Pastry Delights', 'Special pastry offers', '2025-02-01', '2025-02-28', GETDATE(), GETDATE());
GO
DROP TABLE if exists Discounts;
CREATE TABLE Discounts
(
    id                  INT PRIMARY KEY IDENTITY(1,1),
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
INSERT INTO Discounts ( code, description, discount_percentage, start_date, end_date, status, promotion_id)
VALUES
( 'COFFEE10', '10% off on coffee', 10.00, '2025-01-01', '2025-01-31', 'active', NULL),
( 'PASTRY5', '5% off on pastries', 5.00, '2025-01-01', '2025-02-28', 'active', NULL);
GO

CREATE table Store_Chain
(
    id           INT PRIMARY KEY IDENTITY(1,1),
    name         varchar(50) NOT NULL,
    location     varchar(50) NOT NULL,
    manager_id   INT         NOT NULL,
    open_time    DATETIME,
    closing_time DATETIME,
    FOREIGN KEY (manager_id) REFERENCES Users (id)
);
go
INSERT INTO Store_Chain ( name, location, manager_id, open_time, closing_time)
VALUES
( 'Central Coffee', 'Downtown', 7, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
( 'Westside Brews', 'Westside', 11, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
( 'North Park Cafe', 'North Park', 12, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
( 'East End Coffee', 'East End', 13, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
( 'Suburban Coffee', 'Suburbs', 14, '2025-01-01 08:00:00', '2025-01-01 20:00:00'),
( 'Uptown Java', 'Uptown', 15, '2025-01-01 08:00:00', '2025-01-01 20:00:00');
go