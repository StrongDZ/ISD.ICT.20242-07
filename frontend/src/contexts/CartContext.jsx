import React, { createContext, useContext, useState, useEffect } from "react";
import { cartService } from "../services/cartService";
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
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [deliveryInfo, setDeliveryInfo] = useState({
        address: "",
        city: "",
        district: "",
        ward: "",
        phone: "",
        customerName: "",
        isRushOrder: false,
    });

    // Load cart from localStorage on component mount
    useEffect(() => {
        const savedCart = localStorage.getItem("cart");
        const savedDeliveryInfo = localStorage.getItem("deliveryInfo");

        if (savedCart) {
            try {
                setCartItems(JSON.parse(savedCart));
            } catch (error) {
                console.error("Error parsing cart from localStorage:", error);
                localStorage.removeItem("cart");
            }
        }

        if (savedDeliveryInfo) {
            try {
                setDeliveryInfo(JSON.parse(savedDeliveryInfo));
            } catch (error) {
                console.error("Error parsing delivery info from localStorage:", error);
                localStorage.removeItem("deliveryInfo");
            }
        }
    }, []);

    // Save cart to localStorage whenever cartItems changes
    useEffect(() => {
        localStorage.setItem("cart", JSON.stringify(cartItems));
    }, [cartItems]);

    // Save delivery info to localStorage
    useEffect(() => {
        localStorage.setItem("deliveryInfo", JSON.stringify(deliveryInfo));
    }, [deliveryInfo]);

    const addToCart = async (product, quantity = 1) => {
        try {
            setLoading(true);
            setError(null);

            const existingItemIndex = cartItems.findIndex((item) => item.productID === product.productID);

            if (existingItemIndex >= 0) {
                // Update existing item
                const updatedItems = [...cartItems];
                updatedItems[existingItemIndex].quantity += quantity;
                setCartItems(updatedItems);
            } else {
                // Add new item with product details
                const newItem = {
                    productID: product.productID,
                    productTitle: product.title,
                    productPrice: product.price,
                    productValue: product.value,
                    quantity: quantity,
                    imageURL: product.imageURL,
                    category: product.category,
                    weight: product.weight || 0.5, // Default weight if not specified
                    rushEligible: product.rushEligible || false,
                    availableQuantity: product.quantity,
                };
                setCartItems([...cartItems, newItem]);
            }
        } catch (error) {
            setError("Failed to add item to cart");
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const updateCartItem = (productID, quantity) => {
        if (quantity <= 0) {
            removeFromCart(productID);
            return;
        }

        setCartItems((prevItems) => prevItems.map((item) => (item.productID === productID ? { ...item, quantity } : item)));
    };

    const removeFromCart = (productID) => {
        setCartItems((prevItems) => prevItems.filter((item) => item.productID !== productID));
    };

    const clearCart = () => {
        setCartItems([]);
        setDeliveryInfo({
            address: "",
            city: "",
            district: "",
            ward: "",
            phone: "",
            customerName: "",
            isRushOrder: false,
        });
        localStorage.removeItem("cart");
        localStorage.removeItem("deliveryInfo");
    };

    const getCartTotal = () => {
        return cartItems.reduce((total, item) => {
            return total + item.productPrice * item.quantity;
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
        const totalExcludingVAT = getTotalExcludingVAT();
        return totalExcludingVAT * 0.1; // 10% VAT
    };

    const isInCart = (productID) => {
        return cartItems.some((item) => item.productID === productID);
    };

    const getItemQuantity = (productID) => {
        const item = cartItems.find((item) => item.productID === productID);
        return item ? item.quantity : 0;
    };

    // Check if rush order is eligible based on location and products
    const isRushOrderEligible = () => {
        const hanoiInnerCityDistricts = [
            "ba đình",
            "hoàn kiếm",
            "tây hồ",
            "long biên",
            "cầu giấy",
            "đống đa",
            "hai bà trưng",
            "hoàng mai",
            "thanh xuân",
            "nam từ liêm",
        ];

        const isValidLocation =
            deliveryInfo.city?.toLowerCase() === "hà nội" && hanoiInnerCityDistricts.includes(deliveryInfo.district?.toLowerCase());

        const allItemsEligible = cartItems.every((item) => item.rushEligible);

        return isValidLocation && allItemsEligible && cartItems.length > 0;
    };

    // Get detailed delivery information
    const getDeliveryDetails = () => {
        const totalWeight = getTotalWeight();
        const subtotal = getCartTotal();
        const isRush = deliveryInfo.isRushOrder;
        const rushEligible = isRushOrderEligible();

        let deliveryFee = calculateDeliveryFee();
        let deliveryTime = "3-5 working days";

        if (isRush && rushEligible) {
            deliveryTime = "Within 2 hours";
        } else if (isRush && !rushEligible) {
            // If rush is requested but not eligible, fall back to standard
            deliveryFee = calculateDeliveryFee(false); // Recalculate without rush
            deliveryTime = "3-5 working days (Rush not available for this order)";
        }

        return {
            totalWeight: totalWeight,
            deliveryFee: deliveryFee,
            deliveryTime: deliveryTime,
            isRushOrder: isRush && rushEligible,
            rushEligible: rushEligible,
            freeShippingApplied: !isRush && subtotal > 100000 && deliveryFee < calculateDeliveryFeeWithoutDiscount(),
        };
    };

    // Calculate total weight of cart items
    const getTotalWeight = () => {
        return cartItems.reduce((weight, item) => {
            return weight + (item.weight || 0.5) * item.quantity;
        }, 0);
    };

    // Calculate delivery fee based on weight, location, and rush order
    const calculateDeliveryFee = (useRushOrder = deliveryInfo.isRushOrder) => {
        if (cartItems.length === 0) return 0;

        const totalWeight = getTotalWeight();
        const city = deliveryInfo.city?.toLowerCase();
        const district = deliveryInfo.district?.toLowerCase();
        let baseFee = 0;

        // Base fee calculation based on location
        if (city === "hà nội" || city === "hồ chí minh") {
            baseFee = 22000; // First 3kg
            if (totalWeight > 3) {
                const extraWeight = Math.ceil((totalWeight - 3) / 0.5);
                baseFee += extraWeight * 2500;
            }
        } else {
            baseFee = 30000; // First 0.5kg for other provinces
            if (totalWeight > 0.5) {
                const extraWeight = Math.ceil((totalWeight - 0.5) / 0.5);
                baseFee += extraWeight * 2500;
            }
        }

        // Rush order additional fee (only if eligible)
        if (useRushOrder && isRushOrderEligible()) {
            baseFee += cartItems.length * 10000; // 10,000 VND per item
        }

        // Free shipping for orders over 100,000 VND (excluding rush orders)
        if (!useRushOrder && getCartTotal() > 100000) {
            const discount = Math.min(baseFee, 25000); // Max 25,000 VND discount
            baseFee = Math.max(0, baseFee - discount);
        }

        return baseFee;
    };

    // Calculate delivery fee without free shipping discount for comparison
    const calculateDeliveryFeeWithoutDiscount = () => {
        if (cartItems.length === 0) return 0;

        const totalWeight = getTotalWeight();
        const city = deliveryInfo.city?.toLowerCase();
        let baseFee = 0;

        if (city === "hà nội" || city === "hồ chí minh") {
            baseFee = 22000;
            if (totalWeight > 3) {
                const extraWeight = Math.ceil((totalWeight - 3) / 0.5);
                baseFee += extraWeight * 2500;
            }
        } else {
            baseFee = 30000;
            if (totalWeight > 0.5) {
                const extraWeight = Math.ceil((totalWeight - 0.5) / 0.5);
                baseFee += extraWeight * 2500;
            }
        }

        return baseFee;
    };

    // Update delivery information
    const updateDeliveryInfo = (newDeliveryInfo) => {
        setDeliveryInfo((prevInfo) => ({
            ...prevInfo,
            ...newDeliveryInfo,
        }));
    };

    // Get order summary with all calculations
    const getOrderSummary = () => {
        const subtotal = getCartTotal();
        const subtotalExcludingVAT = getTotalExcludingVAT();
        const vatAmount = getVATAmount();
        const deliveryDetails = getDeliveryDetails();
        const total = subtotal + deliveryDetails.deliveryFee;

        return {
            items: cartItems,
            itemCount: getCartCount(),
            subtotal: subtotal,
            subtotalExcludingVAT: subtotalExcludingVAT,
            vatAmount: vatAmount,
            deliveryFee: deliveryDetails.deliveryFee,
            total: total,
            totalWeight: deliveryDetails.totalWeight,
            deliveryTime: deliveryDetails.deliveryTime,
            isRushOrder: deliveryDetails.isRushOrder,
            rushEligible: deliveryDetails.rushEligible,
            freeShippingApplied: deliveryDetails.freeShippingApplied,
            deliveryInfo: deliveryInfo,
        };
    };

    // Validate cart for checkout
    const validateCart = () => {
        const errors = [];

        if (cartItems.length === 0) {
            errors.push("Cart is empty");
        }

        // Check stock availability
        cartItems.forEach((item) => {
            if (item.quantity > item.availableQuantity) {
                errors.push(`${item.productTitle}: Only ${item.availableQuantity} items available`);
            }
        });

        // Validate delivery info
        if (!deliveryInfo.customerName) errors.push("Customer name is required");
        if (!deliveryInfo.phone) errors.push("Phone number is required");
        if (!deliveryInfo.address) errors.push("Address is required");
        if (!deliveryInfo.city) errors.push("City is required");
        if (!deliveryInfo.district) errors.push("District is required");

        // Rush order validation
        if (deliveryInfo.isRushOrder && !isRushOrderEligible()) {
            if (deliveryInfo.city?.toLowerCase() !== "hà nội") {
                errors.push("Rush delivery is only available in Hanoi");
            } else {
                const nonEligibleItems = cartItems.filter((item) => !item.rushEligible);
                if (nonEligibleItems.length > 0) {
                    errors.push(`Rush delivery not available for: ${nonEligibleItems.map((item) => item.productTitle).join(", ")}`);
                }
            }
        }

        return {
            isValid: errors.length === 0,
            errors: errors,
        };
    };

    const value = {
        cartItems,
        loading,
        error,
        deliveryInfo,
        addToCart,
        updateCartItem,
        removeFromCart,
        clearCart,
        getCartTotal,
        getCartCount,
        getTotalExcludingVAT,
        getVATAmount,
        isInCart,
        getItemQuantity,
        calculateDeliveryFee,
        updateDeliveryInfo,
        isRushOrderEligible,
        getDeliveryDetails,
        getTotalWeight,
        getOrderSummary,
        validateCart,
    };

    return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
