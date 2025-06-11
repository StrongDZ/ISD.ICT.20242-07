import axios from "axios";

// Create axios instance with base configuration
const api = axios.create({
  baseURL: "http://localhost:8080/",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    // Không thêm token cho các request đến file tĩnh
    if (
      config.url.includes("/favicon.ico") ||
      config.url.includes("/manifest.json")
    ) {
      return config;
    }

    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Không xử lý lỗi cho các request đến file tĩnh
    if (
      error.config?.url?.includes("/favicon.ico") ||
      error.config?.url?.includes("/manifest.json")
    ) {
      return Promise.reject(error);
    }

    if (error.response?.status === 401) {
      // Unauthorized - clear local storage and redirect to login
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      window.location.href = "/login";
    } else if (error.response?.status === 403) {
      // Forbidden - user doesn't have permission
      console.error("Access forbidden: Insufficient permissions");
    } else if (error.response?.status >= 500) {
      // Server error
      console.error(
        "Server error:",
        error.response.data?.message || "Internal server error"
      );
    } else if (error.code === "ECONNABORTED") {
      // Request timeout
      console.error("Request timeout - please try again");
    }

    return Promise.reject(error);
  }
);

export default api;
