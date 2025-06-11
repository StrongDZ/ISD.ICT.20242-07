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
  // Get all mock users for development/testing
  getMockUsers: () => {
    return [
      // Customer Users
      {
        id: "customer1",
        username: "customer",
        password: "customer123",
        email: "customer@aims.com",
        role: "CUSTOMER",
        fullName: "John Customer",
        phone: "0123456789",
        address: "123 Customer Street, Hanoi, Vietnam",
        preferredDelivery: "standard",
        orderHistory: ["order_001", "order_002"],
        createdAt: "2024-01-01T00:00:00Z",
        isActive: true,
      },
      {
        id: "customer2",
        username: "jane_doe",
        password: "jane123",
        email: "jane.doe@email.com",
        role: "CUSTOMER",
        fullName: "Jane Doe",
        phone: "0987654321",
        address: "456 Main Road, Ho Chi Minh City, Vietnam",
        preferredDelivery: "rush",
        orderHistory: ["order_003"],
        createdAt: "2024-01-15T00:00:00Z",
        isActive: true,
      },
      {
        id: "customer3",
        username: "bookworm",
        password: "books123",
        email: "reader@books.com",
        role: "CUSTOMER",
        fullName: "Alice Reader",
        phone: "0345678901",
        address: "789 Literature Lane, Hanoi Inner City, Vietnam",
        preferredDelivery: "rush",
        orderHistory: ["order_004", "order_005", "order_006"],
        createdAt: "2024-02-01T00:00:00Z",
        isActive: true,
      },

      // Product Manager Users
      {
        id: "manager1",
        username: "manager",
        password: "manager123",
        email: "manager@aims.com",
        role: "PRODUCT_MANAGER",
        fullName: "Bob Manager",
        phone: "0234567890",
        address: "456 Manager Avenue, Hanoi, Vietnam",
        department: "Product Management",
        permissions: [
          "create_product",
          "update_product",
          "delete_product",
          "manage_inventory",
        ],
        employeeId: "EMP001",
        createdAt: "2024-01-01T00:00:00Z",
        isActive: true,
      },
      {
        id: "manager2",
        username: "inventory_manager",
        password: "inventory123",
        email: "inventory@aims.com",
        role: "PRODUCT_MANAGER",
        fullName: "Carol Inventory",
        phone: "0345678912",
        address: "789 Inventory Street, Hanoi, Vietnam",
        department: "Inventory Management",
        permissions: ["update_product", "manage_inventory", "view_reports"],
        employeeId: "EMP002",
        createdAt: "2024-01-10T00:00:00Z",
        isActive: true,
      },

      // Administrator Users
      {
        id: "admin1",
        username: "admin",
        password: "admin123",
        email: "admin@aims.com",
        role: "ADMINISTRATOR",
        fullName: "Charlie Administrator",
        phone: "0456789012",
        address: "789 Admin Boulevard, Hanoi, Vietnam",
        department: "System Administration",
        permissions: ["all_permissions"],
        employeeId: "ADM001",
        securityLevel: "high",
        createdAt: "2024-01-01T00:00:00Z",
        isActive: true,
      },
      {
        id: "admin2",
        username: "system_admin",
        password: "system123",
        email: "system@aims.com",
        role: "ADMINISTRATOR",
        fullName: "David System",
        phone: "0567890123",
        address: "321 System Street, Hanoi, Vietnam",
        department: "IT Operations",
        permissions: ["all_permissions"],
        employeeId: "ADM002",
        securityLevel: "high",
        createdAt: "2024-01-05T00:00:00Z",
        isActive: true,
      },
    ];
  },

  // Get mock user by role (for testing)
  getMockUser: (role = "CUSTOMER") => {
    const users = authService.getMockUsers();
    return users.find((user) => user.role === role) || users[0];
  },

  // Get user profile by ID
  getUserProfile: async (userId) => {
    try {
      const users = authService.getMockUsers();
      const user = users.find((u) => u.id === userId);

      if (!user) {
        throw new Error("User not found");
      }

      const { password, ...userProfile } = user;
      return userProfile;
    } catch (error) {
      throw new Error(error.message || "Failed to fetch user profile");
    }
  },

  // Verify token
  verifyToken: async (token) => {
    try {
      const response = await api.get("/auth/verify");
      if (!response.data) {
        throw new Error("Invalid response from server");
      }
      return response.data;
    } catch (error) {
      throw new Error("Token verification failed");
    }
  },
};
