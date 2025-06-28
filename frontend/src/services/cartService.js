import { localCartService } from "./localCartService";
import { apiCartService } from "./apiCartService";

// Helper function to check if user is authenticated
const isAuthenticated = () => {
    return !!localStorage.getItem("token");
};

// Current cart service reference - switches based on auth status
let currentCartService = isAuthenticated() ? apiCartService : localCartService;

// Update the current cart service reference
const updateCartServiceReference = () => {
    currentCartService = isAuthenticated() ? apiCartService : localCartService;
};

export const cartService = {
    // Update service reference when auth status changes
    updateServiceReference: updateCartServiceReference,

    getCurrentService: () => currentCartService,

    // Local service is sync, API service is async

    getCartItems: async () => {
        updateCartServiceReference();
        if (currentCartService === localCartService) {
            return currentCartService.getCartItems();
        } else {
            return await currentCartService.getCartItems();
        }
    },

    // Add product to cart (với full product)
    addToCart: async (product, quantity = 1) => {
        updateCartServiceReference();
        try {
            if (currentCartService === localCartService) {
                return currentCartService.addToCart(product, quantity);
            } else {
                return await currentCartService.addToCart(product, quantity);
            }
        } catch (error) {
            console.error(`Error adding product ${product.productID} to cart:`, error);
            throw error;
        }
    },

    // Update cart item quantity (với full product)
    updateCartItem: async (product, quantity) => {
        updateCartServiceReference();
        try {
            if (currentCartService === localCartService) {
                return currentCartService.updateCartItem(product, quantity);
            } else {
                return await currentCartService.updateCartItem(product, quantity);
            }
        } catch (error) {
            console.error(`Error updating cart item ${product.productID}:`, error);
            throw error;
        }
    },

    // Remove product from cart (với full product)
    removeFromCart: async (product) => {
        updateCartServiceReference();
        try {
            if (currentCartService === localCartService) {
                return currentCartService.removeFromCart(product);
            } else {
                return await currentCartService.removeFromCart(product);
            }
        } catch (error) {
            console.error(`Error removing product ${product.productID} from cart:`, error);
            throw error;
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
