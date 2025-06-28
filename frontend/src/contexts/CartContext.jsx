import React, { createContext, useContext, useState, useEffect } from "react";
import { cartService } from "../services/cartService";
import { useAuth } from "./AuthContext";
import { productService } from "../services/productService";

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
    const { user } = useAuth();

    // Load cart items when user changes
    useEffect(() => {
        loadCartItems();
    }, [user]);

    // Update cart service reference when auth status changes
    useEffect(() => {
        cartService.updateServiceReference();
    }, [user]);


    const loadCartItems = async () => {
        try {
            // For non-authenticated users, refresh cart items to get latest stock info
            const items = await cartService.refreshCartItems();
            setCartItems(items);
        } catch (error) {
            console.error("Failed to load cart items:", error);
        }
    };

    const getCartCount = () => {
        return cartItems.reduce((count, item) => {
            if (item && item.quantity) {
                return count + item.quantity;
            }
            return count;
        }, 0);
    };


    const addToCart = async (product, quantity = 1) => {
        try {
            const newItem = await cartService.addToCart(product, quantity);
            await loadCartItems();
            return newItem;
        } catch (error) {
            console.error("Failed to add to cart:", error);
            throw error;
        }
    };

    const updateCartItem = async (product, quantity) => {
        try {
            if (quantity > product.quantity) quantity = product.quantity;

            const updatedItem = await cartService.updateCartItem(product, quantity);
            await loadCartItems();
            return updatedItem;
        } catch (error) {
            console.error("Failed to update cart item:", error);

            // Handle inventory error specifically
            if (error.response && error.response.data) {
                const errorMessage = error.response.data.message || error.response.data;
                if (errorMessage.includes("Not enough stock") || errorMessage.includes("insufficient")) {
                    // Fetch latest product info from server
                    try {
                        const response = await productService.getProductById(product.productID);
                        const latestProduct = response.data || response;
                        await loadCartItems();
                        throw new Error(`Hàng không đủ: ${product.title}. Số lượng có sẵn mới nhất: ${latestProduct.quantity}`);
                    } catch (refreshError) {
                        throw new Error("Số lượng hàng trong kho vừa được cập nhật");
                    }
                }
            }

            // Handle other errors
            if (error.message) {
                throw new Error(error.message);
            } else {
                throw new Error("Có lỗi xảy ra khi cập nhật giỏ hàng");
            }
        }
    };

    const removeFromCart = async (product) => {
        try {
            await cartService.removeFromCart(product);
            await loadCartItems();
        } catch (error) {
            console.error("Failed to remove from cart:", error);
            throw error;
        }
    };

    const clearCart = async () => {
        try {
            await cartService.clearCart();
            setCartItems([]);
        } catch (error) {
            console.error("Failed to clear cart:", error);
            throw error;
        }
    };


    const getCartTotal = () => {
        return cartItems.reduce((total, item) => {
            if (item && item.productDTO) {
                return total + item.productDTO.price * item.quantity;
            }
            return total;
        }, 0);
    };

    const getTotalExcludingVAT = () => {
        return getCartTotal() / 1.1; // Assuming 10% VAT
    };

    const getVATAmount = () => {
        return getCartTotal() - getTotalExcludingVAT();
    };



    const getInventoryStatus = (item) => {
        if (!item || !item.productDTO) return { status: "unknown", message: "", shortfall: 0 };

        const product = item.productDTO;
        const availableStock = product.quantity || 0;
        const requestedQuantity = item.quantity || 0;

        if (availableStock === 0) {
            return {
                status: "out-of-stock",
                message: "Sản phẩm hiện không có sẵn",
                shortfall: requestedQuantity,
            };
        } else if (requestedQuantity > availableStock) {
            const shortfall = requestedQuantity - availableStock;
            return {
                status: "insufficient",
                message: `Chỉ còn ${availableStock} (thiếu ${shortfall})`,
                shortfall: shortfall,
            };
        } else if (availableStock <= 5) {
            return {
                status: "low-stock",
                message: `Chỉ còn ${availableStock} sản phẩm trong kho`,
                shortfall: 0,
            };
        } else {
            return {
                status: "available",
                message: `${availableStock} sản phẩm có sẵn`,
                shortfall: 0,
            };
        }
    };


    const hasInventoryIssues = () => {
        return cartItems.some((item) => {
            if (!item || !item.productDTO) return false;
            const product = item.productDTO;
            const availableStock = product.quantity || 0;
            const requestedQuantity = item.quantity || 0;
            return availableStock === 0 || requestedQuantity > availableStock;
        });
    };

    const value = {
        cartItems,
        addToCart,
        updateCartItem,
        removeFromCart,
        clearCart,
        getCartTotal,
        getTotalExcludingVAT,
        getVATAmount,
        getInventoryStatus,
        getCartCount,
        hasInventoryIssues,
        loadCartItems,
    };

    return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
