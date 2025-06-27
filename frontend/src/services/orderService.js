import api from "./api";

export const orderService = {
  // Tạo đơn hàng không cần đăng nhập
  createOrder: async (orderData) => {
    try {
      const response = await api.post("/create-order", orderData);
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || "Không thể tạo đơn hàng"
      );
    }
  },
};
