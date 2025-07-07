# Daily Limits Feature for Product Manager

## Tổng quan

Tính năng này giới hạn số lượng sản phẩm mà Product Manager có thể update hoặc delete trong một ngày để đảm bảo an toàn hệ thống. Hệ thống được thiết kế cho một Product Manager duy nhất.

## Quy tắc

- **Update limit**: Tối đa 30 sản phẩm/ngày
- **Delete limit**: Tối đa 30 sản phẩm/ngày  
- **Add limit**: Không giới hạn (unlimited)

## Các thành phần đã thêm

### 1. Model
- `ManagerActivity.java`: Entity để track hoạt động hàng ngày của manager

### 2. Repository
- `ManagerActivityRepository.java`: Interface để truy vấn dữ liệu hoạt động

### 3. Service
- `ManagerActivityService.java`: Service chính để quản lý daily limits
- Cập nhật `ProductService.java` và `ProductServiceImpl.java` để tích hợp daily limits

### 4. Exception
- `ManagerDailyLimitException.java`: Exception khi vượt quá giới hạn

### 5. Controller
- Cập nhật `ManagerController.java` để sử dụng daily limits
- Thêm endpoint `/api/manager/daily-limits` để kiểm tra limits

### 6. DTO
- `DailyLimitsResponseDTO.java`: Response DTO cho daily limits

## Kiến trúc đơn giản hóa

### Gộp các hàm xử lý
- **Unified Methods**: Tất cả các hàm xử lý đơn lẻ và đống đều sử dụng cùng một method signature
- **No Duplicate Methods**: Loại bỏ các hàm trùng lặp như `updateProductWithLimit`, `deleteProductWithLimit`
- **Built-in Limits**: Daily limits được tích hợp trực tiếp vào các method chính

### Loại bỏ tham số không cần thiết
- **No Manager ID**: Loại bỏ tất cả các tham số `managerId` không cần thiết
- **Simplified API**: API endpoints không cần truyền managerId
- **Single Manager System**: Hệ thống được thiết kế cho một manager duy nhất

## API Endpoints

### GET /api/manager/daily-limits
Trả về thông tin daily limits của manager hiện tại.

**Response:**
```json
{
  "managerId": 1,
  "updateCount": 15,
  "deleteCount": 10,
  "updateLimit": 30,
  "deleteLimit": 30,
  "message": "Daily limits retrieved successfully"
}
```

### PUT /api/manager/products/{id}
Update sản phẩm với daily limit check.

### DELETE /api/manager/products/{id}
Delete sản phẩm với daily limit check.

### DELETE /api/manager/products/bulk
Bulk delete sản phẩm với daily limit check.

## Database Schema

```sql
CREATE TABLE manager_activity (
    id BIGSERIAL PRIMARY KEY,
    activity_date DATE NOT NULL UNIQUE,
    update_count INTEGER NOT NULL DEFAULT 0,
    delete_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Tests

- `ManagerActivityServiceTest.java`: Unit tests cho service
- `ManagerControllerTest.java`: Integration tests cho controller

## Cách hoạt động

1. Khi Product Manager thực hiện update/delete sản phẩm, hệ thống sẽ kiểm tra daily limit
2. Nếu chưa vượt quá giới hạn, tăng counter và cho phép thực hiện
3. Nếu đã vượt quá giới hạn, throw `ManagerDailyLimitException`
4. Exception được handle và trả về HTTP 429 (Too Many Requests)

### Batch Operations Optimization
- **Single Operations**: Tăng counter 1 cho mỗi lần update/delete đơn lẻ
- **Batch Operations**: Tăng counter theo số lượng sản phẩm trong batch (ví dụ: delete 5 sản phẩm = tăng 5)
- **Efficient Processing**: Batch operations được xử lý hiệu quả hơn với một lần check limit thay vì nhiều lần

### Method Signatures
- `checkAndIncrementUpdateCount(int increment)`: Tăng theo increment (cho cả single và batch operations)
- `checkAndIncrementDeleteCount(int increment)`: Tăng theo increment (cho cả single và batch operations)
- **Single Operations**: Truyền increment = 1
- **Batch Operations**: Truyền increment = số lượng sản phẩm

## Đặc điểm của hệ thống đơn giản

- Chỉ có một Product Manager duy nhất
- Tracking hoạt động theo ngày, không theo từng manager riêng biệt
- Database schema đơn giản hơn với chỉ một record per day
- API endpoints không cần truyền managerId

## Lưu ý

- Limits được reset mỗi ngày (00:00)
- Chỉ áp dụng cho update và delete, không áp dụng cho create
- Bulk operations cũng được tính vào daily limits
- System logs sẽ ghi lại mỗi lần update/delete để tracking 