# Class Cohesion and SRP Evaluation

## Legend

-   **Functional Cohesion**: All elements work together for a single, well-defined task
-   **Sequential Cohesion**: Elements work together in a sequence where output of one is input to next
-   **Procedural Cohesion**: Elements grouped because they follow a certain sequence of execution
-   **Logical Cohesion**: Elements grouped because they logically belong to the same category but perform different tasks

---

## Evaluation Results

| Class Name                     | PIC                           | Cohesion                                                                                                                                                                                                                            | SRP Compliance                                                                                                                                                        | Solution                                                                                                                          |
| ------------------------------ | ----------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- |
| **CartService**                | Manh_20225984                 | **Sequential**: Methods like getCartItems(), addToCart(), updateCartItem() follow sequential steps (validate → query → transform → save) but each handles different cart operations                                                 | **No**: 4 responsibilities - 1. Cart CRUD operations 2. User/Product validation 3. Stock validation 4. DTO mapping coordination                                       | Extract to: **CartValidator** (user/product existence), **StockValidator** (inventory checks), keep only core cart business logic |
| **DVD, CD, Book**              | Manh_20225984                 | **Functional**: Each holds fields specific to one product type only                                                                                                                                                                 | **Yes**: Pure data classes with single responsibility of holding product-specific attributes                                                                          | No changes needed - properly designed entities                                                                                    |
| **GlobalExceptionHandler**     | Manh_20225984                 | **Logical Cohesion**: Multiple methods handle different exception types with varying logic and response formats                                                                                                                     | **No**: 3 responsibilities - 1. Exception handling 2. Response formatting 3. Validation error processing                                                              | Split into: **ValidationExceptionHandler**, **SecurityExceptionHandler**, **BusinessExceptionHandler**                            |
| **CartItem**                   | Manh_20225984                 | **Functional**: Holds fields related to cart items only                                                                                                                                                                             | **Yes**: Pure data class representing cart-product relationship                                                                                                       | No changes needed - proper entity design                                                                                          |
| **AuthService**                | Manh_20225984                 | **Sequential**: authenticateUser() follows sequence: authenticate → generate token → build response                                                                                                                                 | **No**: 3 responsibilities - 1. Authentication 2. JWT token generation 3. Response construction                                                                       | Extract: **JwtTokenService** for token operations, keep only authentication coordination                                          |
| **ProductService (Interface)** | Huyen_20220073, Manh_20225984 | **Functional**: All methods focused on product operations                                                                                                                                                                           | **Yes**: Single responsibility of defining product service contract                                                                                                   | Well-designed interface - no changes needed                                                                                       |
| **ProductServiceImpl**         | Huyen_20220073, Manh_20225984 | **Procedural**: Methods like getAllProducts(), searchProducts(), getFilteredProducts() follow similar procedural pattern (repository query → map via strategy → collect) but handle different operations with different logic flows | **No**: 5 responsibilities - 1. Strategy coordination 2. Repository querying 3. Pagination logic 4. Sort logic (getSort method) 5. Stream transformation coordination | Extract: **ProductPaginationHandler**, **ProductSortHandler**, **ProductQueryCoordinator**, keep only pure strategy delegation    |
| **CartItemMapper**             | -                             | **Functional**: Solely focused on CartItem ↔ CartItemDTO mapping                                                                                                                                                                    | **Yes**: Single responsibility of mapping between CartItem entities and DTOs                                                                                          | Well-designed mapper - no changes needed                                                                                          |
| **ProductMapperHelper**        | -                             | **Functional**: Focused on Product ↔ ProductDTO mapping with type resolution                                                                                                                                                        | **Yes**: Single responsibility of resolving correct mapper and performing product mapping                                                                             | Well-designed helper - no changes needed                                                                                          |
| **ProductMapperFactory**       | -                             | **Functional**: Focused on providing correct ProductMapper based on type                                                                                                                                                            | **Yes**: Single responsibility of mapper creation and type resolution                                                                                                 | Well-designed factory - no changes needed                                                                                         |
| **ViewProductController**      | -                             | **Sequential**: Methods follow REST pattern: receive request → validate → delegate → format response                                                                                                                                | **No**: 3 responsibilities - 1. HTTP request handling 2. Parameter validation/transformation 3. Response formatting                                                   | Extract: **ProductRequestValidator**, **ProductResponseFormatter**, keep only HTTP routing                                        |
| **CartController**             | -                             | **Sequential**: Methods follow REST pattern: receive request → validate → delegate → format response                                                                                                                                | **No**: 3 responsibilities - 1. HTTP request handling 2. User authentication handling 3. Response formatting                                                          | Extract: **CartRequestValidator**, **CartResponseFormatter**, keep only HTTP routing                                              |

---

## Detailed Analysis

### Well-Designed Classes (High Cohesion + SRP Compliant)

1. **Product Entities (Book, CD, DVD)**: Perfect functional cohesion with single data responsibility
2. **CartItem Entity**: Clean entity design focused on cart-product relationship
3. **ProductService Interface**: Well-defined contract with single responsibility
4. **CartItemMapper**: Focused mapping responsibility with proper delegation
5. **ProductMapperHelper**: Clean helper with single mapping coordination responsibility
6. **ProductMapperFactory**: Proper factory pattern implementation

### Classes Needing Improvement

#### High Priority (Multiple Responsibilities)

-   **CartService**: Too many validation concerns mixed with business logic
-   **ProductServiceImpl**: Strategy coordination mixed with pagination and validation
-   **GlobalExceptionHandler**: Different exception types require different handling strategies

#### Medium Priority (Design Improvements)

-   **ViewProductController**: Parameter handling can be extracted
-   **CartController**: Authentication and validation logic can be separated
-   **AuthService**: Token generation can be separated from authentication

---

## Recommended Refactoring Priorities

1. **CartService** → Extract validators and keep pure cart operations
2. **ProductServiceImpl** → Extract pagination and validation services
3. **GlobalExceptionHandler** → Split by exception categories
4. **Controllers** → Extract request/response processors

This evaluation shows the codebase has good foundational design with entities and mappers, but services and controllers need responsibility separation for better maintainability.
