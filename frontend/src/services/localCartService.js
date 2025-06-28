import api from "./api";

const CART_STORAGE_KEY = "aims_cart_items";

export const localCartService = {
    // Get cart from localStorage
    getCartItems: () => {
        try {
            const cart = localStorage.getItem(CART_STORAGE_KEY);
            return cart ? JSON.parse(cart) : [];
        } catch (error) {
            console.error("Error parsing local cart:", error);
            return [];
        }
    },

    // Refresh product information from server to update stock quantities
    refreshCartItems: async () => {
        try {
            const cartItems = localCartService.getCartItems();
            const updatedCartItems = [];

            for (const item of cartItems) {
                try {
                    // Fetch latest product information from server
                    const response = await api.get(`/products/${item.productDTO.productID}`);
                    const updatedProduct = response.data;

                    // Update the product information while keeping the quantity
                    updatedCartItems.push({
                        productDTO: updatedProduct,
                        quantity: item.quantity,
                    });
                } catch (error) {
                    console.error(`Error refreshing product ${item.productDTO.productID}:`, error);
                    // If we can't fetch the product, keep the old information
                    updatedCartItems.push(item);
                }
            }

            // Save updated cart
            localCartService.saveCart(updatedCartItems);
            return updatedCartItems;
        } catch (error) {
            console.error("Error refreshing cart items:", error);
            return localCartService.getCartItems();
        }
    },

    // Add product to localStorage cart (với full product object)
    addToCart: (product, quantity = 1) => {
        try {
            const cartItems = localCartService.getCartItems();
            const existingItem = cartItems.find((item) => item.productDTO.productID === product.productID);

            if (existingItem) {
                existingItem.quantity = quantity;
            } else {
                // Lưu dạng {product, quantity} giống API
                cartItems.push({
                    productDTO: product,
                    quantity: quantity,
                });
            }

            localCartService.saveCart(cartItems);
            return { productDTO: product, quantity: existingItem ? existingItem.quantity : quantity };
        } catch (error) {
            console.error(`Error adding product ${product.productID} to local cart:`, error);
            throw error;
        }
    },

    // Update cart item quantity in localStorage (với full product)
    updateCartItem: (product, quantity) => {
        try {
            const cartItems = localCartService.getCartItems();
            const item = cartItems.find((item) => item.productDTO.productID === product.productID);

            if (item) {
                item.quantity = quantity;
                localCartService.saveCart(cartItems);
                return { productDTO: product, quantity };
            }
            return null;
        } catch (error) {
            console.error(`Error updating cart item ${product.productID}:`, error);
            throw error;
        }
    },

    // Remove product from localStorage cart (với full product)
    removeFromCart: (product) => {
        try {
            const cartItems = localCartService.getCartItems();
            const filteredItems = cartItems.filter((item) => item.productDTO.productID !== product.productID);
            localCartService.saveCart(filteredItems);
        } catch (error) {
            console.error(`Error removing product ${product.productID} from local cart:`, error);
            throw error;
        }
    },

    // Clear entire localStorage cart
    clearCart: () => {
        try {
            localStorage.removeItem(CART_STORAGE_KEY);
        } catch (error) {
            console.error("Error clearing local cart:", error);
        }
    },

    // Save cart to localStorage
    saveCart: (cartItems) => {
        try {
            localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cartItems));
        } catch (error) {
            console.error("Error saving local cart:", error);
        }
    },

    // Get cart count
    getCartCount: () => {
        const cartItems = localCartService.getCartItems();
        return cartItems.reduce((count, item) => count + item.quantity, 0);
    },

    // Get cart total
    getCartTotal: () => {
        const cartItems = localCartService.getCartItems();
        return cartItems.reduce((total, item) => total + item.productDTO.price * item.quantity, 0);
    },
};
