import axios from "axios";

// Create axios instance with base configuration
const api = axios.create({
    baseURL: "http://localhost:8080/api",
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    },
});

// Request interceptor to add auth token and log requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        // Log request details
        console.log("🚀 REQUEST:", {
            method: config.method?.toUpperCase(),
            url: config.url,
            baseURL: config.baseURL,
            headers: config.headers,
            data: config.data,
            params: config.params,
        });

        return config;
    },
    (error) => {
        console.error("❌ REQUEST ERROR:", error);
        return Promise.reject(error);
    }
);

// Response interceptor to handle errors and log responses
api.interceptors.response.use(
    (response) => {
        // Log successful response
        console.log("✅ RESPONSE:", {
            status: response.status,
            statusText: response.statusText,
            url: response.config.url,
            method: response.config.method?.toUpperCase(),
            data: response.data,
            headers: response.headers,
        });

        return response;
    },
    (error) => {
        // Log error response
        console.error("❌ RESPONSE ERROR:", {
            status: error.response?.status,
            statusText: error.response?.statusText,
            url: error.config?.url,
            method: error.config?.method?.toUpperCase(),
            data: error.response?.data,
            message: error.message,
        });

        if (error.response?.status === 403) {
            // Forbidden - user doesn't have permission
            console.error("Access forbidden: Insufficient permissions");
        } else if (error.response?.status >= 500) {
            // Server error
            console.error("Server error:", error.response.data?.message || "Internal server error");
        } else if (error.code === "ECONNABORTED") {
            // Request timeout
            console.error("Request timeout - please try again");
        }

        return Promise.reject(error);
    }
);

export default api;
