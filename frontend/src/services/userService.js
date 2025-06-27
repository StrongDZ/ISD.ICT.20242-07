import api from "./api";

export const userService = {
    // Get all users with pagination and search
    getAllUsers: async (keyword = "", page = 0, size = 20) => {
        try {
            const response = await api.get(`/user/list?keyword=${keyword}&page=${page}&size=${size}`);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to fetch users");
        }
    },

    // Get user by ID
    getUserById: async (userId) => {
        try {
            const response = await api.get(`/user/${userId}`);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to fetch user");
        }
    },

    // Create new user
    createUser: async (userData) => {
        try {
            const response = await api.post("/user/add", userData);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to create user");
        }
    },

    // Update user
    updateUser: async (userData) => {
        try {
            const response = await api.put("/user/upd", userData);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to update user");
        }
    },

    // Block user (soft delete)
    blockUser: async (userId) => {
        try {
            const response = await api.delete(`/user/block/${userId}`);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to block user");
        }
    },

    // Change user password
    changePassword: async (passwordData) => {
        try {
            const response = await api.patch("/user/change_pwd", passwordData);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to change password");
        }
    },

    // Hard delete user
    deleteUserHard: async (userId) => {
        try {
            const response = await api.delete(`/user/delete/${userId}`);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || "Failed to hard delete user");
        }
    }
}; 