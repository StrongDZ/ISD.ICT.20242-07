# AIMS - Advanced Interactive Media Store

Đây là một hệ thống thương mại điện tử để bán các sản phẩm truyền thông như sách, CD và DVD.

## Cấu trúc dự án

```
ISD.ICT.20242-07/
├── backend/                  # Spring Boot API
│   ├── src/
│   │   ├── main/java/com/example/aims/
│   │   └── test/
│   └── ...
├── frontend/                 # React.js Application
│   ├── public/
│   ├── src/
│   │   ├── components/       # Reusable UI components
│   │   ├── contexts/         # React contexts (Auth, Cart)
│   │   ├── pages/           # Page components
│   │   └── services/        # API services
│   └── package.json
└── README.md
```

## Tính năng chính

### Frontend (React.js)

#### 🔑 Authentication & Authorization

-   Đăng nhập/đăng xuất
-   Quản lý phiên làm việc
-   Phân quyền theo vai trò (Customer, Product Manager, Administrator)

#### 🛍️ Tính năng khách hàng

-   Trang chủ với sản phẩm nổi bật
-   Xem danh sách sản phẩm (20 sản phẩm/trang)
-   Tìm kiếm và lọc sản phẩm
-   Sắp xếp theo giá, tên, danh mục
-   Xem chi tiết sản phẩm
-   Thêm vào giỏ hàng
-   Quản lý giỏ hàng (thêm/sửa/xóa)
-   Thanh toán đơn hàng
-   Giao hàng nhanh (rush order) cho nội thành Hà Nội

#### 🏢 Tính năng quản lý

-   **Product Manager:**

    -   Thêm/sửa/xóa sản phẩm
    -   Quản lý đơn hàng
    -   Duyệt/từ chối đơn hàng

-   **Administrator:**
    -   Quản lý người dùng
    -   Tạo/sửa/xóa tài khoản
    -   Phân quyền

#### 🛒 Giỏ hàng & Thanh toán

-   Tính toán tự động VAT (10%)
-   Tính phí giao hàng theo trọng lượng và địa điểm
-   Miễn phí giao hàng cho đơn hàng > 100.000 VND
-   Tích hợp VNPay (sandbox)
-   Giao hàng nhanh 2 giờ (nội thành Hà Nội)

## Công nghệ sử dụng

### Frontend

-   **React 18.2.0** - Framework chính
-   **Material-UI (MUI) 5.10.0** - Component library
-   **React Router 6.3.0** - Navigation
-   **React Query 3.39.2** - Data fetching
-   **React Hook Form 7.34.0** - Form handling
-   **Axios 0.27.2** - HTTP client

### Backend

-   **Spring Boot 3.x** - Framework chính
-   **Spring Security** - Authentication & Authorization
-   **Spring Data JPA** - Database operations
-   **JWT** - Token-based authentication
-   **PostgreSQL/H2** - Database
-   **Swagger/OpenAPI** - API documentation

## Hướng dẫn cài đặt

### Prerequisites

-   Node.js 16+ và npm
-   Java 17+
-   PostgreSQL (hoặc sử dụng H2 cho development)

### 1. Clone repository

```bash
git clone <repository-url>
cd ISD.ICT.20242-07
```

### 2. Cài đặt Frontend

```bash
cd frontend
npm install
```

### 3. Cài đặt Backend

```bash
cd backend
./mvnw clean install
```

### 4. Cấu hình Database

Tạo file `backend/src/main/resources/application.yml`:

```yaml
spring:
    datasource:
        url: jdbc:postgresql://localhost:5432/aims_db
        username: your_username
        password: your_password
    jpa:
        hibernate:
            ddl-auto: update
        show-sql: true

app:
    jwt:
        secret: your-secret-key-here
        expiration: 86400000

rush:
    address:
        city: "Ha Noi"
        districts:
            - "Ba Dinh"
            - "Hoan Kiem"
            - "Dong Da"
            - "Hai Ba Trung"
```

### 5. Chạy ứng dụng

#### Backend (Port 8080)

