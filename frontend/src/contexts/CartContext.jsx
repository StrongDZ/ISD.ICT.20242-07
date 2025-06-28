import React, { createContext, useContext, useState, useEffect } from "react";
import { cartService } from "../services/cartService";
import { useAuth } from "./AuthContext";

const CartContext = createContext();

export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error("useCart must be used within a CartProvider");
    }
    return context;
};

export const CartProvider = ({ children }) => {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(false);
    const { isAuthenticated } = useAuth();

    // Load cart items on component mount
    useEffect(() => {
        loadCartItems();
    }, []);

    // Handle auth state changes - just reload cart without merging
    useEffect(() => {
        const handleAuthChange = async () => {
            // Service sẽ tự động switch giữa localStorage và API
            await loadCartItems();
        };

        handleAuthChange();
    }, [isAuthenticated()]);

    const loadCartItems = async () => {
        try {
            setLoading(true);
            const items = await cartService.getCartItems();
            setCartItems(items || []);
        } catch (error) {
            console.error("Error loading cart items:", error);
            setCartItems([]);
        } finally {
            setLoading(false);
        }
    };

    const addToCart = async (product, quantity = 1) => {
        try {
            setLoading(true);
            await cartService.addToCart(product, quantity);
            await loadCartItems(); // Reload cart to get updated state
        } catch (error) {
            console.error("Error adding to cart:", error);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const updateCartItem = async (product, quantity) => {
        try {
            if (quantity <= 0) {
                await removeFromCart(product);
                return;
            }

            // Update in backend/localStorage with full product
            await cartService.updateCartItem(product, quantity);
            await loadCartItems();
        } catch (error) {
            console.error("Error updating cart item:", error);
            // Reload cart on error to ensure consistency
            await loadCartItems();
        }
    };

    const removeFromCart = async (product) => {
        try {
            // Remove from backend/localStorage with full product
            await cartService.removeFromCart(product);
            await loadCartItems();
        } catch (error) {
            console.error("Error removing from cart:", error);
            // Reload cart on error to ensure consistency
            await loadCartItems();
        }
    };

    const clearCart = async () => {
        try {
            await cartService.clearCart();
            await loadCartItems();
        } catch (error) {
            console.error("Error clearing cart:", error);
            // Reload cart on error to ensure consistency
            await loadCartItems();
        }
    };

    const getCartTotal = () => {
        return cartItems.reduce((total, item) => {
            return total + item.product.price * item.quantity;
        }, 0);
    };

    const getCartCount = () => {
        return cartItems.reduce((count, item) => count + item.quantity, 0);
    };

    const getTotalExcludingVAT = () => {
        const total = getCartTotal();
        return total / 1.1; // Remove 10% VAT
    };

    const getVATAmount = () => {
        return getCartTotal() - getTotalExcludingVAT();
    };

    const isInCart = (productID) => {
        return cartItems.some((item) => {
            return item.product.productID === productID;
        });
    };

    const getItemQuantity = (productID) => {
        const item = cartItems.find((item) => {
            return item.product.productID === productID;
        });
        return item ? item.quantity : 0;
    };

    const validateCart = () => {
        const errors = [];

        if (cartItems.length === 0) {
            errors.push("Cart is empty");
        }

        // Check stock availability (for API items)
        cartItems.forEach((item) => {
            const product = item.product;
            const productTitle = product.title;
            const stockQuantity = product.quantity;

            if (item.quantity && stockQuantity && item.quantity > stockQuantity) {
                errors.push(`${productTitle}: Requested quantity (${item.quantity}) exceeds available stock (${stockQuantity})`);
            }
        });

        return {
            isValid: errors.length === 0,
            errors,
        };
    };

    const value = {
        cartItems,
        loading,
        addToCart,
        updateCartItem,
        removeFromCart,
        clearCart,
        loadCartItems,
        getCartTotal,
        getCartCount,
        getTotalExcludingVAT,
        getVATAmount,
        isInCart,
        getItemQuantity,
        validateCart,
    };

    return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
