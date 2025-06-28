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

    // Add product to localStorage cart (với full product object)
    addToCart: (product, quantity = 1) => {
        try {
            const cartItems = localCartService.getCartItems();
            const existingItem = cartItems.find((item) => item.product.productID === product.productID);

            if (existingItem) {
                existingItem.quantity += quantity;
            } else {
                // Lưu dạng {product, quantity} giống API
                cartItems.push({
                    product: product, // Lưu nguyên product object
                    quantity: quantity,
                });
            }

            localCartService.saveCart(cartItems);
            return cartItems.find((item) => item.product.productID === product.productID);
        } catch (error) {
            console.error(`Error adding product ${product.productID} to local cart:`, error);
            throw error;
        }
    },

    // Update cart item quantity in localStorage (với full product)
    updateCartItem: (product, quantity) => {
        try {
            const cartItems = localCartService.getCartItems();
            const itemIndex = cartItems.findIndex((item) => item.product.productID === product.productID);

            if (itemIndex !== -1) {
                if (quantity <= 0) {
                    cartItems.splice(itemIndex, 1);
                } else {
                    cartItems[itemIndex].quantity = quantity;
                }
                localCartService.saveCart(cartItems);
                return cartItems[itemIndex] || null;
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
            const filteredItems = cartItems.filter((item) => item.product.productID !== product.productID);
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

    // Get item quantity
    getItemQuantity: (productId) => {
        const cartItems = localCartService.getCartItems();
        const item = cartItems.find((item) => item.product.productID === productId);
        return item ? item.quantity : 0;
    },

    // Get cart count
    getCartCount: () => {
        const cartItems = localCartService.getCartItems();
        return cartItems.reduce((count, item) => count + item.quantity, 0);
    },

    // Get cart total
    getCartTotal: () => {
        const cartItems = localCartService.getCartItems();
        return cartItems.reduce((total, item) => total + item.product.price * item.quantity, 0);
    },
};