```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend (Port 3000)

```bash
cd frontend
npm start
```

## Tài khoản demo

Hệ thống tự động tạo các tài khoản demo:

-   **Customer:** `customer` / `customer123`
-   **Product Manager:** `manager` / `manager123`
-   **Administrator:** `admin` / `admin123`

## API Endpoints

### Authentication

-   `POST /api/auth/login` - Đăng nhập
-   `POST /api/auth/register` - Đăng ký

### Products

-   `GET /api/products` - Danh sách sản phẩm
-   `GET /api/products/{id}` - Chi tiết sản phẩm
-   `GET /api/products/category/{category}` - Sản phẩm theo danh mục
-   `GET /api/products/search?keyword=` - Tìm kiếm

### Cart (Customer)

-   `GET /api/customer/cart` - Xem giỏ hàng
-   `POST /api/customer/cart/{productId}` - Thêm vào giỏ
-   `PUT /api/customer/cart/{productId}` - Cập nhật số lượng
-   `DELETE /api/customer/cart/{productId}` - Xóa khỏi giỏ

### Product Management (Manager)

-   `POST /api/manager/products` - Thêm sản phẩm
-   `PUT /api/manager/products/{id}` - Cập nhật sản phẩm
-   `DELETE /api/manager/products/{id}` - Xóa sản phẩm

### User Management (Admin)

-   `GET /api/admin/users` - Danh sách người dùng
-   `POST /api/admin/users` - Tạo người dùng mới

## Cấu trúc Frontend

### Components

```
src/components/
├── Common/
│   └── LoadingSpinner.jsx      # Component loading
├── Layout/
│   ├── Layout.jsx              # Layout chính
│   ├── Header.jsx              # Header với navigation
│   └── Footer.jsx              # Footer
└── Product/
    └── ProductCard.jsx         # Card hiển thị sản phẩm
```

### Pages

```
src/pages/
├── HomePage/                   # Trang chủ
├── ProductsPage/              # Danh sách sản phẩm
├── ProductDetailPage/         # Chi tiết sản phẩm
├── CartPage/                  # Giỏ hàng
├── CheckoutPage/              # Thanh toán
├── LoginPage/                 # Đăng nhập
├── AdminPages/                # Trang quản trị
│   ├── AdminDashboard.jsx
│   └── UserManagementPage.jsx
└── ManagerPages/              # Trang quản lý
    ├── ManagerDashboard.jsx
    ├── ProductManagementPage.jsx
    └── OrderManagementPage.jsx
```

### Contexts

```
src/contexts/
├── AuthContext.jsx            # Quản lý authentication
└── CartContext.jsx            # Quản lý giỏ hàng
```

### Services

```
src/services/
├── api.js                     # Axios configuration
├── authService.js             # Authentication API
├── productService.js          # Product API
└── cartService.js             # Cart API
```

## Tính năng đặc biệt

### 🚀 Giao hàng nhanh (Rush Order)

-   Chỉ có ở nội thành Hà Nội
-   Giao hàng trong 2 giờ
-   Phí phụ thu 10.000 VND/sản phẩm
-   Kiểm tra tự động tính khả dụng

### 💰 Tính phí giao hàng

-   **Hà Nội/TP.HCM:** 22.000 VND cho 3kg đầu
-   **Các tỉnh khác:** 30.000 VND cho 0.5kg đầu
-   Thêm 2.500 VND cho mỗi 0.5kg tiếp theo
-   Miễn phí cho đơn hàng > 100.000 VND (tối đa 25.000 VND)

### 🛡️ Bảo mật

-   JWT token authentication
-   Role-based access control
-   CORS configuration
-   Input validation

## Development

### Thêm sản phẩm mới

1. Sử dụng tài khoản Product Manager
2. Truy cập `/manager/products`
3. Điền thông tin sản phẩm theo danh mục:
    - **Book:** authors, publisher, coverType, pages, language, genre
    - **CD:** artist, recordLabel, tracklist, musicType
    - **DVD:** director, studio, runtime, language, subtitle

### Customize theme

Chỉnh sửa theme trong `src/App.jsx`:

```javascript
const theme = createTheme({
    palette: {
        primary: { main: "#1976d2" },
        secondary: { main: "#dc004e" },
    },
});
```

## Deployment

### Build production

```bash
# Frontend
cd frontend
npm run build

# Backend
cd backend
./mvnw clean package -DskipTests
```

### Environment variables

```bash
# Backend
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL=your_production_db_url
export JWT_SECRET=your_production_secret

# Frontend
export REACT_APP_API_URL=https://your-api-domain.com
```

## Troubleshooting

### Common Issues

1. **CORS Error:**

    - Kiểm tra configuration trong `backend/src/main/java/.../config/CorsConfig.java`

2. **JWT Token Error:**

    - Xóa localStorage và đăng nhập lại
    - Kiểm tra token expiration time

3. **Database Connection:**

    - Kiểm tra PostgreSQL service
    - Verify database credentials

4. **Port conflicts:**
    - Frontend: Thay đổi port trong `package.json`
    - Backend: Thay đổi `server.port` trong `application.yml`

## Contributing

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact

-   Team 07 - ISD.ICT.20242
-   Project Link: [https://github.com/yourusername/ISD.ICT.20242-07](https://github.com/yourusername/ISD.ICT.20242-07)
