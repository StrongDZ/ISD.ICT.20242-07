import api from "./api";

export const orderService = {
    // Tạo đơn hàng không cần đăng nhập
    createOrder: async (orderData) => {
        try {
            const response = await api.post("/create-order", orderData);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Không thể tạo đơn hàng");
        }
    },

    // Kiểm tra tính đủ điều kiện rush order
    checkRushOrderEligibility: async (deliveryInfo, products) => {
        try {
            const response = await api.post("/rush-order/check", {
                deliveryInfo,
                products
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Không thể kiểm tra rush order");
        }
    },

    async getAllOrders() {
        const response = await api.get("/orders");
        return response.data;
    },

    // Approve order
    async approveOrder(orderId, managerId = "MAN001") {
        const response = await api.post(`/manager/orders/${orderId}/approve?managerId=${managerId}`);
        return response.data;
    },

    // Reject order
    async rejectOrder(orderId, reason, managerId = "MAN001") {
        const response = await api.post(`/manager/orders/${orderId}/reject?managerId=${managerId}`, { reason });
        return response.data;
    },

    // Mock orders for development/testing
    getMockOrders: () => {
        return [
            {
                orderID: "ORD001",
                customerName: "Nguyen Van A",
                customerEmail: "nguyenvana@email.com",
                status: "PENDING_APPROVAL",
                orderDate: "2024-01-15",
                totalAmount: 850000,
                shippingAddress: "123 Le Loi, District 1, Ho Chi Minh City",
                items: [
                    { productName: "Clean Code", quantity: 1, price: 450000 },
                    { productName: "The Great Gatsby", quantity: 2, price: 200000 },
                ],
            },
            {
                orderID: "ORD002",
                customerName: "Tran Thi B",
                customerEmail: "tranthib@email.com",
                status: "PROCESSING",
                orderDate: "2024-01-14",
                totalAmount: 1200000,
                shippingAddress: "456 Nguyen Hue, District 3, Ho Chi Minh City",
                items: [
                    { productName: "Effective Java", quantity: 1, price: 520000 },
                    { productName: "Bohemian Rhapsody CD", quantity: 2, price: 340000 },
                ],
            },
            {
                orderID: "ORD003",
                customerName: "Le Van C",
                customerEmail: "levanc@email.com",
                status: "PENDING_APPROVAL",
                orderDate: "2024-01-13",
                totalAmount: 680000,
                shippingAddress: "789 Tran Hung Dao, District 5, Ho Chi Minh City",
                items: [
                    { productName: "Sapiens", quantity: 1, price: 380000 },
                    { productName: "Avatar DVD", quantity: 1, price: 300000 },
                ],
            },
            {
                orderID: "ORD004",
                customerName: "Pham Thi D",
                customerEmail: "phamthid@email.com",
                status: "SHIPPING",
                orderDate: "2024-01-12",
                totalAmount: 920000,
                shippingAddress: "321 Vo Van Tan, District 3, Ho Chi Minh City",
                items: [
                    { productName: "Design Patterns", quantity: 1, price: 580000 },
                    { productName: "The Beatles Collection", quantity: 1, price: 340000 },
                ],
            },
            {
                orderID: "ORD005",
                customerName: "Hoang Van E",
                customerEmail: "hoangvane@email.com",
                status: "DELIVERED",
                orderDate: "2024-01-11",
                totalAmount: 760000,
                shippingAddress: "654 Hai Ba Trung, District 1, Ho Chi Minh City",
                items: [{ productName: "React Handbook", quantity: 2, price: 380000 }],
            },
            {
                orderID: "ORD006",
                customerName: "Vu Thi F",
                customerEmail: "vuthif@email.com",
                status: "PENDING_APPROVAL",
                orderDate: "2024-01-10",
                totalAmount: 1150000,
                shippingAddress: "987 Dien Bien Phu, District Binh Thanh, Ho Chi Minh City",
                items: [
                    { productName: "Spring Boot Guide", quantity: 1, price: 650000 },
                    { productName: "Classical Music Collection", quantity: 1, price: 500000 },
                ],
            },
        ];
    },
};
