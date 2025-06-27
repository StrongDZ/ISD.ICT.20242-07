import React, { createContext, useContext, useState, useEffect } from "react";
import { authService } from "../services/authService";

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const user = localStorage.getItem("user");
    if (token && user) {
      setUser(JSON.parse(user));
    }
    setLoading(false);
  }, []);

    const login = async (credentials) => {
        try {
            const response = await authService.login(credentials);
            const userData = {
                id: response.id,
                username: response.username,
                email: response.gmail,
                role: response.roles[0],
            };
            setUser(userData);
            localStorage.setItem("token", response.token);
            localStorage.setItem("user", JSON.stringify(userData));

            return response;
        } catch (error) {
            throw error;
        }
    };

    const logout = async () => {
        setUser(null);
        localStorage.removeItem("token");
        localStorage.removeItem("user");
    };

    const isAuthenticated = () => {
        return !!user;
    };

    const hasRole = (role) => {
        return user?.role === "ROLE_" + role;
    };

    const isAdmin = () => {
        return hasRole("ADMINISTRATOR");
    };

    const isManager = () => {
        return hasRole("PRODUCT_MANAGER");
    };

    const isCustomer = () => {
        return hasRole("CUSTOMER");
    };

    const value = {
        user,
        login,
        logout,
        isAuthenticated,
        hasRole,
        isAdmin,
        isManager,
        isCustomer,
        loading,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
