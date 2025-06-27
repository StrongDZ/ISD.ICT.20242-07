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
    const [deliveryInfo, setDeliveryInfo] = useState({
        recipientName: "",
        phoneNumber: "",
        mail: "",
        city: "",
        district: "",
        addressDetail: "",
        isRushOrder: false,
    });
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

    const updateCartItem = async (productID, quantity) => {
        try {
            if (quantity <= 0) {
                await removeFromCart(productID);
                return;
            }

            // Optimistic update - consistent format {product, quantity}
            setCartItems((prevItems) =>
                prevItems.map((item) => {
                    if (item.product.productID === productID) {
                        return { ...item, quantity };
                    }
                    return item;
                })
            );

            // Update in backend/localStorage
            await cartService.updateCartItem(productID, quantity);
        } catch (error) {
            console.error("Error updating cart item:", error);
            // Reload cart on error to ensure consistency
            await loadCartItems();
        }
    };

    const removeFromCart = async (productID) => {
        try {
            // Optimistic update - consistent format
            setCartItems((prevItems) =>
                prevItems.filter((item) => {
                    return item.product.productID !== productID;
                })
            );

            // Remove from backend/localStorage
            await cartService.removeFromCart(productID);
        } catch (error) {
            console.error("Error removing from cart:", error);
            // Reload cart on error to ensure consistency
            await loadCartItems();
        }
    };

    const clearCart = async () => {
        try {
            setCartItems([]);
            await cartService.clearCart();
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

    const isRushOrderEligible = () => {
        // Check if delivery address supports rush order
        const isEligibleAddress =
            deliveryInfo.city === "Hà Nội" &&
            [
                "Ba Đình",
                "Hoàn Kiếm",
                "Tây Hồ",
                "Long Biên",
                "Cầu Giấy",
                "Đống Đa",
                "Hai Bà Trưng",
                "Hoàng Mai",
                "Thanh Xuân",
                "Nam Từ Liêm",
                "Bắc Từ Liêm",
            ].includes(deliveryInfo.district);

        // Check if all products support rush order
        const allProductsEligible = cartItems.every((item) => {
            const product = item.product;

            // For localStorage items, we might not have rushEligible info
            // so we assume books, CDs, and some DVDs are eligible
            if (product.rushEligible !== undefined) {
                return product.rushEligible;
            }
            // Fallback logic for localStorage items
            return product.category === "book" || product.category === "cd" || (product.category === "dvd" && product.price < 1000000);
        });

        return isEligibleAddress && allProductsEligible && cartItems.length > 0;
    };

    const getDeliveryDetails = () => {
        const subtotal = getTotalExcludingVAT();
        const isRushEligible = isRushOrderEligible();
        const rushFee = deliveryInfo.isRushOrder && isRushEligible ? 50000 : 0;

        return {
            isEligible: isRushEligible,
            fee: rushFee,
            estimatedTime: deliveryInfo.isRushOrder && isRushEligible ? "2-4 hours" : "3-5 days",
            restrictions: !isRushEligible ? "Rush delivery only available in Hanoi inner districts" : null,
        };
    };

    const getTotalWeight = () => {
        return cartItems.reduce((weight, item) => {
            const product = item.product;
            const itemWeight = product.weight || 0.5; // Default weight if not specified
            return weight + itemWeight * item.quantity;
        }, 0);
    };

    const calculateDeliveryFee = (useRushOrder = deliveryInfo.isRushOrder) => {
        const subtotal = getTotalExcludingVAT();
        const weight = getTotalWeight();

        let baseFee = 25000; // Base delivery fee

        // Weight-based calculation
        if (weight > 2) {
            baseFee += Math.ceil((weight - 2) / 0.5) * 5000;
        }

        // Distance-based calculation (simplified)
        if (deliveryInfo.city !== "Hà Nội") {
            baseFee += 15000; // Inter-city fee
        }

        // Rush order fee
        if (useRushOrder && isRushOrderEligible()) {
            baseFee += 50000;
        }

        // Free shipping threshold
        if (subtotal >= 500000 && !useRushOrder) {
            baseFee = Math.max(0, baseFee - 25000); // Free standard shipping
        }

        return Math.min(baseFee, 100000); // Cap at 100,000 VND
    };

    const calculateDeliveryFeeWithoutDiscount = () => {
        const weight = getTotalWeight();
        let baseFee = 25000;

        if (weight > 2) {
            baseFee += Math.ceil((weight - 2) / 0.5) * 5000;
        }

        if (deliveryInfo.city !== "Hà Nội") {
            baseFee += 15000;
        }

        if (deliveryInfo.isRushOrder && isRushOrderEligible()) {
            baseFee += 50000;
        }

        return Math.min(baseFee, 100000);
    };

    const updateDeliveryInfo = (newDeliveryInfo) => {
        setDeliveryInfo((prev) => ({
            ...prev,
            ...newDeliveryInfo,
        }));
    };

    const getOrderSummary = () => {
        const subtotal = getTotalExcludingVAT();
        const vat = getVATAmount();
        const deliveryFee = calculateDeliveryFee();
        const total = subtotal + vat + deliveryFee;
        const savings = calculateDeliveryFeeWithoutDiscount() - deliveryFee;

        return {
            items: cartItems,
            itemCount: getCartCount(),
            subtotal,
            vat,
            deliveryFee,
            savings,
            total,
            deliveryInfo,
            weight: getTotalWeight(),
        };
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
        deliveryInfo,
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
        isRushOrderEligible,
        getDeliveryDetails,
        getTotalWeight,
        calculateDeliveryFee,
        updateDeliveryInfo,
        getOrderSummary,
        validateCart,
    };

    return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
