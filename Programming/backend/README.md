# E-commerce Backend Application

This is a Spring Boot backend application for an e-commerce platform that sells books, CDs, and DVDs. The application provides role-based access control with three user roles: Customer, Product Manager, and Administrator.

## Features

-   Users authentication and authorization
-   Role-based access control
-   Product management (CRUD operations)
-   Shopping cart functionality
-   Order processing
-   Payment and invoice generation

## Technology Stack

-   Java 17
-   Spring Boot 3.1.5
-   Spring Security with JWT authentication
-   Spring Data JPA
-   PostgreSQL database
-   Maven

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── aims/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── exception/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               ├── security/
│   │   │               ├── service/
│   │   │               └── AimsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Database Schema

The application uses the following database tables:

-   Product (base product information)
-   Book (extends Product)
-   CD (extends Product)
-   DVD (extends Product)
-   Users (user information with roles)
-   ShopItems (relationship between products and managers)
-   CartItems (shopping cart items)
-   Orders (order information)
-   OrderItems (items in an order)
-   DeliveryInfo (delivery information for orders)
-   PaymentTransaction (payment information)
-   Invoice (invoice information)

## API Endpoints

### Authentication

-   `POST /api/auth/login` - Users login
-   `POST /api/auth/register` - Users registration

### Products (Public)

-   `GET /api/products` - Get all products
-   `GET /api/products/{id}` - Get product by ID
-   `GET /api/products/category/{category}` - Get products by category
-   `GET /api/products/search?keyword={keyword}` - Search products

### Customer (Requires CUSTOMER role)

-   `GET /api/customer/cart` - Get cart items
-   `POST /api/customer/cart/{productId}?quantity={quantity}` - Add product to cart
-   `PUT /api/customer/cart/{productId}?quantity={quantity}` - Update cart item quantity
-   `DELETE /api/customer/cart/{productId}` - Remove product from cart
-   `DELETE /api/customer/cart` - Clear cart
-   `GET /api/customer/orders` - Get customer orders
-   `GET /api/customer/orders/{orderId}` - Get order by ID
-   `POST /api/customer/orders` - Create order from cart

### Product Manager (Requires PRODUCT_MANAGER role)

-   `POST /api/manager/products` - Create product
-   `PUT /api/manager/products/{id}` - Update product
-   `DELETE /api/manager/products/{id}` - Delete product

### Administrator (Requires ADMINISTRATOR role)

-   `GET /api/admin/users` - Get all users
-   `POST /api/admin/users` - Create user
-   `GET /api/admin/orders` - Get all orders
-   `GET /api/admin/orders/status/{status}` - Get orders by status
-   `PUT /api/admin/orders/{orderId}/status?status={status}` - Update order status

## Setup and Installation

1. Clone the repository
2. Configure PostgreSQL database
3. Update `application.properties` with your database credentials
4. Run `mvn clean install` to build the project
5. Run `mvn spring-boot:run` to start the application

## Default Users

The application creates the following default users on startup:

-   Administrator: username=`admin`, password=`admin123`
-   Product Manager: username=`manager`, password=`manager123`
-   Customer: username=`customer`, password=`customer123`
