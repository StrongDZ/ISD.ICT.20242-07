import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import { CssBaseline } from "@mui/material";
import { QueryClient, QueryClientProvider } from "react-query";

// Context Providers
import { AuthProvider } from "./contexts/AuthContext";
import { CartProvider } from "./contexts/CartContext";

// Layout Components
import Layout from "./components/Layout/Layout";

// Pages
import HomePage from "./pages/HomePage/HomePage";
import ProductsPage from "./pages/ProductsPage/ProductsPage";
import ProductDetailPage from "./pages/ProductDetailPage/ProductDetailPage";
import CartPage from "./pages/CartPage/CartPage";
import CheckoutPage from "./pages/CheckoutPage/CheckoutPage";
import OrderSuccessPage from "./pages/OrderSuccessPage/OrderSuccessPage";
import LoginPage from "./pages/LoginPage/LoginPage";

// Admin Pages
import AdminDashboard from "./pages/AdminPages/AdminDashboard";
import UserManagementPage from "./pages/AdminPages/UserManagementPage";
import PaymentSuccessPage from "./pages/PaymentPages/PaymentSuccess";
// Manager Pages
import ManagerDashboard from "./pages/ManagerPages/ManagerDashboard";
import ProductManagementPage from "./pages/ManagerPages/ProductManagementPage";
import OrderManagementPage from "./pages/ManagerPages/OrderManagementPage";

// Theme configuration
const theme = createTheme({
    palette: {
        primary: {
            main: "#1976d2",
        },
        secondary: {
            main: "#dc004e",
        },
        background: {
            default: "#f5f5f5",
        },
    },
    typography: {
        fontFamily: "Roboto, Arial, sans-serif",
    },
});

// React Query client
const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            retry: 2,
            refetchOnWindowFocus: false,
        },
    },
});

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                <AuthProvider>
                    <CartProvider>
                        <Router>
                            <Layout>
                                <Routes>
                                    {/* Public Routes */}
                                    <Route path="/" element={<HomePage />} />
                                    <Route path="/products" element={<ProductsPage />} />
                                    <Route path="/products/:id" element={<ProductDetailPage />} />
                                    <Route path="/cart" element={<CartPage />} />
                                    <Route path="/checkout" element={<CheckoutPage />} />
                                    <Route path="/order-success" element={<OrderSuccessPage />} />
                                    <Route path="/login" element={<LoginPage />} />

                                    {/* Admin Routes */}
                                    <Route path="/admin" element={<AdminDashboard />} />
                                    <Route path="/admin/users" element={<UserManagementPage />} />

                                    {/* Manager Routes */}
                                    <Route path="/manager" element={<ManagerDashboard />} />
                                    <Route path="/manager/products" element={<ProductManagementPage />} />
                                    <Route path="/manager/orders" element={<OrderManagementPage />} />
                                    <Route path="/payment-success" element={<PaymentSuccessPage />} />

                                </Routes>
                            </Layout>
                        </Router>
                    </CartProvider>
                </AuthProvider>
            </ThemeProvider>
        </QueryClientProvider>
    );
}

export default App;
