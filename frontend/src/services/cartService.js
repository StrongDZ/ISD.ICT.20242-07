import { localCartService } from "./localCartService";
import { apiCartService } from "./apiCartService";

// Helper function to check if user is logged in
const isLoggedIn = () => {
    return !!localStorage.getItem("token");
};

// Current cart service reference - switches based on auth status
let currentCartService = isLoggedIn() ? apiCartService : localCartService;

// Update the current cart service reference
const updateCartServiceReference = () => {
    currentCartService = isLoggedIn() ? apiCartService : localCartService;
};

export const cartService = {
    // Update service reference when auth status changes
    updateServiceReference: updateCartServiceReference,

    // Get current cart service (for debugging/testing)
    getCurrentService: () => currentCartService,

    // All cart operations use the current service
    getCartItems: async () => {
        updateCartServiceReference();
        // Local service is sync, API service is async
        if (currentCartService === localCartService) {
            return currentCartService.getCartItems();
        } else {
            return await currentCartService.getCartItems();
        }
    },

    // Add product với full product object
    addToCart: async (product, quantity = 1) => {
        updateCartServiceReference();
        // Cả 2 service đều nhận full product object
        if (currentCartService === localCartService) {
            return currentCartService.addToCart(product, quantity);
        } else {
            return await currentCartService.addToCart(product, quantity);
        }
    },

    // Update, remove, các operations khác vẫn dùng productId
    updateCartItem: async (productId, quantity) => {
        updateCartServiceReference();
        if (currentCartService === localCartService) {
            return currentCartService.updateCartItem(productId, quantity);
        } else {
            return await currentCartService.updateCartItem(productId, quantity);
        }
    },

    removeFromCart: async (productId) => {
        updateCartServiceReference();
        if (currentCartService === localCartService) {
            return currentCartService.removeFromCart(productId);
        } else {
            return await currentCartService.removeFromCart(productId);
        }
    },

    clearCart: async () => {
        updateCartServiceReference();
        if (currentCartService === localCartService) {
            return currentCartService.clearCart();
        } else {
            return await currentCartService.clearCart();
        }
    },

    isInCart: async (productId) => {
        updateCartServiceReference();
        // Handle sync/async difference
        if (currentCartService === localCartService) {
            return currentCartService.isInCart(productId);
        } else {
            return await currentCartService.isInCart(productId);
        }
    },

    getItemQuantity: async (productId) => {
        updateCartServiceReference();
        // Handle sync/async difference
        if (currentCartService === localCartService) {
            return currentCartService.getItemQuantity(productId);
        } else {
            return await currentCartService.getItemQuantity(productId);
        }
    },

    getCartCount: async () => {
        updateCartServiceReference();
        // Handle sync/async difference
        if (currentCartService === localCartService) {
            return currentCartService.getCartCount();
        } else {
            return await currentCartService.getCartCount();
        }
    },

    getCartTotal: async () => {
        updateCartServiceReference();
        // Handle sync/async difference
        if (currentCartService === localCartService) {
            return currentCartService.getCartTotal();
        } else {
            return await currentCartService.getCartTotal();
        }
    },
};
