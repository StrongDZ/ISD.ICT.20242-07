import api from "./api";
import { productService } from "./productService";

export const apiCartService = {
    // Get cart items from API
    getCartItems: async () => {
        try {
            const response = await api.get("/cart");
            // Transform API response từ {productDTO, quantity} thành {product, quantity}
            const transformedItems = response.data.map((item) => ({
                product: item.productDTO,
                quantity: item.quantity,
            })).sort((a, b) => a.product.productID.localeCompare(b.product.productID));

            return transformedItems;
        } catch (error) {
            console.error("Error fetching cart items from API:", error);
            throw error;
        }
    },

    // Add product to cart via API (với full product object)
    addToCart: async (product, quantity = 1) => {
        console.log("product", product);
        try {
            const cartItemDTO = {
                productDTO: product, // Gửi toàn bộ product object
                quantity: quantity,
            };
            const response = await api.post("/cart", cartItemDTO);
            return response.data;
        } catch (error) {
            console.error(`Error adding product ${product.productID} to cart via API:`, error);
            throw error;
        }
    },

    // Update cart item quantity via API (với full product)
    updateCartItem: async (product, quantity) => {
        try {
            const cartItemDTO = {
                productDTO: product,
                quantity: quantity,
            };
            const response = await api.put("/cart", cartItemDTO);
            return response.data;
        } catch (error) {
            console.error(`Error updating cart item ${product.productID} via API:`, error);
            throw error;
        }
    },

    // Remove product from cart via API (với full product)
    removeFromCart: async (product) => {
        try {
            await api.delete("/cart", { data: product });
        } catch (error) {
            console.error(`Error removing product ${product.productID} from cart via API:`, error);
            throw error;
        }
    },

    // Clear entire cart via API
    clearCart: async () => {
        try {
            await api.delete("/cart/clear");
        } catch (error) {
            console.error("Error clearing cart via API:", error);
            throw error;
        }
    },


    // Get item quantity
    getItemQuantity: async (productId) => {
        try {
            const item = await productService.getProductById(productId);
            return item ? item.quantity : 0;
        } catch (error) {
            console.error(`Error getting quantity for product ${productId}:`, error);
            return 0;
        }
    },

    // Get cart count
    getCartCount: async () => {
        try {
            const cartItems = await apiCartService.getCartItems();
            return cartItems.reduce((count, item) => count + item.quantity, 0);
        } catch (error) {
            console.error("Error getting cart count:", error);
            return 0;
        }
    },

    // Get cart total
    getCartTotal: async () => {
        try {
            const cartItems = await apiCartService.getCartItems();
            return cartItems.reduce((total, item) => {
                return total + item.product.price * item.quantity;
            }, 0);
        } catch (error) {
            console.error("Error getting cart total:", error);
            return 0;
        }
    },
};
