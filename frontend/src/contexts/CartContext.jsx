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
    const [selectedItems, setSelectedItems] = useState([]);
    const [validationErrors, setValidationErrors] = useState([]);
    const { user } = useAuth();

    // Load cart items when user changes
    useEffect(() => {
        loadCartItems();
    }, [user]);

    // Update cart service reference when auth status changes
    useEffect(() => {
        cartService.updateServiceReference();
    }, [user]);

    // Validate cart items when cart changes
    useEffect(() => {
        if (cartItems.length > 0) {
            validateCart();
        } else {
            setValidationErrors([]);
        }
    }, [cartItems]);

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

    const validateCart = async () => {
        if (cartItems.length === 0) {
            setValidationErrors([]);
            return;
        }

        try {
            const products = cartItems.map((item) => item.productDTO);
            const errors = await validateProducts(products);
            setValidationErrors(errors);
        } catch (error) {
            console.error("Failed to validate cart:", error);
        }
    };

    const validateProducts = async (products) => {
        if (!products || products.length === 0) return [];

        try {
            const errors = [];
            for (const product of products) {
                if (product.quantity < 1) {
                    errors.push({
                        productId: product.productID,
                        message: `Sản phẩm "${product.title}" không đủ số lượng trong kho (có ${product.quantity})`,
                    });
                }
            }
            return errors;
        } catch (error) {
            console.error("Error validating products:", error);
            return [];
        }
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
                    throw new Error(`Hàng không đủ: ${product.title}. Số lượng có sẵn: ${product.quantity}`);
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
            setSelectedItems([]);
        } catch (error) {
            console.error("Failed to clear cart:", error);
            throw error;
        }
    };

    const toggleItemSelection = (productId) => {
        setSelectedItems((prev) => {
            if (prev.includes(productId)) {
                return prev.filter((id) => id !== productId);
            } else {
                return [...prev, productId];
            }
        });
    };

    const selectAllItems = () => {
        const allProductIds = cartItems
            .filter((item) => item && item.productDTO) // Filter out items without productDTO
            .map((item) => item.productDTO.productID);
        setSelectedItems(allProductIds);
    };

    const deselectAllItems = () => {
        setSelectedItems([]);
    };

    const getSelectedCartItems = () => {
        return cartItems.filter((item) => item && item.productDTO && selectedItems.includes(item.productDTO.productID));
    };

    const getTotalPrice = () => {
        const itemsToCalculate = selectedItems.length > 0 ? getSelectedCartItems() : [];
        return itemsToCalculate.reduce((total, item) => {
            if (item && item.productDTO) {
                return total + item.productDTO.price * item.quantity;
            }
            return total;
        }, 0);
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

    const getTotalWithVAT = () => {
        return getTotalPrice();
    };

    const getSelectedItemsCount = () => {
        return selectedItems.length;
    };

    const hasSelectedItems = () => {
        return selectedItems.length > 0;
    };

    const hasValidationErrors = () => {
        return validationErrors.length > 0;
    };

    const getValidationErrorsForProduct = (productId) => {
        return validationErrors.filter((error) => error.productId === productId);
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
        selectedItems,
        validationErrors,
        addToCart,
        updateCartItem,
        removeFromCart,
        clearCart,
        toggleItemSelection,
        selectAllItems,
        deselectAllItems,
        getSelectedCartItems,
        getTotalPrice,
        getCartTotal,
        getTotalExcludingVAT,
        getVATAmount,
        getTotalWithVAT,
        getSelectedItemsCount,
        hasSelectedItems,
        hasValidationErrors,
        getValidationErrorsForProduct,
        validateProducts,
        getCartCount,
        hasInventoryIssues,
        loadCartItems,
    };

    return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
