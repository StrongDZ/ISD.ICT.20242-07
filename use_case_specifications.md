# Use Case Specifications (Narrative Version)

The following specifications describe how a typical customer interacts with the AIMS online store. Technical implementation details (e.g.
method calls, API routes) have been removed so that each step reads as a clear, user-oriented narrative.

---

## UC-01 Add Product to Cart

**Primary Actor:** Customer  
**Goal:** Select a product and reserve it for purchase.  
**Scope:** Shopping Cart  
**Level:** User goal

### Preconditions

1. The customer is viewing the catalogue or the details of an in-stock product.

### Success Guarantees (Post-conditions)

-   The selected item appears in the cart with the desired quantity.
-   Inventory for that product is tentatively reduced or validated.
-   The cart icon immediately reflects the updated item count and value.

### Main Success Scenario

1. The customer presses the **"Add to Cart"** button and optionally chooses a quantity.
2. The system double-checks that the item is still available in the requested amount.
3. The system stores the item in the customer's cart (persisting it on the server for signed-in users or in local storage for guests).
4. The cart total and badge value refresh at the top of the page.
5. A confirmation message briefly notifies the customer that the action succeeded.

### Extensions

-   **Item out of stock:** If insufficient inventory is detected at Step 2, the system shows an error message and suggests a smaller quantity.
-   **Session expired:** If the customer's login has timed out, the system asks them to sign in before retrying the action.

### Special Requirements

-   The feedback message should appear within two seconds in 95 % of cases.
-   Guest carts must survive a browser refresh.

---

## UC-02 Update Cart Item Quantity

**Primary Actor:** Customer

### Preconditions

-   UC-01 has added at least one item to the cart.

### Post-conditions

-   The cart reflects the new quantity or, if the quantity becomes zero, the item is removed.

### Main Success Scenario

1. While reviewing the cart, the customer increases, decreases, or types a new quantity for an existing line item.
2. The system validates the number (must be positive and not exceed current stock).
3. The system records the new quantity and recalculates subtotals, taxes, and the overall total.
4. The page updates to display the revised amounts and—for low stock—any relevant warnings.

### Extensions

-   **Insufficient stock (Step 2):** The system notifies the customer that fewer units are available and displays the exact amount still in stock.
-   **Quantity set to 0:** The system interprets this as a delete request and removes the line item after confirmation.

### Special Requirements

-   **No stock validation during update** - validation occurs only during cart view and checkout.

---

## UC-03 Remove Cart Item

**Primary Actor:** Customer

### Preconditions

-   UC-01 has added at least one item to the cart.

### Post-conditions

-   The specified item is removed from the cart.
-   Cart totals are recalculated.

### Main Success Scenario

1. While reviewing the cart, the customer clicks the "Remove" button for a specific item.
2. The system removes the item from the cart.
3. The cart totals are recalculated and displayed.
4. A confirmation message briefly notifies the customer that the item was removed.

### Extensions

-   **Item already removed:** If the item was already removed by another process, the system shows an appropriate message.

### Special Requirements

-   The removal should be immediate and require no additional confirmation for single items.

---

## UC-04 View Cart

**Primary Actor:** Customer

### Preconditions

-   None - the customer can view an empty cart.

### Post-conditions

-   The customer can see all items in their cart with current stock validation.
-   Any stock issues are clearly indicated.

### Main Success Scenario

1. The customer navigates to the cart page.
2. The system loads all cart items and validates current stock levels.
3. The system displays all items with quantities, prices, and stock status.
4. Items with insufficient stock are clearly marked with warnings.
5. The customer can see subtotals, taxes, and the overall total.

### Extensions

-   **Insufficient stock detected:** Items with insufficient stock are highlighted and show the available quantity.
-   **Empty cart:** The system displays an empty cart message with a link to continue shopping.

### Special Requirements

-   **Stock validation occurs during cart load** - this is a critical validation point.
-   Stock warnings should be clearly visible and actionable.

---

## UC-05 Select Items for Checkout

**Primary Actor:** Customer

### Preconditions

