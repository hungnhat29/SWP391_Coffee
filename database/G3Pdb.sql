USE master
DROP DATABASE G3P_Coffee
DROP PROCEDURE IF EXISTS G3P_Coffee;
GO

CREATE DATABASE G3P_Coffee;
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

INSERT INTO Users (name, email, password, phone, address, role, created_at, updated_at)
VALUES
('Admin User', 'admin@gmail.com', 'p12345', '1234567890', '123 Admin St', 'admin', GETDATE(), GETDATE()),
('Manager 1', 'manager1@gmail.com', 'p12345', '2345678901', '456 Manager St', 'manager', GETDATE(), GETDATE()),
('Manager 2', 'manager2@gmail.com', 'p12345', '3456789012', '789 Manager St', 'manager', GETDATE(), GETDATE()),
('Manager 3', 'manager3@gmail.com', 'p12345', '4567890123', '321 Manager St', 'manager', GETDATE(), GETDATE()),
('Staff 1', 'staff1@gmail.com', 'p12345', '5678901234', '654 Staff St', 'staff', GETDATE(), GETDATE()),
('Staff 2', 'staff2@gmail.com', 'p12345', '6789012345', '987 Staff St', 'staff', GETDATE(), GETDATE()),
('Staff 3', 'staff3@gmail.com', 'p12345', '7890123456', '135 Staff St', 'staff', GETDATE(), GETDATE()),
('Staff 4', 'staff4@gmail.com', 'p12345', '8901234567', '246 Staff St', 'staff', GETDATE(), GETDATE()),
('Staff 5', 'staff5@gmail.com', 'p12345', '9012345678', '369 Staff St', 'staff', GETDATE(), GETDATE()),
('Customer 1', 'customer1@gmail.com', 'cust123', '1122334455', '123 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 2', 'customer2@gmail.com', 'cust234', '2233445566', '456 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 3', 'customer3@gmail.com', 'cust345', '3344556677', '789 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 4', 'customer4@gmail.com', 'cust456', '4455667788', '321 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 5', 'customer5@gmail.com', 'cust567', '5566778899', '654 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 6', 'customer6@gmail.com', 'cust678', '6677889900', '987 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 7', 'customer7@gmail.com', 'cust789', '7788990011', '135 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 8', 'customer8@gmail.com', 'cust890', '8899001122', '246 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 9', 'customer9@gmail.com', 'cust901', '9900112233', '369 Customer St', 'customer', GETDATE(), GETDATE()),
('Customer 10', 'customer10@gmail.com', 'cust012', '1011121314', '159 Customer St', 'customer', GETDATE(), GETDATE());
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

INSERT INTO Memberships (user_id, membership_type, start_date, expiry_date, rank, points, status)
VALUES
(1, 'premium', '2025-01-01', '2025-12-31', 'Gold', 5000, 'active'),
(10, 'basic', '2025-01-01', '2025-06-30', 'Silver', 2000, 'active'),
(12, 'basic', '2025-01-01', '2025-06-30', 'Bronze', 1000, 'inactive'),
(2, 'premium', '2025-01-01', '2025-12-31', 'Gold', 7000, 'active'),
(15, 'basic', '2025-01-01', '2025-06-30', 'Silver', 3000, 'active');
GO

DROP TABLE IF EXISTS Categories;
GO

