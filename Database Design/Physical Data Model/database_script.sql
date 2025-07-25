-- Bảng Product
CREATE TABLE Product (
    productID VARCHAR PRIMARY KEY,
    category VARCHAR,
    title VARCHAR,
    value FLOAT,
    price FLOAT,
    quantity INT,
    description VARCHAR,
    barcode VARCHAR,
    warehouseEntryDate DATE,
    dimensions VARCHAR,
    weight FLOAT,
    imageURL VARCHAR
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
CREATE TABLE "User" (
    id VARCHAR PRIMARY KEY,
    role VARCHAR,
    username VARCHAR,
    password VARCHAR
);

-- Bảng ShopItems (quan hệ giữa Product và Manager)
CREATE TABLE ShopItems (
    productID VARCHAR REFERENCES Product(productID),
    managerID VARCHAR REFERENCES "User"(id),
    PRIMARY KEY (productID, managerID)
);

-- Bảng CartItems (giỏ hàng của khách hàng)
CREATE TABLE CartItems (
    customerID VARCHAR REFERENCES "User"(id),
    productID VARCHAR REFERENCES Product(productID),
    quantity INT,
    PRIMARY KEY (customerID, productID)
);

-- Bảng Order
CREATE TABLE OrderAccount (
    id VARCHAR PRIMARY KEY,
    customerID VARCHAR REFERENCES "User"(id),
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
    orderID VARCHAR PRIMARY KEY REFERENCES OrderAccount(id),
    deliveryAddress VARCHAR,
    phoneNumber VARCHAR,
    recipientName VARCHAR,
    mail VARCHAR,
    province VARCHAR
);

-- Bảng PaymentTransaction
CREATE TABLE PaymentTransaction (
    orderID VARCHAR PRIMARY KEY REFERENCES OrderAccount(id),
    content VARCHAR,
    datetime TIMESTAMP
);

-- Bảng Invoice
CREATE TABLE Invoice (
    orderID VARCHAR PRIMARY KEY REFERENCES OrderAccount(id),
    productPriceExcludingVAT FLOAT,
    productPriceIncludingVAT FLOAT,
    deliveryFee FLOAT
);
