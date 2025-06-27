import api from "./api";

export const authService = {
  // Login user
  login: async (credentials) => {
    try {
      const response = await api.post("/auth/login", credentials);
      if (!response.data) {
        throw new Error("Invalid response from server");
      }
      return response.data;
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error("Login failed. Please try again.");
    }
  },

  // Logout
  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },
};
