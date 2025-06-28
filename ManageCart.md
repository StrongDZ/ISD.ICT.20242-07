# Manage Cart Use Case - UML Diagrams

## Overview

The "Manage Cart" use case allows customers to manage their shopping cart by adding products, updating quantities, removing items, and clearing the entire cart.

## Class Diagram

```mermaid
classDiagram
    class CartController {
        -CartService cartService
        +getCartItems(userDetails) ResponseEntity~List~CartItemDTO~~
        +addToCart(cartItemDTO, userDetails) ResponseEntity~CartItemDTO~
        +updateCartItem(cartItemDTO, userDetails) ResponseEntity~CartItemDTO~
        +removeFromCart(productDTO, userDetails) ResponseEntity~Void~
        +clearCart(userDetails) ResponseEntity~Void~
    }

    class CartService {
        -CartItemRepository cartItemRepository
        -UsersRepository userRepository
        -CartItemMapper cartItemMapper
        -ProductRepository productRepository
        +getCartItems(customerId) List~CartItemDTO~
        +addToCart(customerId, productId, quantity) CartItemDTO
        +updateCartItem(customerId, productId, quantity) CartItemDTO
        +removeFromCart(customerId, productId) void
        +clearCart(customerId) void
    }

    class CartItem {
        -CartItemId id
        -Users customer
        -Product product
        -Integer quantity
        +CartItem()
        +CartItem(customerId, productId, quantity)
    }

    class CartItemId {
        -Integer userID
        -String productID
        +CartItemId()
        +CartItemId(userID, productID)
    }

    class CartItemDTO {
        -ProductDTO productDTO
        -Integer quantity
        +CartItemDTO()
        +CartItemDTO(productDTO, quantity)
    }

    class CartItemMapper {
        -ProductMapperFactory mapperFactory
        +toDTO(cartItem) CartItemDTO
        +toEntity(cartItemDTO) CartItem
        +mapProductToDTO(product) ProductDTO
        +mapDTOToProduct(productDTO) Product
    }

    class Product {
        -String productID
        -ProductType category
        -String title
        -Double price
        -Integer quantity
        -Boolean eligible
        +validateStock(requestedQuantity) void
    }

    class Users {
        -Integer id
        -String username
        -String gmail
        -UserType type
        -UserStatus userStatus
    }

    class CartItemRepository {
        +findByCustomer(customer) List~CartItem~
        +deleteByCustomer(customer) void
        +save(cartItem) CartItem
        +deleteById(cartItemId) void
        +existsById(cartItemId) boolean
    }

    class UsersRepository {
        +findById(id) Optional~Users~
    }

    class ProductRepository {
        +findById(id) Optional~Product~
    }

    class ProductDTO {
        -String productID
        -String category
        -String title
        -Double price
        -Integer quantity
        -Boolean eligible
    }

    class UserDetailsImpl {
        -Integer id
        -String username
        -String gmail
        -String password
        -UserStatus userStatus
        +getId() Integer
    }

    %% Relationships
    CartController --> CartService : uses
    CartService --> CartItemRepository : uses
    CartService --> UsersRepository : uses
    CartService --> ProductRepository : uses
    CartService --> CartItemMapper : uses

    CartItem --> CartItemId : has
    CartItem --> Users : belongs to
    CartItem --> Product : contains

    CartItemMapper --> ProductMapperFactory : uses
    CartItemMapper --> ProductDTO : maps to
    CartItemMapper --> Product : maps to

    CartItemRepository --> CartItem : manages
    UsersRepository --> Users : manages
    ProductRepository --> Product : manages

    CartController --> UserDetailsImpl : receives
    CartService --> CartItemDTO : returns
    CartService --> CartItem : manages
```

## Activity Diagram

