import api from "./api";

export const cartService = {
    // Get all cart items for current user
    getCartItems: async () => {
        try {
            const response = await api.get("/cart");
            return response.data;
        } catch (error) {
            console.error("Error fetching cart items:", error);
            throw error;
        }
    },

    // Add product to cart
    addToCart: async (productId, quantity = 1) => {
        try {
            const response = await api.post(`/cart/${productId}`, null, {
                params: { quantity },
            });
            return response.data;
        } catch (error) {
            console.error(`Error adding product ${productId} to cart:`, error);
            throw error;
        }
    },

    // Update cart item quantity
    updateCartItem: async (productId, quantity) => {
        try {
            const response = await api.put(`/cart/${productId}`, null, {
                params: { quantity },
            });
            return response.data;
        } catch (error) {
            console.error(`Error updating cart item ${productId}:`, error);
            throw error;
        }
    },

    // Remove product from cart
    removeFromCart: async (productId) => {
        try {
            await api.delete(`/cart/${productId}`);
        } catch (error) {
            console.error(`Error removing product ${productId} from cart:`, error);
            throw error;
        }
    },

    // Clear entire cart
    clearCart: async () => {
        try {
            await api.delete("/cart");
        } catch (error) {
            console.error("Error clearing cart:", error);
            throw error;
        }
    },

    // Create order from cart
    createOrder: async (orderData) => {
        try {
            const response = await api.post("/orders", orderData);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to create order");
        }
    },

    // Get order by ID
    getOrder: async (orderId) => {
        try {
            const response = await api.get(`/orders/${orderId}`);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to fetch order");
        }
    },

    // Get user orders
    getUserOrders: async (page = 1, limit = 10) => {
        try {
            const response = await api.get(`/orders/user?page=${page}&limit=${limit}`);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to fetch orders");
        }
    },

    // Cancel order
    cancelOrder: async (orderId) => {
        try {
            const response = await api.put(`/orders/${orderId}/cancel`);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to cancel order");
        }
    },

    // Process VNPay payment
    processPayment: async (paymentData) => {
        try {
            const response = await api.post("/payments/vnpay", paymentData);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Payment processing failed");
        }
    },

    // Verify VNPay payment
    verifyPayment: async (paymentParams) => {
        try {
            const response = await api.post("/payments/vnpay/verify", paymentParams);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Payment verification failed");
        }
    },

    // Calculate delivery fee
    calculateDeliveryFee: async (deliveryInfo) => {
        try {
            const response = await api.post("/delivery/calculate-fee", deliveryInfo);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to calculate delivery fee");
        }
    },

    // Check rush order eligibility
    checkRushEligibility: async (deliveryInfo, cartItems) => {
        try {
            const response = await api.post("/delivery/rush-eligibility", {
                deliveryInfo,
                cartItems,
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to check rush eligibility");
        }
    },

    // Mock order data for development
    getMockOrders: () => {
        return [
            {
                orderID: "ORDER_001",
                userID: "customer1",
                customerName: "John Customer",
                customerPhone: "0123456789",
                customerEmail: "customer@aims.com",
                deliveryAddress: "123 Customer Street, Hanoi, Vietnam",
                orderDate: "2024-01-15T10:30:00Z",
                status: "DELIVERED",
                paymentStatus: "PAID",
                paymentMethod: "VNPAY",
                isRushOrder: false,
                items: [
                    {
                        productID: "1",
                        productTitle: "Clean Code",
                        quantity: 1,
                        unitPrice: 450000,
                        totalPrice: 450000,
                    },
                    {
                        productID: "5",
                        productTitle: "Bohemian Rhapsody - Greatest Hits",
                        quantity: 1,
                        unitPrice: 350000,
                        totalPrice: 350000,
                    },
                ],
                subtotal: 800000,
                vatAmount: 72727,
                deliveryFee: 0, // Free shipping applied
                totalAmount: 800000,
                estimatedDeliveryTime: "3-5 working days",
                actualDeliveryTime: "2024-01-17T14:20:00Z",
            },
            {
                orderID: "ORDER_002",
                userID: "customer1",
                customerName: "John Customer",
                customerPhone: "0123456789",
                customerEmail: "customer@aims.com",
                deliveryAddress: "123 Customer Street, Hanoi, Vietnam",
                orderDate: "2024-01-20T14:15:00Z",
                status: "SHIPPING",
                paymentStatus: "PAID",
                paymentMethod: "VNPAY",
                isRushOrder: false,
                items: [
                    {
                        productID: "11",
                        productTitle: "Avengers: Endgame",
                        quantity: 1,
                        unitPrice: 650000,
                        totalPrice: 650000,
                    },
                ],
                subtotal: 650000,
                vatAmount: 59091,
                deliveryFee: 0, // Free shipping applied
                totalAmount: 650000,
                estimatedDeliveryTime: "3-5 working days",
                trackingNumber: "AIMS2024012001",
            },
            {
                orderID: "ORDER_003",
                userID: "customer2",
                customerName: "Jane Doe",
                customerPhone: "0987654321",
                customerEmail: "jane.doe@email.com",
                deliveryAddress: "456 Main Road, Ho Chi Minh City, Vietnam",
                orderDate: "2024-01-22T16:45:00Z",
                status: "PROCESSING",
                paymentStatus: "PAID",
                paymentMethod: "VNPAY",
                isRushOrder: false,
                items: [
                    {
                        productID: "2",
                        productTitle: "The Great Gatsby",
                        quantity: 2,
                        unitPrice: 280000,
                        totalPrice: 560000,
                    },
                ],
                subtotal: 560000,
                vatAmount: 50909,
                deliveryFee: 0, // Free shipping applied
                totalAmount: 560000,
                estimatedDeliveryTime: "3-5 working days",
            },
            {
                orderID: "ORDER_004",
                userID: "customer3",
                customerName: "Alice Reader",
                customerPhone: "0345678901",
                customerEmail: "reader@books.com",
                deliveryAddress: "789 Literature Lane, Hanoi Inner City, Vietnam",
                orderDate: "2024-01-25T09:30:00Z",
                status: "DELIVERED",
                paymentStatus: "PAID",
                paymentMethod: "VNPAY",
                isRushOrder: true,
                items: [
                    {
                        productID: "1",
                        productTitle: "Clean Code",
                        quantity: 1,
                        unitPrice: 450000,
                        totalPrice: 450000,
                    },
                ],
                subtotal: 450000,
                vatAmount: 40909,
                deliveryFee: 32000, // Rush delivery fee
                totalAmount: 482000,
                estimatedDeliveryTime: "Within 2 hours",
                actualDeliveryTime: "2024-01-25T11:15:00Z",
            },
            {
                orderID: "ORDER_005",
                userID: "customer3",
                customerName: "Alice Reader",
                customerPhone: "0345678901",
                customerEmail: "reader@books.com",
                deliveryAddress: "789 Literature Lane, Hanoi Inner City, Vietnam",
                orderDate: "2024-01-28T11:00:00Z",
                status: "PENDING_APPROVAL",
                paymentStatus: "PENDING",
                paymentMethod: "VNPAY",
                isRushOrder: false,
                items: [
                    {
                        productID: "8",
                        productTitle: "Dark Side of the Moon",
                        quantity: 1,
                        unitPrice: 850000,
                        totalPrice: 850000,
                    },
                    {
                        productID: "9",
                        productTitle: "Thriller",
                        quantity: 1,
                        unitPrice: 920000,
                        totalPrice: 920000,
                    },
                ],
                subtotal: 1770000,
                vatAmount: 160909,
                deliveryFee: 0, // Free shipping applied
                totalAmount: 1770000,
                estimatedDeliveryTime: "3-5 working days",
                needsApproval: true,
                approvalReason: "High-value order requires manager approval",
            },
        ];
    },

    // Get order statistics for dashboard
    getOrderStatistics: () => {
        const orders = cartService.getMockOrders();

        return {
            totalOrders: orders.length,
            pendingOrders: orders.filter((o) => o.status === "PENDING_APPROVAL").length,
            processingOrders: orders.filter((o) => o.status === "PROCESSING").length,
            shippingOrders: orders.filter((o) => o.status === "SHIPPING").length,
            deliveredOrders: orders.filter((o) => o.status === "DELIVERED").length,
            rushOrders: orders.filter((o) => o.isRushOrder).length,
            totalRevenue: orders.reduce((sum, order) => sum + order.totalAmount, 0),
            averageOrderValue: orders.length > 0 ? orders.reduce((sum, order) => sum + order.totalAmount, 0) / orders.length : 0,
        };
    },

    // Mock VNPay payment processing
    mockVNPayPayment: async (paymentData) => {
        // Simulate payment processing delay
        await new Promise((resolve) => setTimeout(resolve, 2000));

        const success = Math.random() > 0.1; // 90% success rate

        if (success) {
            return {
                success: true,
                transactionId: `VNPAY_${Date.now()}`,
                paymentUrl: `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=${
                    paymentData.amount * 100
                }&vnp_Command=pay&vnp_CreateDate=${
                    new Date().toISOString().replace(/[-:]/g, "").split(".")[0]
                }&vnp_CurrCode=VND&vnp_IpAddr=127.0.0.1&vnp_Locale=vn&vnp_OrderInfo=${encodeURIComponent(
                    paymentData.orderInfo
                )}&vnp_OrderType=other&vnp_ReturnUrl=${encodeURIComponent(paymentData.returnUrl)}&vnp_TmnCode=DEMO&vnp_TxnRef=${
                    paymentData.orderRef
                }&vnp_Version=2.1.0`,
                message: "Payment URL generated successfully",
            };
        } else {
            throw new Error("Payment processing failed");
        }
    },

    // Validate order data
    validateOrderData: (orderData) => {
        const errors = [];

        if (!orderData.customerName || orderData.customerName.trim().length < 2) {
            errors.push("Customer name must be at least 2 characters");
        }

        if (!orderData.customerPhone || !/^0\d{9}$/.test(orderData.customerPhone)) {
            errors.push("Phone number must be 10 digits starting with 0");
        }

        if (!orderData.customerEmail || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(orderData.customerEmail)) {
            errors.push("Valid email address is required");
        }

        if (!orderData.deliveryAddress || orderData.deliveryAddress.trim().length < 10) {
            errors.push("Delivery address must be at least 10 characters");
        }

        // Validate rush order eligibility
        if (orderData.isRushOrder) {
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

            if (
                !orderData.deliveryAddress.toLowerCase().includes("hà nội") ||
                !hanoiInnerCityDistricts.some((district) => orderData.deliveryAddress.toLowerCase().includes(district))
            ) {
                errors.push("Rush delivery is only available in Hanoi inner city");
            }
        }

        return {
            isValid: errors.length === 0,
            errors: errors,
        };
    },
};
