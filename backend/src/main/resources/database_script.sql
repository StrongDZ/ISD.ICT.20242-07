-- Bảng Product
CREATE TABLE Product (
    productID VARCHAR PRIMARY KEY,
    category VARCHAR,
    title VARCHAR,
    value DOUBLE PRECISION,
    price DOUBLE PRECISION,
    quantity INT,
    description VARCHAR,
    barcode VARCHAR,
    warehouseEntryDate DATE,
    dimensions VARCHAR,
    weight DOUBLE PRECISION,
    imageURL VARCHAR,
    eligible BOOLEAN
);

-- Bảng Book kế thừa Product
CREATE TABLE Book (
    productID VARCHAR PRIMARY KEY REFERENCES Product(productID),
    coverType VARCHAR,
    authors VARCHAR,
    publisher VARCHAR,
    numberOfPages INT,
    language VARCHAR,
    genre VARCHAR,
    pubDate DATE
);

-- Bảng CD kế thừa Product
CREATE TABLE CD (
    productID VARCHAR PRIMARY KEY REFERENCES Product(productID),
    tracklist VARCHAR,
    artist VARCHAR,
    releaseDate DATE,
    recordLabel VARCHAR,
    musicType VARCHAR
);

-- Bảng DVD kế thừa Product
CREATE TABLE DVD (
    productID VARCHAR PRIMARY KEY REFERENCES Product(productID),
    discType VARCHAR,
    runtime VARCHAR,
    studio VARCHAR,
    director VARCHAR,
    subtitle VARCHAR,
    releaseDate DATE,
    language VARCHAR,
    genre VARCHAR
);

-- Bảng User
CREATE TABLE "user" (
    id VARCHAR PRIMARY KEY,
    role VARCHAR,
    username VARCHAR,
    password VARCHAR,
    gmail VARCHAR,
    userStatus VARCHAR
);

-- Bảng ShopItems (quan hệ giữa Product và Manager)
CREATE TABLE ShopItems (
    productID VARCHAR REFERENCES Product(productID),
    managerID VARCHAR REFERENCES "user"(id),
    PRIMARY KEY (productID, managerID)
);

-- Bảng CartItems (giỏ hàng của khách hàng)
CREATE TABLE CartItems (
    customerID VARCHAR REFERENCES "user"(id),
    productID VARCHAR REFERENCES Product(productID),
    quantity INT,
    PRIMARY KEY (customerID, productID)
);

-- Bảng Order
CREATE TABLE OrderAccount (
    id VARCHAR PRIMARY KEY,
    customerID VARCHAR REFERENCES "user"(id),
    status VARCHAR
);

-- Bảng OrderItems
CREATE TABLE OrderItems (
    productID VARCHAR REFERENCES Product(productID),
    orderID VARCHAR REFERENCES OrderAccount(id),
    quantity INT,
    PRIMARY KEY (productID, orderID)
);

-- Bảng DeliveryInfo
CREATE TABLE DeliveryInfo (
    orderID VARCHAR PRIMARY KEY REFERENCES "user"(id),
    deliveryAddress VARCHAR,
    phoneNumber VARCHAR,
    recipientName VARCHAR,
    mail VARCHAR,
    province VARCHAR
);

-- Bảng PaymentTransaction
CREATE TABLE PaymentTransaction (
    orderID VARCHAR PRIMARY KEY REFERENCES "user"(id),
    content VARCHAR,
    datetime TIMESTAMP
);

-- Bảng Invoice
CREATE TABLE Invoice (
    orderID VARCHAR PRIMARY KEY REFERENCES "user"(id),
    productPriceExcludingVAT DOUBLE PRECISION,
    productPriceIncludingVAT DOUBLE PRECISION,
    deliveryFee DOUBLE PRECISION
);

-- Create manager_activity table for tracking daily limits
CREATE TABLE IF NOT EXISTS manager_activity (
    id BIGSERIAL PRIMARY KEY,
    activity_date DATE NOT NULL UNIQUE,
    update_count INTEGER NOT NULL DEFAULT 0,
    delete_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add price tracking columns to Product table
ALTER TABLE Product ADD COLUMN IF NOT EXISTS old_price DECIMAL(10,2);
ALTER TABLE Product ADD COLUMN IF NOT EXISTS update_count INTEGER DEFAULT 0;
ALTER TABLE Product ADD COLUMN IF NOT EXISTS update_at DATE;

-- Create index for better performance
CREATE INDEX IF NOT EXISTS idx_manager_activity_date ON manager_activity(activity_date);