```mermaid
flowchart TD
    A[Customer accesses cart] --> B{Action Type?}

    B -->|View Cart| C[Get Cart Items]
    B -->|Add Product| D[Add to Cart]
    B -->|Update Quantity| E[Update Cart Item]
    B -->|Remove Product| F[Remove from Cart]
    B -->|Clear Cart| G[Clear Cart]

    C --> C1[Validate customer exists]
    C1 --> C2[Retrieve cart items from database]
    C2 --> C3[Map to DTOs]
    C3 --> C4[Return cart items list]

    D --> D1[Validate customer exists]
    D1 --> D2[Validate product exists]
    D2 --> D3[Validate stock availability]
    D3 --> D4{Stock sufficient?}
    D4 -->|No| D5[Throw BadRequestException]
    D4 -->|Yes| D6[Create new CartItem]
    D6 --> D7[Save to database]
    D7 --> D8[Map to DTO]
    D8 --> D9[Return CartItemDTO]

    E --> E1[Validate product exists]
    E1 --> E2[Validate stock availability]
    E2 --> E3{Stock sufficient?}
    E3 -->|No| E4[Throw BadRequestException]
    E3 -->|Yes| E5[Find existing cart item]
    E5 --> E6{Cart item exists?}
    E6 -->|No| E7[Throw ResourceNotFoundException]
    E6 -->|Yes| E8[Update quantity]
    E8 --> E9[Save to database]
    E9 --> E10[Map to DTO]
    E10 --> E11[Return CartItemDTO]

    F --> F1[Find cart item by ID]
    F1 --> F2{Cart item exists?}
    F2 -->|No| F3[Throw ResourceNotFoundException]
    F2 -->|Yes| F4[Delete from database]
    F4 --> F5[Return success]

    G --> G1[Validate customer exists]
    G1 --> G2[Delete all cart items for customer]
    G2 --> G3[Return success]

    C4 --> H[End]
    D5 --> H
    D9 --> H
    E4 --> H
    E7 --> H
    E11 --> H
    F3 --> H
    F5 --> H
    G3 --> H
```

## Sequence Diagram

### 1. View Cart Items

```mermaid
sequenceDiagram
    participant C as Customer
    participant CC as CartController
    participant CS as CartService
    participant UR as UsersRepository
    participant CIR as CartItemRepository
    participant CIM as CartItemMapper
    participant DB as Database

    C->>CC: GET /api/cart
    CC->>CC: Extract customer ID from UserDetails
    CC->>CS: getCartItems(customerId)
    CS->>UR: findById(customerId)
    UR->>DB: SELECT * FROM users WHERE id = ?
    DB-->>UR: User data
    UR-->>CS: Users object
    CS->>CIR: findByCustomer(customer)
    CIR->>DB: SELECT * FROM cart_items WHERE userID = ?
    DB-->>CIR: Cart items data
    CIR-->>CS: List<CartItem>
    CS->>CIM: toDTO(cartItem) for each item
    CIM-->>CS: List<CartItemDTO>
    CS-->>CC: List<CartItemDTO>
    CC-->>C: HTTP 200 + Cart items
```

### 2. Add Product to Cart

```mermaid
sequenceDiagram
    participant C as Customer
    participant CC as CartController
    participant CS as CartService
    participant UR as UsersRepository
    participant PR as ProductRepository
    participant CIR as CartItemRepository
    participant CIM as CartItemMapper
    participant DB as Database

    C->>CC: POST /api/cart (CartItemDTO)
    CC->>CC: Extract customer ID from UserDetails
    CC->>CC: Extract product ID and quantity from DTO
    CC->>CS: addToCart(customerId, productId, quantity)
    CS->>UR: findById(customerId)
    UR->>DB: SELECT * FROM users WHERE id = ?
    DB-->>UR: User data
    UR-->>CS: Users object
    CS->>PR: findById(productId)
    PR->>DB: SELECT * FROM products WHERE productID = ?
    DB-->>PR: Product data
    PR-->>CS: Product object
    CS->>CS: product.validateStock(quantity)
    CS->>CS: Create new CartItem
    CS->>CIR: save(cartItem)
    CIR->>DB: INSERT INTO cart_items VALUES (...)
    DB-->>CIR: Saved cart item
    CIR-->>CS: CartItem object
    CS->>CIM: toDTO(cartItem)
    CIM-->>CS: CartItemDTO
    CS-->>CC: CartItemDTO
    CC-->>C: HTTP 200 + CartItemDTO
```

### 3. Update Cart Item