CREATE TABLE Categories (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(500) NOT NULL,
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at DATETIME DEFAULT (CURRENT_TIMESTAMP)
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

SET IDENTITY_INSERT Categories OFF;
GO

DROP TABLE IF EXISTS Products;
GO

CREATE TABLE Products (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(255) NOT NULL,
    description NTEXT,
    base_price DECIMAL(10, 0) NOT NULL,
    category_id INT,
    image_url NVARCHAR(255),
    sizes NVARCHAR(MAX),
    toppings NVARCHAR(MAX),
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

SET IDENTITY_INSERT Products ON;

INSERT INTO Products (id, name, description, base_price, category_id, image_url, sizes, toppings)
VALUES 
(1, N'Trà Xanh Espresso Marble', N'Cho ngày thêm tươi, tỉnh, êm, mượt với Trà Xanh Espresso Marble. Đây là sự mai mối bất ngờ giữa trà xanh Tây Bắc vị mộc và cà phê Arabica Đà Lạt. Muốn ngày thêm chút highlight, nhớ tìm đến sự bất ngờ này bạn nhé!', 49000, 1, 'https://product.hstatic.net/1000075078/product/1737355620_tx-espresso-marble_3942abe277644167a391b0a3bcfc52fc.png', @Sizes, @Toppings),
(2, N'Bơ Arabica', N'Bơ sáp Đắk Lắk dẻo quẹo hòa quyện cùng Cà phê Arabica Cầu Đất êm mượt. Khuấy đều để thưởng thức hương vị tươi tỉnh, đầy mới lạ!', 49000, 2, 'https://product.hstatic.net/1000075078/product/1737357055_bo-arabica_b64556656a7d479bbe641ab7cff99605.png', @Sizes, @Toppings),
(3, N'Đường Đen Sữa Đá', N'Đường đen thơm ngọt hòa quyện cùng sữa đặc và đá viên, tạo nên một thức uống mát lạnh, sảng khoái cho ngày dài.', 45000, 2, 'https://product.hstatic.net/1000075078/product/1737357048_uong-den-sua-da_979b4b23b19f429b9dedb3491743c016.png', @Sizes, @Toppings),
(4, N'G3P Sữa Đá', N'Cà phê đậm đà kết hợp với sữa đặc và đá viên, mang đến hương vị truyền thống nhưng không kém phần hiện đại.', 39000, 2, 'https://product.hstatic.net/1000075078/product/1737357037_tch-sua-da_5802e6e0dcb14c76b36bb45333996d33.png', @Sizes, @Toppings),
(5, N'Bạc Xỉu', N'Bạc xỉu với vị cà phê nhẹ nhàng, hòa quyện cùng sữa đặc, tạo nên một thức uống thơm ngon, phù hợp cho những ai yêu thích vị ngọt dịu.', 29000, 2, 'https://product.hstatic.net/1000075078/product/1737357020_bac-xiu-da_b59fbf30268c40deadd2aab42df34821.png', @Sizes, @Toppings),
(6, N'Bạc Xỉu Nóng', N'Bạc xỉu nóng với hương thơm của cà phê và vị ngọt dịu của sữa, là sự lựa chọn hoàn hảo cho những ngày se lạnh.', 39000, 2, 'https://product.hstatic.net/1000075078/product/1737357029_bac-xiu-nong_e72d12e16da140ffb856398c1da0e935.png', @Sizes, @Toppings),
(7, N'Cà Phê Đen Nóng', N'Cà phê đen nóng đậm đà, thơm nồng, giúp bạn tỉnh táo và tràn đầy năng lượng cho ngày mới.', 39000, 2, 'https://product.hstatic.net/1000075078/product/1737356979_cf-den-nong_ea24bdf37cbc4216ba07cffc0dffb82f.png', @Sizes, @Toppings),
(8, N'Cà Phê Sữa Nóng', N'Cà phê sữa nóng với vị đắng nhẹ của cà phê hòa quyện cùng vị ngọt béo của sữa, mang đến cảm giác ấm áp và thư giãn.', 39000, 2, 'https://product.hstatic.net/1000075078/product/1737356996_cfs-nong_3d7f237bf9944469bd0d067b9eb6a093.png', @Sizes, @Toppings),
(9, N'Cà Phê Đen Đá', N'Cà phê đen đá mát lạnh, đậm đà, giúp bạn tỉnh táo và sảng khoái trong những ngày nắng nóng.', 29000, 2, 'https://product.hstatic.net/1000075078/product/1737356988_cf-den-da_8f7dd455223e409fbd4caf9bbe394f0b.png', @Sizes, @Toppings),
(10, N'Cà Phê Sữa Đá', N'Cà phê sữa đá với vị đắng nhẹ của cà phê và vị ngọt béo của sữa, là thức uống giải khát tuyệt vời cho mùa hè.', 29000, 2, 'https://product.hstatic.net/1000075078/product/1737357012_cfsd_ab683c1d7cb6429ab3556719ce662f0c.png', @Sizes, @Toppings),
(11, N'Bạc Xỉu Lắc Sữa Yến Mạch', N'Bạc xỉu lắc sữa yến mạch với vị ngọt dịu của sữa yến mạch và hương thơm nhẹ nhàng của cà phê, mang đến một trải nghiệm mới lạ.', 45000, 2, 'https://product.hstatic.net/1000075078/product/1737357063_bac-xiu-lac-sua-yen-mach_cbd6b29a670040ccb1fa14b2799a6156.png', @Sizes, @Toppings),
(12, N'A-Mê Classic', N'A-Mê Classic với vị cà phê đậm đà, thơm nồng, là sự lựa chọn hoàn hảo cho những ai yêu thích hương vị cà phê nguyên bản.', 39000, 5, 'https://product.hstatic.net/1000075078/product/1737354959_ame-classic_db5b4a4f50cd4d3da97e0264ecebe6ec.png', @Sizes, @Toppings),
(13, N'A-Mê Đào', N'A-Mê Đào với vị chua ngọt của đào hòa quyện cùng cà phê, tạo nên một thức uống độc đáo và hấp dẫn.', 55000, 5, 'https://product.hstatic.net/1000075078/product/1737354989_ame-dao_2d10766d733b4399b8599a9064fbe8a5.png', @Sizes, @Toppings),
(14, N'A-Mê Mơ', N'A-Mê Mơ với vị chua nhẹ của mơ và hương thơm của cà phê, mang đến một trải nghiệm thú vị cho vị giác.', 55000, 5, 'https://product.hstatic.net/1000075078/product/1737355000_ame-mo_dc823b45087240f4a724c781cb94e30f.png', @Sizes, @Toppings),
(15, N'A-Mê Quất', N'A-Mê Quất với vị chua thanh của quất và hương thơm của cà phê, tạo nên một thức uống sảng khoái và tươi mới.', 55000, 5, 'https://product.hstatic.net/1000075078/product/1737355010_ame-quat_1174dd5cdd254f99b1e3a0dc662a5ff9.png', @Sizes, @Toppings),
(16, N'A-Mê Tuyết Đào', N'A-Mê Tuyết Đào với vị chua ngọt của đào và lớp tuyết mát lạnh, là sự kết hợp hoàn hảo giữa cà phê và trái cây.', 65000, 5, 'https://product.hstatic.net/1000075078/product/1737355029_ame-tuyet-dao_de2f73da79c643caa6fb56d98db5a9e0.png', @Sizes, @Toppings),
(17, N'A-Mê Tuyết Mơ', N'A-Mê Tuyết Mơ với vị chua nhẹ của mơ và lớp tuyết mát lạnh, mang đến một trải nghiệm thú vị và sảng khoái.', 65000, 5, 'https://product.hstatic.net/1000075078/product/1737355037_ame-tuyet-mo_5421ce73c76041169a860732eb442e18.png', @Sizes, @Toppings),
(18, N'A-Mê Tuyết Quất', N'A-Mê Tuyết Quất với vị chua thanh của quất và lớp tuyết mát lạnh, tạo nên một thức uống độc đáo và hấp dẫn.', 65000, 5, 'https://product.hstatic.net/1000075078/product/1737355048_ame-tuyet-quat_514338c991b64fa4b48d4955a3b48352.png', @Sizes, @Toppings),
(19, N'Americano Nóng', N'Americano nóng với vị cà phê đậm đà, thơm nồng, là sự lựa chọn hoàn hảo cho những ai yêu thích hương vị cà phê nguyên bản.', 49000, 3, 'https://product.hstatic.net/1000075078/product/1737357102_ame-nong_201c8430dc7d479896e5e9c0947f05c6.png', @Sizes, @Toppings),
(20, N'Cappuccino Nóng', N'Cappuccino nóng với lớp bọt sữa mịn màng và hương thơm của cà phê, mang đến cảm giác ấm áp và thư giãn.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357108_cappucino-nong_8102b9252c924903b332fc6cb5f095fe.png', @Sizes, @Toppings),
(21, N'Cappuccino Đá', N'Cappuccino đá mát lạnh với lớp bọt sữa mịn màng và hương thơm của cà phê, là thức uống giải khát tuyệt vời cho mùa hè.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357118_cappucio-da_03315d8d22034e918142c8040101b754.png', @Sizes, @Toppings),
(22, N'Caramel Macchiato Nóng', N'Caramel Macchiato nóng với vị ngọt của caramel và hương thơm của cà phê, mang đến một trải nghiệm thưởng thức đầy tinh tế.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357126_caramel-macchiato-nong_ffc0fb416090487fa17372171d97e167_large.png', @Sizes, @Toppings),
(23, N'Caramel Macchiato Đá', N'Caramel Macchiato đá mát lạnh với vị ngọt của caramel và hương thơm của cà phê, là thức uống giải khát tuyệt vời cho mùa hè.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357135_caramel-macchiato-da_b3952ad1bccd430eaff3962b2aa81541_large.png', @Sizes, @Toppings),
(24, N'Espresso Nóng', N'Espresso nóng đậm đà, thơm nồng, giúp bạn tỉnh táo và tràn đầy năng lượng cho ngày mới.', 45000, 3, 'https://product.hstatic.net/1000075078/product/1737357444_espresso-nong_5fdcc7cf79354261ba37f0615fe54ce7_large.png', @Sizes, @Toppings),
(25, N'Espresso Đá', N'Espresso đá mát lạnh, đậm đà, giúp bạn tỉnh táo và sảng khoái trong những ngày nắng nóng.', 49000, 3, 'https://product.hstatic.net/1000075078/product/1737357429_espresso-da_a1aadc906e3c4ce8ae5cb9bc24d58857_large.png', @Sizes, @Toppings),
(26, N'Latte Nóng', N'Latte nóng với lớp bọt sữa mịn màng và hương thơm của cà phê, mang đến cảm giác ấm áp và thư giãn.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357451_latte-nong_879435385c804cef9a3b723b1d1f9c91_large.png', @Sizes, @Toppings),
(27, N'Latte Đá', N'Latte đá mát lạnh với lớp bọt sữa mịn màng và hương thơm của cà phê, là thức uống giải khát tuyệt vời cho mùa hè.', 55000, 3, 'https://product.hstatic.net/1000075078/product/1737357460_latte-da_65cbfd340d044ff29e324e1089b78fac_large.png', @Sizes, @Toppings),
(28, N'Cold Brew Truyền Thống', N'Cold Brew truyền thống với vị cà phê đậm đà, thơm nồng, được ủ lạnh trong nhiều giờ, mang đến hương vị tinh tế và sảng khoái.', 50000, 4, 'https://product.hstatic.net/1000075078/product/1737357505_coldbrew-classic_9d08e3ee0d154050898affcb0ebb7745_large.png', @Sizes, @Toppings);

SET IDENTITY_INSERT Products OFF;
GO

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
    UNIQUE (user_id, voucher_id)
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

DROP TABLE IF EXISTS Orders;
GO

CREATE TABLE Orders
(
    id                  INT PRIMARY KEY IDENTITY(1,1),
    user_id             INT NULL,
    session_id          VARCHAR(100) NULL,
    order_date          DATETIME DEFAULT (CURRENT_TIMESTAMP),
    status              VARCHAR(50) DEFAULT 'pending',
    order_total         DECIMAL(10, 0) NOT NULL,
    payment_method      VARCHAR(50),
    shipping_address    TEXT NULL,
    order_type          VARCHAR(10) DEFAULT 'PICKUP',
    created_at          DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at          DATETIME DEFAULT (CURRENT_TIMESTAMP),
    customer_name       VARCHAR(255),
    customer_email      VARCHAR(255),
    customer_phone      VARCHAR(20),
    customer_address    TEXT,
    first_product_image_url NVARCHAR(255),
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
    id            INT PRIMARY KEY IDENTITY(1,1),
    order_id      INT NOT NULL,
    product_id    INT NOT NULL,
    quantity      INT NOT NULL,
    size_info     NVARCHAR(MAX),
    toppings_info NVARCHAR(MAX),
    sub_total     DECIMAL(10,0) NOT NULL,
    created_at    DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at    DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (order_id) REFERENCES Orders (id),
    FOREIGN KEY (product_id) REFERENCES Products (id)
);
GO

DROP TABLE IF EXISTS Cart;
GO

CREATE TABLE Cart
(
    id                INT PRIMARY KEY IDENTITY(1,1),
    user_id           INT,
    session_id        VARCHAR(100),
    product_id        INT NOT NULL,
    quantity          INT NOT NULL,
    size_info         NVARCHAR(MAX),
    toppings_info     NVARCHAR(MAX),
    sub_total         DECIMAL(10,0) NOT NULL,
    receive_type      VARCHAR(10),
    created_at        DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at        DATETIME DEFAULT (CURRENT_TIMESTAMP),
    appliedPromotions NVARCHAR(MAX),
    active            BIT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES Users (id),
    FOREIGN KEY (product_id) REFERENCES Products (id),
    CONSTRAINT CHK_Cart_User CHECK (
        (user_id IS NOT NULL AND session_id IS NULL) OR 
        (user_id IS NULL AND session_id IS NOT NULL)
    )
);
GO

CREATE TABLE Promotion (
    id             INT PRIMARY KEY IDENTITY(1,1),
    name           NVARCHAR(255) NOT NULL,
    description    NVARCHAR(1000),
    start_date     DATETIME NOT NULL,
    end_date       DATETIME NOT NULL,
    active         BIT DEFAULT 1,
    promotion_type NVARCHAR(50) NOT NULL,
    priority       INT DEFAULT 0,
    stackable      BIT DEFAULT 0,
    usage_limit    INT DEFAULT NULL,
    usage_count    INT DEFAULT 0,
    created_at     DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at     DATETIME DEFAULT (CURRENT_TIMESTAMP)
);
GO

CREATE TABLE PromotionRule (
    id          INT PRIMARY KEY IDENTITY(1,1),
    promotion_id INT NOT NULL,
    rule_type   NVARCHAR(50) NOT NULL,
    rule_data   NVARCHAR(MAX),
    created_at  DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at  DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (promotion_id) REFERENCES Promotion(id)
);
GO

CREATE TABLE PromotionAction (
    id          INT PRIMARY KEY IDENTITY(1,1),
    promotion_id INT NOT NULL,
    action_type NVARCHAR(50) NOT NULL,
    action_data NVARCHAR(MAX),
    created_at  DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at  DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (promotion_id) REFERENCES Promotion(id)
);
GO

CREATE TABLE PromotionCoupon (
    id          INT PRIMARY KEY IDENTITY(1,1),
    promotion_id INT NOT NULL,
    code        NVARCHAR(50) NOT NULL,
    usage_limit INT DEFAULT NULL,
    usage_count INT DEFAULT 0,
    created_at  DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at  DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (promotion_id) REFERENCES Promotion(id)
);
GO

CREATE TABLE OrderPromotion (
    id             INT PRIMARY KEY IDENTITY(1,1),
    order_id       INT NOT NULL,
    promotion_id   INT NOT NULL,
    discount_amount DECIMAL(10, 0) NOT NULL,
    description    NVARCHAR(255),
    created_at     DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at     DATETIME DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (order_id) REFERENCES Orders (id),
    FOREIGN KEY (promotion_id) REFERENCES Promotion (id)
);
GO

CREATE TABLE UserPromotion (
    id             INT PRIMARY KEY IDENTITY(1,1),
    user_id        INT NOT NULL,
    promotion_id   INT NOT NULL,
    usage_count    INT DEFAULT 0,
    is_redeemed    BIT DEFAULT 0,
    expiry_date    DATETIME,
    created_at     DATETIME DEFAULT (CURRENT_TIMESTAMP),
    updated_at     DATETIME DEFAULT (CURRENT_TIMESTAMP),
    status         VARCHAR(20) NOT NULL,
    percent_discount DECIMAL(5, 2) NULL,
    FOREIGN KEY (promotion_id) REFERENCES Promotion (id)
);
GO

CREATE TABLE Store_Chain
(
    id           INT PRIMARY KEY IDENTITY(1,1),
    name         VARCHAR(50) NOT NULL,
    location     VARCHAR(50) NOT NULL,
    manager_id   INT         NOT NULL,
    open_time    DATETIME,
    closing_time DATETIME,
    FOREIGN KEY (manager_id) REFERENCES Users (id)
);
GO