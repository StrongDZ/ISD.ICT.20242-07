import api from "./api";
import { productService } from "./productService";

export const apiCartService = {
    // Get cart items from API
    getCartItems: async () => {
        try {
            const response = await api.get("/cart");
            // Return items with productDTO property to match CartContext expectations
            const transformedItems = response.data
                .map((item) => ({
                    productDTO: item.productDTO,
                quantity: item.quantity,
                }))
                .sort((a, b) => a.productDTO.productID.localeCompare(b.productDTO.productID));

            return transformedItems;
        } catch (error) {
            console.error("Error fetching cart items from API:", error);
            throw error;
        }
    },

    // Add product to cart via API (với full product)
    addToCart: async (product, quantity) => {
        try {
            const cartItemDTO = {
                productDTO: product,
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
                return total + item.productDTO.price * item.quantity;
            }, 0);
        } catch (error) {
            console.error("Error getting cart total:", error);
            return 0;
        }
    },
};