-   UC-04 has loaded the cart with at least one item.

### Post-conditions

-   Selected items are marked for checkout.
-   Only selected items proceed to the checkout process.

### Main Success Scenario

1. The customer reviews items in their cart.
2. The customer uses checkboxes to select specific items for checkout.
3. The system updates the checkout total to reflect only selected items.
4. The customer can proceed to checkout with only the selected items.

### Extensions

-   **No items selected:** The checkout button is disabled until at least one item is selected.
-   **Select all functionality:** The customer can use a "Select All" checkbox to select/deselect all items.

### Special Requirements

-   This is a new feature that allows selective checkout.
-   Stock validation occurs for selected items before proceeding to checkout.

---

## UC-06 Clear Cart

**Primary Actor:** Customer

### Preconditions

-   UC-01 has added at least one item to the cart.

### Post-conditions

-   All items are removed from the cart.
-   The cart is empty.

### Main Success Scenario

1. While reviewing the cart, the customer clicks the "Clear Cart" button.
2. The system asks for confirmation to remove all items.
3. Upon confirmation, the system removes all items from the cart.
4. The cart page shows an empty cart message.

### Extensions

-   **Cancellation:** If the customer cancels the confirmation, no items are removed.

### Special Requirements

-   Clear cart should require confirmation to prevent accidental loss of items.

---

## UC-07 Validate Stock on Cart Load

**Primary Actor:** System

### Preconditions

-   UC-04 is being executed.

### Post-conditions

-   All cart items have current stock validation.
-   Stock issues are identified and marked.

### Main Success Scenario

1. When the cart is loaded, the system retrieves current stock levels for all items.
2. The system compares cart quantities with available stock.
3. Items with insufficient stock are marked with warnings.
4. Available quantities are displayed for items with stock issues.

### Extensions

-   **Product no longer available:** If a product has been discontinued, appropriate messaging is shown.

### Special Requirements

-   This validation occurs automatically during cart load.
-   Stock validation is critical at this point to prevent checkout issues.

---

## UC-08 Continue Shopping

**Primary Actor:** Customer

### Preconditions

-   The customer is viewing the cart.

### Post-conditions

-   The customer is returned to the product catalog.

### Main Success Scenario

1. While viewing the cart, the customer clicks "Continue Shopping".
2. The system navigates the customer back to the product catalog.
3. The cart state is preserved for when the customer returns.

### Extensions

-   **From empty cart:** The system navigates to the main product listing.

### Special Requirements

-   Cart state should be preserved during navigation.

---

## UC-09 View Product Details

**Primary Actor:** Customer

### Preconditions

-   None – the user can access the product catalogue anonymously.

### Main Success Scenario

1. From any listing page, the customer selects a product to learn more.
2. The system gathers comprehensive information about the chosen product (images, description, specifications, stock level, etc.).
3. The system presents a dedicated product page or pop-up detailing those attributes.
4. The customer may now read the description, inspect images, check availability, and—if interested—add the product to the cart (invoking UC-01).

### Extensions

-   **Product no longer exists:** The system shows an apologetic message and offers navigation back to the catalogue.
-   **Temporary network issue:** The page displays a spinner followed by a retry prompt if data cannot be fetched.

---

## UC-10 Search for Products

**Primary Actor:** Customer

### Goal

Locate products that match keywords and optional filters so the customer can quickly find what they need.

### Main Success Scenario

1. The customer enters a search term (and optionally sets filters such as category or price range) and submits the request.
2. The system analyses the search parameters and queries the product catalogue accordingly.
3. Matching products are arranged into pages and shown to the customer along with navigation controls.
4. The customer browses the results, refines the filters, or moves between result pages until satisfied.

### Extensions

-   **No matches found:** The system politely informs the customer and suggests broadening the search criteria.
-   **Server error:** An error banner appears with an option to retry.

### Special Requirements

-   Search inputs should be debounced so that the system is not queried more often than once every 400 ms while the user types.
-   Result pages should load in under three seconds on a standard broadband connection.

---

_Document version 3.0 – 2024-12-19_