```mermaid
sequenceDiagram
    participant C as Customer
    participant CC as CartController
    participant CS as CartService
    participant PR as ProductRepository
    participant CIR as CartItemRepository
    participant CIM as CartItemMapper
    participant DB as Database

    C->>CC: PUT /api/cart (CartItemDTO)
    CC->>CC: Extract customer ID from UserDetails
    CC->>CC: Extract product ID and quantity from DTO
    CC->>CS: updateCartItem(customerId, productId, quantity)
    CS->>PR: findById(productId)
    PR->>DB: SELECT * FROM products WHERE productID = ?
    DB-->>PR: Product data
    PR-->>CS: Product object
    CS->>CS: product.validateStock(quantity)
    CS->>CIR: findById(cartItemId)
    CIR->>DB: SELECT * FROM cart_items WHERE userID = ? AND productID = ?
    DB-->>CIR: Cart item data
    CIR-->>CS: CartItem object
    CS->>CS: cartItem.setQuantity(quantity)
    CS->>CIR: save(cartItem)
    CIR->>DB: UPDATE cart_items SET quantity = ? WHERE userID = ? AND productID = ?
    DB-->>CIR: Updated cart item
    CIR-->>CS: CartItem object
    CS->>CIM: toDTO(cartItem)
    CIM-->>CS: CartItemDTO
    CS-->>CC: CartItemDTO
    CC-->>C: HTTP 200 + CartItemDTO
```

### 4. Remove Product from Cart

```mermaid
sequenceDiagram
    participant C as Customer
    participant CC as CartController
    participant CS as CartService
    participant CIR as CartItemRepository
    participant DB as Database

    C->>CC: DELETE /api/cart (ProductDTO)
    CC->>CC: Extract customer ID from UserDetails
    CC->>CC: Extract product ID from DTO
    CC->>CS: removeFromCart(customerId, productId)
    CS->>CS: Create CartItemId(customerId, productId)
    CS->>CIR: existsById(cartItemId)
    CIR->>DB: SELECT COUNT(*) FROM cart_items WHERE userID = ? AND productID = ?
    DB-->>CIR: Count result
    CIR-->>CS: boolean exists
    alt Cart item exists
        CS->>CIR: deleteById(cartItemId)
        CIR->>DB: DELETE FROM cart_items WHERE userID = ? AND productID = ?
        DB-->>CIR: Deletion result
        CIR-->>CS: void
        CS-->>CC: void
        CC-->>C: HTTP 200
    else Cart item not found
        CS-->>CC: ResourceNotFoundException
        CC-->>C: HTTP 404
    end
```

### 5. Clear Cart

```mermaid
sequenceDiagram
    participant C as Customer
    participant CC as CartController
    participant CS as CartService
    participant UR as UsersRepository
    participant CIR as CartItemRepository
    participant DB as Database

    C->>CC: DELETE /api/cart/clear
    CC->>CC: Extract customer ID from UserDetails
    CC->>CS: clearCart(customerId)
    CS->>UR: findById(customerId)
    UR->>DB: SELECT * FROM users WHERE id = ?
    DB-->>UR: User data
    UR-->>CS: Users object
    CS->>CIR: deleteByCustomer(customer)
    CIR->>DB: DELETE FROM cart_items WHERE userID = ?
    DB-->>CIR: Deletion result
    CIR-->>CS: void
    CS-->>CC: void
    CC-->>C: HTTP 200
```

## Key Components and Responsibilities

### Controllers

-   **CartController**: Handles HTTP requests and responses, extracts user information from authentication context

### Services

-   **CartService**: Contains business logic for cart operations, validates data, and coordinates between repositories

### Repositories

-   **CartItemRepository**: Manages database operations for cart items
-   **UsersRepository**: Manages user data access
-   **ProductRepository**: Manages product data access

### Mappers

-   **CartItemMapper**: Converts between entity and DTO objects using MapStruct

### Models

-   **CartItem**: JPA entity representing a cart item with embedded ID
-   **CartItemId**: Embedded ID class for composite primary key
-   **CartItemDTO**: Data transfer object for cart items

## Error Handling

The system handles various error scenarios:

-   **ResourceNotFoundException**: When customer, product, or cart item is not found
-   **BadRequestException**: When quantity is invalid or insufficient stock
-   **Validation errors**: When input data doesn't meet requirements

## Security

-   All cart operations require authentication
-   Customer can only access their own cart items
-   User ID is extracted from JWT token context
